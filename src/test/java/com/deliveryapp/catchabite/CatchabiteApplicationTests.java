package com.deliveryapp.catchabite;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class CatchabiteApplicationTests {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createLoginDummyAccount() {
        String email = "dummy@example.com";
        String mobile = "01012345678";
        String nickname = "dummy";
        String name = "Dummy User";
        String rawPassword = "Passw0rd!";

        boolean exists = appUserRepository.existsByAppUserEmail(email)
            || appUserRepository.existsByAppUserMobile(mobile)
            || appUserRepository.existsByAppUserNickname(nickname);

        if (exists) {
            return;
        }

        AppUser user = AppUser.builder()
            .appUserEmail(email)
            .appUserPassword(passwordEncoder.encode(rawPassword))
            .appUserNickname(nickname)
            .appUserMobile(mobile)
            .appUserName(name)
            .appUserCreatedDate(LocalDateTime.now())
            .build();

        appUserRepository.save(user);
    }

    @Test
    void contextLoads() {
    }

}

	


