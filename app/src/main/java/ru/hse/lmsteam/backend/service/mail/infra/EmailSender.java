package ru.hse.lmsteam.backend.service.mail.infra;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import java.util.Collection;

public interface EmailSender {
  /**
   * Creates message with configured system fields. Use it as a template for creating transactional
   * business messages.
   *
   * @param targetEmails - emails to send message to
   * @return message template
   */
  Message getMessageTemplate(Collection<String> targetEmails);

  /**
   * Sends message
   *
   * @param message - message to send
   */
  void send(Message message) throws MessagingException;
}
