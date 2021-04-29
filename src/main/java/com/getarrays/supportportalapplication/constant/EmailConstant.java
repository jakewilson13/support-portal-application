package com.getarrays.supportportalapplication.constant;

public class EmailConstant {
    public static final String SIMPLE_MAIL_TRANSFER_PROTOCOL = "smtps"; //calling the protocall
    public static final String USERNAME = "";    //will be email
    public static final String PASSWORD = "";  //remember to enable less secure app
    public static final String FROM_EMAIL = "support@getarrays.com";
    public static final String CC_EMAIL = "";
    public static final String EMAIL_SUBJECT = "Get Arrays, LLC - New Password";
    public static final String GMAIL_SMTP_SERVER = "smtp.gmail.com";    //mail server (gmail server)
    public static final String SMTP_HOST = "mail.smtp.host";    //host
    public static final String SMTP_AUTH = "mail.smtp.auth";    //authentication
    public static final String SMTP_PORT = "mail.smtp.port";
    public static final int DEFAULT_PORT = 465; //default port
    public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";  //going to set these to true in email service
    public static final String SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";  //TLS - Transport Layer Security

}
