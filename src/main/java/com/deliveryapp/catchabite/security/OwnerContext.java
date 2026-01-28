package com.deliveryapp.catchabite.security;

import com.deliveryapp.catchabite.entity.StoreOwner;
import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.security.Principal;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
public class OwnerContext {

    private final StoreOwnerRepository storeOwnerRepository;
    private final DataSource dataSource;

    public Long requireStoreOwnerId(Principal principal) {
        // ✅ 스프링이 실제로 붙은 DB를 콘솔에 찍기 (진단용)
        try (Connection c = dataSource.getConnection()) {
            System.out.println("[ownercontext] jdbc url  = " + c.getMetaData().getURL());
            System.out.println("[ownercontext] jdbc user = " + c.getMetaData().getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new IllegalStateException("unauthenticated store owner");
        }

        final String raw = principal.getName();

        String normalized = raw.trim();
        int colonIndex = normalized.indexOf(':');
        if (colonIndex >= 0) {
            normalized = normalized.substring(colonIndex + 1).trim();
        }

        final String email = normalized;

        final StoreOwner owner = storeOwnerRepository.findByStoreOwnerEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "store owner not found: " + email + " (raw=" + raw + ")"
                ));

        return owner.getStoreOwnerId();
    }
}
