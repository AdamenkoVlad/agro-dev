package com.abi.agro_back.config;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class MailSender {
    @Value("${mail.smtps.host}")
    String host;
    @Value("${mail.transport.protocol}")
    String protocol;
    @Value("${mail.smtps.auth}")
    String auth;
    @Value("${mail.smtps.user}")
    String emailUser;
    @Value("${mail.password}")
    String emailPassword;
    @Value("${contextPath}")
    String contextPath;

    public void sendEmail(String email, String password){
        String recipient = email;

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtps.host", host);
        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.smtps.auth", auth);
        properties.setProperty("mail.smtps.user", emailUser);

        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUser));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Agro credentials!");

            message.setContent("<h1>Congratulations</h1>" +
                    "<p>This is your login </p>" + email +
                    "<p>This is your password</p>" + password, "text/html");

            Transport tr = session.getTransport();
            tr.connect(emailUser, emailPassword);
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public void sendResetEmail(String token, String email) {

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtps.host", host);
        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.smtps.auth", auth);
        properties.setProperty("mail.smtps.user", emailUser);

        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUser));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Agro reset password!");
            String url = "<a href='"+ contextPath + "/savePassword?token=" + token + "'>Link for reset password</a>";
            message.setContent("<h1>Your link for reset password!</h1>" +
                    "<p>This is your link </p>" + "\r\n"+ url, "text/html");

            Transport tr = session.getTransport();
            tr.connect(emailUser, emailPassword);
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
}
