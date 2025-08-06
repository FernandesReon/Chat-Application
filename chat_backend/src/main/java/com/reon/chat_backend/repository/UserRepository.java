package com.reon.chat_backend.repository;

import com.reon.chat_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);    // useful during registration
    Optional<User> findByEmail(String email);
}
