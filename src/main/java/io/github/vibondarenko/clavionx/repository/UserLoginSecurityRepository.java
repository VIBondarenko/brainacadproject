package io.github.vibondarenko.clavionx.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.entity.UserLoginSecurity;

public interface UserLoginSecurityRepository extends JpaRepository<UserLoginSecurity, Long> {
    Optional<UserLoginSecurity> findByUser(User user);
}
