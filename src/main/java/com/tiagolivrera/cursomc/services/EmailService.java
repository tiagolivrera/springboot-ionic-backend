package com.tiagolivrera.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.tiagolivrera.cursomc.domain.Pedido;

public interface EmailService {

    void sendOrderConfirmationEmail(Pedido obj);

    void sendEmail(SimpleMailMessage msg);
    
}
