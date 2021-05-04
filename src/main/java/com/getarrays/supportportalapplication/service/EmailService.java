package com.getarrays.supportportalapplication.service;


import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.getarrays.supportportalapplication.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

@Service
public class EmailService {

    public void sendNewPasswordEmail(String firstName, String password, String email) throws MessagingException {
        Message message = createEmail(firstName, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false)); //strict is false
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Hello " + firstName + ", \n \n Your new account password is: " + password + "\n \n The Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Session getEmailSession() {
        Properties properties = System.getProperties(); //once we get the properties we can set our email constant & use them to create the session
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);   //coming from email constant, passing in our host
        properties.put(SMTP_AUTH, true);    //setting authentication to true
        properties.put(SMTP_PORT, DEFAULT_PORT);    //setting the port #
        properties.put(SMTP_STARTTLS_ENABLE, true); //enabling transport layer security
        properties.put(SMTP_STARTTLS_REQUIRED, true);   //transport layer security is required, if tls doesn't start it will fail. Have to make sure it starts
        return Session.getInstance(properties, null);   //authentication is null
    }
}
