package com.tiagolivrera.cursomc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SmtpEmailService extends AbstractEmailService {

    @Autowired
    private MailSender mailSender; // o framework instancia automaticamente um MailSender com os dados em
                                   // application-dev.properties

    private static final Logger LOG = LoggerFactory.getLogger(SmtpEmailService.class);

    @Override
    public void sendEmail(SimpleMailMessage msg) {
        LOG.info("Enviando email...");
        mailSender.send(msg);
        LOG.info("Email enviado");
    }

}
