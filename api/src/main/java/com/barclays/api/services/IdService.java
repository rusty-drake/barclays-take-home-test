package com.barclays.api.services;

import org.springframework.stereotype.Service;

@Service
public class IdService {

    public String generateId(String prefix) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        char randomChar = chars.charAt((int) (Math.random() * chars.length()));
        return prefix + "-" + randomChar;
    }
    
}
