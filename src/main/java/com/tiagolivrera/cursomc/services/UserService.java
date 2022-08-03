package com.tiagolivrera.cursomc.services;

import org.springframework.security.core.context.SecurityContextHolder;

import com.tiagolivrera.cursomc.security.UserSS;

public class UserService {
    
    public static UserSS authenticated() {
        // retorna o usuario que esta logado no sistema
        try {
            return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }        
    }
}