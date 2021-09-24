package com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken;

import com.greenfoxacademy.zelenamackatribes.users.exceptions.SendValidationEmailException;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SendEmail {

  private static final Logger logger = LogManager.getLogger(SendEmail.class);

  public static void sendMail(String userEntityName, String kingdomName, String recepient,
      String link) throws SendValidationEmailException, MessagingException {

    String from = System.getenv("EMAIL_SENDER");
    final String username =  System.getenv("EMAIL_USERNAME");
    final String password =  System.getenv("EMAIL_PASSWORD");

    String host = System.getenv("EMAIL_SMTP_SERVER");
    String port = System.getenv("EMAIL_SMTP_PORT");

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port);

    Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });

    try {
      MimeMessage mimeMessage = new MimeMessage(session);
      MimeMessageHelper helper =
          new MimeMessageHelper(mimeMessage, "utf-8");
      helper.setTo(recepient);
      helper.setSubject("Confirm your email");
      helper.setFrom(new InternetAddress(from));
      helper.setText(buildEmail(userEntityName, kingdomName, link), true);

      Transport.send(mimeMessage);
      logger.info("Validation email for: " + userEntityName + " sent");
      System.out.println("Sent message successfully....");

    } catch (AddressException e) {
      e.printStackTrace();
      logger.info("Failed to send email: " + e.getMessage());
      throw new SendValidationEmailException(e.getMessage());
    }
  }

  private static String buildEmail(String name, String kingdomName, String link) {
    return "Welcome " + name + "!  <br>" + kingdomName
        + " is ready! <br>You just need to confirm your email address and then you are ready to "
        + "conquer the world :)  \n"
        + " Please confirm your email address by clicking this button: <br> <a href=\""
        + link
        + "\">Activate Now</a> <br>"
        + " — The Tribes Team <br>"
        + " -----------------------------------------------------------------------------------<br>"
        +
        "Welcome " + name + "!  <br>" + kingdomName
        + " is ready! <br> You just need to confirm your email address and then you are ready "
        + "to conquer the world :)  \n"
        + " Please confirm your email address visiting next link: <br>"
        + link
        + "<br>"
        + " — The Tribes Team \n";
  }
}
