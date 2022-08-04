package com.tiagolivrera.cursomc.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tiagolivrera.cursomc.domain.Cliente;
import com.tiagolivrera.cursomc.repositories.ClienteRepository;
import com.tiagolivrera.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {

    @Autowired
    private BCryptPasswordEncoder pe;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmailService emailService;

    private Random random = new Random();

    public void sendNewPassword(String email) {

        Cliente cliente = clienteRepository.findByEmail(email);

        if(cliente == null) {
            throw new ObjectNotFoundException("Email nao encontrado");
        }

        String newPass = newPassword();
        cliente.setSenha(pe.encode(newPass));

        clienteRepository.save(cliente);
        emailService.sendNewPasswordEmail(cliente, newPass);
    }

    private String newPassword() {
        char[] vet = new char[10];
        for (int i = 0; i < 10; i++) {
            vet[i] = randomChar();
        }
        return new String(vet);
    }

    private char randomChar() {
        int opt = random.nextInt(3); // escolhe entre digito, letra maiuscula ou minuscula (pela tabela unicode)
        if (opt == 0) { // gera digito
            return (char) (random.nextInt(10) + 48); // 48 e a posicao do numero zero na tabela unicode
        } else if (opt == 1) { // gera letra maiuscula
            return (char) (random.nextInt(26) + 65);
        } else { // gera letra minuscula
            return (char) (random.nextInt(26) + 97);
        }
    }    
}
