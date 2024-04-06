package ru.hse.lmsteam.backend.service.mail.infra;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.hse.lmsteam.backend.config.SMTPSessionFactory;

@Slf4j
@Service
public class EmailSenderImpl implements EmailSender {
  private final String fromEmail;
  private final SMTPSessionFactory sessionFactory;
  private final boolean enableSending;

  public EmailSenderImpl(
      @Value("${mail.from}") String fromEmail,
      @Value("${mail.sending.enabled}") boolean enableSending,
      @Autowired SMTPSessionFactory sessionFactory)
      throws AddressException {
    InternetAddress.parse(fromEmail);
    this.fromEmail = fromEmail;
    this.sessionFactory = sessionFactory;
    this.enableSending = enableSending;
  }

  @Override
  public Message getMessageTemplate(Collection<String> targetEmails) {
    try {
      var message = new MimeMessage(sessionFactory.newSession());
      message.setFrom(fromEmail);

      var targets =
          targetEmails.stream()
              .flatMap(
                  email -> {
                    try {
                      return Arrays.stream(InternetAddress.parse(email));
                    } catch (AddressException e) {
                      throw new IllegalArgumentException("Invalid email target address", e);
                    }
                  })
              .toArray(InternetAddress[]::new);
      message.setRecipients(Message.RecipientType.TO, targets);
      return message;
    } catch (MessagingException e) {
      throw new IllegalArgumentException("Invalid email target addresses", e);
    }
  }

  @Override
  public void send(Message message) throws MessagingException {
    if (!enableSending) {
      log.info("Skip sending email because sending is disabled");
      return;
    }
    Transport.send(message);
  }
}
