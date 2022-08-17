package com.tiagolivrera.cursomc.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tiagolivrera.cursomc.security.JWTAuthenticationFilter;
import com.tiagolivrera.cursomc.security.JWTAuthorizationFilter;
import com.tiagolivrera.cursomc.security.JWTUtil;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private Environment env;

    @Autowired
    private JWTUtil jwtUtil;

    private static final String[] PUBLIC_MATCHERS = {
        "/h2-console/**"        
    };

    // acesso apenas para leitura
    private static final String[] PUBLIC_MATCHERS_GET = {
        "/produtos/**",
        "/categorias/**"
    };

    private static final String[] PUBLIC_MATCHERS_POST = {
        "/clientes", // O cliente pode se cadastrar no sistema
        "/clientes/picture",
        "/auth/forgot/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // habilita o acesso ao h2-console no profile test
        if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.headers().frameOptions().disable();
        }

        // cors() -- chama corsConfigurationSource()
        // csrf().disable() -- desabilita a protecao para csrf, visto que o sistema e stateless -- nao armazena dados de secao
        http.cors().and().csrf().disable();
        http.authorizeRequests()
            .antMatchers(HttpMethod.GET, PUBLIC_MATCHERS_GET).permitAll()
            .antMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll()
            .antMatchers(PUBLIC_MATCHERS).permitAll() // permite acesso aos endpoints registrados em PUBLIC_MATCHERS
            .anyRequest().authenticated();  // para os demais endpoints, solicita autenticacao
        // stateless -- garante que o backend nao vai criar uma secao de usuario
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtUtil));
        http.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtUtil, userDetailsService));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        // habilita o acesso para funcionalidades basicas -- util para testar a aplicacao
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }    
}
