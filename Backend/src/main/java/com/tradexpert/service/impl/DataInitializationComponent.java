package com.tradexpert.service.impl;

import com.tradexpert.domain.USER_ROLE;


import com.tradexpert.model.User;
import com.tradexpert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;


    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializationComponent(UserRepository userRepository,
                                       PasswordEncoder passwordEncoder
                                       ) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;

    }

    @Override
    public void run(String... args) {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        String adminUsername = "tradexpert360@gmail.com";

        if (userRepository.findByEmail(adminUsername)==null) {
            User adminUser = new User();

            adminUser.setPassword(passwordEncoder.encode("tradexpert123@#$"));
            adminUser.setFullName("Vinay Dahariya");
            adminUser.setEmail(adminUsername);
            adminUser.setRole(USER_ROLE.ROLE_ADMIN);
            User admin=userRepository.save(adminUser);
        }
    }

}
