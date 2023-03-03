package com.example.colortasks;


import com.example.colortasks.entity.User;
import com.example.colortasks.service.MailSenderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RunWith(MockitoJUnitRunner.class)
public class MailSenderServiceTest {

    @InjectMocks
    private MailSenderService mailSenderService;

    @Mock
    private JavaMailSender mailSender;

    @Test
    public void sendMailMessageSuccess(){
        User user = new User();
        user.setCodeFromEmail("654321");
        user.setEmail("kroasavbbqaz@gmail.com");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ivan.leshchyshynnn@gmail.com");
        message.setTo(user.getEmail());
        message.setText("Hello " + "Some name" + "! Use this code to change your password (" + user.getCodeFromEmail() + ") ");
        message.setSubject("Here is the subject");
        assertDoesNotThrow(() -> mailSenderService
                .sendMailMessage(message.getFrom(), message.getSubject(),user.getUsername(), user.getCodeFromEmail()));
    }


}
