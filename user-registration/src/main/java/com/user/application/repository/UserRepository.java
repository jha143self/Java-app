package com.user.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.user.application.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Override
    void delete(User user);

}