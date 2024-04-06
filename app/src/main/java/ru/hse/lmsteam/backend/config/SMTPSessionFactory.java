package ru.hse.lmsteam.backend.config;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SMTPSessionFactory {
  private final Properties sessionProperties;
  private final Authenticator serverAuthenticator;
  private final boolean inDebugMode;

  public SMTPSessionFactory(
      @Value("${mail.smtp.auth}") String auth,
      @Value("${mail.smtp.ssl.port}") String sslPort,
      @Value("${mail.smtp.host}") String host,
      @Value("${mail.smtp.port}") String port,
      @Value("${mail.smtp.username}") String username,
      @Value("${mail.smtp.password}") String password,
      @Value("${mail.smtp.debug}") boolean debug) {
    var smtpProperties = new Properties();
    smtpProperties.put("mail.smtp.auth", auth);
    smtpProperties.put("mail.smtp.host", host);
    smtpProperties.put("mail.smtp.port", port);
    smtpProperties.put("mail.smtp.socketFactory.port", sslPort);
    smtpProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

    this.sessionProperties = smtpProperties;
    this.inDebugMode = debug;

    this.serverAuthenticator =
        new Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        };
  }

  public Session newSession() {
    var session = Session.getInstance(sessionProperties, serverAuthenticator);
    session.setDebug(inDebugMode);
    return session;
  }
}
