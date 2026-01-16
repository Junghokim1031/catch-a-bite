package com.deliveryapp.catchabite.security;

import com.deliveryapp.catchabite.repository.StoreOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * Resolve the current store owner's id from the authenticated principal.
 *
 * <p>This keeps the Owner controllers aligned with the team's session-based auth style
 * (principal comes from the login session), instead of requiring a client-provided header.
 */
@Component
@RequiredArgsConstructor
public class OwnerContext {

    private final StoreOwnerRepository storeOwnerRepository;

    public Long requireStoreOwnerId(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new IllegalStateException("unauthenticated store owner");
        }

        // By convention in this project, principal name is the owner's email.
        String email = principal.getName();

        return storeOwnerRepository.findByStoreOwnerEmail(email)
                .orElseThrow(() -> new IllegalStateException("store owner not found: " + email))
                .getStoreOwnerId();
    }
}
