package com.deliveryapp.catchabite;
import com.deliveryapp.catchabite.domain.enumtype.DelivererVehicleType;
import com.deliveryapp.catchabite.domain.enumtype.YesNo;
import com.deliveryapp.catchabite.entity.AppUser;
import com.deliveryapp.catchabite.entity.Deliverer;
import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.AppUserRepository;
import com.deliveryapp.catchabite.repository.DelivererRepository;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
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
    private StoreOwnerRepository storeOwnerRepository;

    @Autowired
    private DelivererRepository delivererRepository;

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
    void createOwnerLoginDummyAccount() {
        String email = "owner@example.com";
        String mobile = "01023456789";
        String name = "Dummy Owner";
        String rawPassword = "Passw0rd!";
        String businessRegistrationNo = "123-45-67890";

        boolean exists = storeOwnerRepository.existsByStoreOwnerEmail(email)
            || storeOwnerRepository.existsByStoreOwnerMobile(mobile)
            || storeOwnerRepository.existsByStoreOwnerBusinessRegistrationNo(businessRegistrationNo);

        if (exists) {
            return;
        }

        StoreOwner owner = StoreOwner.builder()
            .storeOwnerEmail(email)
            .storeOwnerPassword(passwordEncoder.encode(rawPassword))
            .storeOwnerName(name)
            .storeOwnerMobile(mobile)
            .storeOwnerBusinessRegistrationNo(businessRegistrationNo)
            .createdAt(LocalDateTime.now())
            .build();

        storeOwnerRepository.save(owner);
    }

    @Test
    void createDelivererLoginDummyAccount() {
        String email = "deliverer@example.com";
        String rawPassword = "Passw0rd!";

        boolean exists = delivererRepository.existsByDelivererEmail(email);
        if (exists) {
            return;
        }

        Deliverer deliverer = Deliverer.builder()
            .delivererEmail(email)
            .delivererPassword(passwordEncoder.encode(rawPassword))
            .delivererVehicleType(DelivererVehicleType.WALKING)
            .delivererVerified(YesNo.N)
            .delivererCreatedDate(LocalDateTime.now())
            .build();

        delivererRepository.save(deliverer);
    }

    @Test
    void contextLoads() {
    }

}

	


