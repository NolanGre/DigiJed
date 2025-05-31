package org.example.digijed.repositories;

import org.example.digijed.models.AuthProvider;
import org.example.digijed.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> getUserByEmail(String email);

    Optional<User> findByAuthProviderAndProviderId(AuthProvider provider, String providerId);
}
