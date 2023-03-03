package com.example.colortasks.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {

    private final JavaMailSender mailSender;

    public MailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMailMessage(String toEmail, String subject, String username, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ivan.leshchyshynnn@gmail.com");
        message.setTo(toEmail);
        message.setText("Hello " + username + "! Use this code to change your password (" + code + ") ");
        message.setSubject(subject);
        mailSender.send(message);
    }
}
