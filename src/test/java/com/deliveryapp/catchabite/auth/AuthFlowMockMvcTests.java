package com.deliveryapp.catchabite.auth;

import com.deliveryapp.catchabite.domain.enumtype.DelivererVehicleType;
import com.deliveryapp.catchabite.domain.enumtype.YesNo;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.repository.DelivererRepository;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthFlowMockMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private StoreOwnerRepository storeOwnerRepository;

    @Autowired
    private DelivererRepository delivererRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void apiUserSignupSuccess() throws Exception {
        String email = uniqueEmail("user");
        String mobile = uniqueMobile();
        String nickname = "nick" + uniqueSuffix();
        String name = "Test User";
        String password = "Passw0rd!";

        String payload = buildUserSignupPayload(email, mobile, nickname, name, password, true);

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").isNumber())
            .andExpect(jsonPath("$.loginId").value(email));
    }

    @Test
    void apiUserSignupBadRequest() throws Exception {
        String email = uniqueEmail("user");
        String mobile = uniqueMobile();
        String nickname = "nick" + uniqueSuffix();
        String name = "Test User";
        String password = "Passw0rd!";

        String payload = buildUserSignupPayload(email, mobile, nickname, name, password, false);

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void apiUserLoginSuccess() throws Exception {
        String email = uniqueEmail("user");
        String mobile = uniqueMobile();
        String nickname = "nick" + uniqueSuffix();
        String name = "Test User";
        String password = "Passw0rd!";

        AppUser user = createUser(email, mobile, nickname, name, password);

        Map<String, Object> payload = new HashMap<>();
        payload.put("loginKey", email);
        payload.put("password", password);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(user.getAppUserId()));
    }

    @Test
    void apiUserLoginUnauthorized() throws Exception {
        String email = uniqueEmail("user");
        String mobile = uniqueMobile();
        String nickname = "nick" + uniqueSuffix();
        String name = "Test User";
        String password = "Passw0rd!";

        createUser(email, mobile, nickname, name, password);

        Map<String, Object> payload = new HashMap<>();
        payload.put("loginKey", email);
        payload.put("password", "WrongPass1!");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void apiUserMeWithoutLoginReturnsUnauthorizedJson() throws Exception {
        mockMvc.perform(get("/api/v1/user/me"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void apiUserProfileRequiresUserRole() throws Exception {
        String email = uniqueEmail("user");
        String mobile = uniqueMobile();
        String nickname = "nick" + uniqueSuffix();
        String name = "Test User";
        String password = "Passw0rd!";
        createUser(email, mobile, nickname, name, password);

        MockHttpSession session = loginUserSession(email, password);

        mockMvc.perform(get("/api/v1/user/profile").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.path").value("/api/v1/user/profile"));

        mockMvc.perform(get("/api/v1/rider/ping").session(session))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void apiRiderPingRequiresRiderRole() throws Exception {
        String email = uniqueEmail("rider");
        String password = "Passw0rd!";
        createRider(email, password);

        MockHttpSession session = loginRiderSession(email, password);

        mockMvc.perform(get("/api/v1/rider/ping").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.path").value("/api/v1/rider/ping"));
    }

    private String buildUserSignupPayload(
        String email,
        String mobile,
        String nickname,
        String name,
        String password,
        boolean requiredTerms
    ) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("loginId", email);
        payload.put("mobile", mobile);
        payload.put("password", password);
        payload.put("confirmPassword", password);
        payload.put("nickname", nickname);
        payload.put("name", name);
        payload.put("requiredTermsAccepted", requiredTerms);
        payload.put("marketingTermsAccepted", false);
        return objectMapper.writeValueAsString(payload);
    }

    private AppUser createUser(String email, String mobile, String nickname, String name, String rawPassword) {
        AppUser user = AppUser.builder()
            .appUserEmail(email)
            .appUserPassword(passwordEncoder.encode(rawPassword))
            .appUserNickname(nickname)
            .appUserMobile(mobile)
            .appUserName(name)
            .appUserCreatedDate(LocalDateTime.now())
            .build();
        return appUserRepository.save(user);
    }

    private StoreOwner createOwner(String email, String name, String mobile, String rawPassword) {
        StoreOwner owner = StoreOwner.builder()
            .storeOwnerEmail(email)
            .storeOwnerPassword(passwordEncoder.encode(rawPassword))
            .storeOwnerName(name)
            .storeOwnerMobile(mobile)
            .storeOwnerBusinessRegistrationNo("BRN-" + uniqueSuffix())
            .createdAt(LocalDateTime.now())
            .build();
        return storeOwnerRepository.save(owner);
    }

    private Deliverer createRider(String email, String rawPassword) {
        Deliverer rider = Deliverer.builder()
            .delivererEmail(email)
            .delivererPassword(passwordEncoder.encode(rawPassword))
            .delivererVehicleType(DelivererVehicleType.WALKING)
            .delivererVerified(YesNo.N)
            .delivererCreatedDate(LocalDateTime.now())
            .build();
        return delivererRepository.save(rider);
    }

    private MockHttpSession loginUserSession(String email, String password) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("loginKey", email);
        payload.put("password", password);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertThat(session).isNotNull();
        return session;
    }

    private MockHttpSession loginRiderSession(String email, String password) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", email);
        payload.put("password", password);

        MvcResult result = mockMvc.perform(post("/api/v1/deliverer/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertThat(session).isNotNull();
        return session;
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private String uniqueEmail(String prefix) {
        return prefix + uniqueSuffix() + "@test.com";
    }

    private String uniqueMobile() {
        long value = Math.abs(System.nanoTime() % 100000000L);
        return "010" + String.format("%08d", value);
    }
}
