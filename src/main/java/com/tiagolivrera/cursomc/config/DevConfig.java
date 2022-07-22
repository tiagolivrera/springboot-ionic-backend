package com.tiagolivrera.cursomc.config;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tiagolivrera.cursomc.services.DBService;
import com.tiagolivrera.cursomc.services.EmailService;
import com.tiagolivrera.cursomc.services.SmtpEmailService;

@Component
@Profile("dev")
public class DevConfig {

    @Autowired
    private DBService dbService;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String strategy;

    @Bean
    public boolean instantiateDatabase() throws ParseException {

        if (!"create".equals(strategy)) {
            return false;
        }
        
        dbService.instantiateTestDatabase();
        return true;
    }
    
    @Bean
    public EmailService emailService() {
        return new SmtpEmailService();
    }
}

/*
 * OBS.: Mudança na criação de profiles a partir da versão 2.4 do Spring Boot
 * Acrescentar o profile dentro de spring-boot-maven-plugin em pom.xml
 * Consulte: https://www.baeldung.com/spring-profiles
 */