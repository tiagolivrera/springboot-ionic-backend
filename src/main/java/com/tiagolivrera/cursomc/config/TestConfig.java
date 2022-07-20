package com.tiagolivrera.cursomc.config;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tiagolivrera.cursomc.services.DBService;

//@Configuration
@Component
@Profile("test")
public class TestConfig {

    @Autowired
    private DBService dbService;

    @Bean
    public boolean instantiateDatabase() throws ParseException {
        dbService.instantiateTestDatabase();
        return true;
    }    
}

/*
 * OBS.: Mudança na criação de profiles a partir da versão 2.4 do Spring Boot
 * Acrescentar o profile dentro de spring-boot-maven-plugin em pom.xml
 * Consulte: https://www.baeldung.com/spring-profiles
 */