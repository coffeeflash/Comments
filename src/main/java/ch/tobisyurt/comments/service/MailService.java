package ch.tobisyurt.comments.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${mail.notification.admin}")
    private String adminMail;

    public void sendMailNotificationToAdmin(String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("info@tobisyurt.net");
        message.setTo(adminMail);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

}
