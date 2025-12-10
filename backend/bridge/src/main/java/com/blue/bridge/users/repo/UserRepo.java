package com.blue.bridge.users.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blue.bridge.users.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
