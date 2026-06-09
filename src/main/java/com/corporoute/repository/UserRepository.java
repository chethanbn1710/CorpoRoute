package com.corporoute.repository;

import com.corporoute.entity.User;
import com.corporoute.enums.Role;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRoleAndAvailable(Role role, Boolean available);
}

