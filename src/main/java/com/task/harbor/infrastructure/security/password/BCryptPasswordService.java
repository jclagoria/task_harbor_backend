package com.task.harbor.infrastructure.security.password;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BCryptPasswordService {

    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordService(@Value("${app.security.bcrypt-strength:14}") int strength) {
        this.encoder = new BCryptPasswordEncoder(strength);
    }

    public String hashPassword(String password) {
        return encoder.encode(password);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}