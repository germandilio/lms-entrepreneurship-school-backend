package ru.hse.lmsteam.backend.service.mail;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.mail.infra.EmailSender;

@Component
@RequiredArgsConstructor
@Slf4j
public class SetNewPasswordEmailSender {
  private final EmailSender emailSender;

  public CompletableFuture<Void> sendEmail(String targetEmail, String token) {
    return CompletableFuture.supplyAsync(
        () -> {
          sendEmail(emailSender, targetEmail, token);
          return null;
        });
  }

  private void sendEmail(EmailSender sender, String targetEmail, String token) {
    log.info("Sending new passport email to user {} with token {}", targetEmail, token);
    try {
      var message = sender.getMessageTemplate(Set.of(targetEmail));
      if (message == null) {
        log.info("Skip on null message");
        return;
      }
      message.setSubject("Set new password");

      String msg =
          "To set password follow this link: <a href=\"https://google.com/\">Set password</a>.\nToken = "
              + token;

      BodyPart mimeBodyPart = new MimeBodyPart();
      mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(mimeBodyPart);

      message.setContent(multipart);

      sender.send(message);
      log.info("Successfully sent email to {}", targetEmail);
    } catch (MessagingException e) {
      log.error("Error while creating message", e);
    } catch (Throwable e) {
      log.error("Fatal error while sending emails", e);
    }
  }
}
