package com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.MimeMessageHelper;

class SendEmailTest {

  private SendEmail sendEmail;

  private MimeMessage mimeMessage;

  @Before
  public void before() {
    sendEmail = new SendEmail();
  }

  @Test
  public void emailTest() throws MessagingException {
    final String username = System.getenv("EMAIL_USERNAME");
    final String password = System.getenv("EMAIL_PASSWORD");
    final String host = System.getenv("EMAIL_SMTP_SERVER");
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", "2525");
    Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });
    mimeMessage = new MimeMessage((session));
    String recipient = "example@example.com";
    MimeMessageHelper request =
        new MimeMessageHelper(mimeMessage, "utf-8");
    request.setTo(recipient);
    request.setText("text");
    Transport.send(mimeMessage);
    assertEquals(recipient, mimeMessage.getRecipients(RecipientType.TO)[0].toString());
  }
}
