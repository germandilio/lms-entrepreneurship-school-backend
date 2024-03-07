package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import com.google.protobuf.BoolValue;
import com.google.protobuf.StringValue;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.user.Sex;
import ru.hse.lmsteam.backend.domain.user.User;

@Component
public class UserProtoConverterImpl implements UserProtoConverter {

  @Override
  public ru.hse.lmsteam.schema.api.users.User map(User user) {
    var userBuilder = ru.hse.lmsteam.schema.api.users.User.newBuilder();
    if (user.id() != null) {
      userBuilder.setId(user.id().toString());
    }
    if (user.name() != null) {
      userBuilder.setName(user.name());
    }
    if (user.surname() != null) {
      userBuilder.setSurname(user.surname());
    }
    if (user.patronymic() != null) {
      userBuilder.setPatronymic(StringValue.of(user.patronymic()));
    }
    if (user.messengerContact() != null) {
      userBuilder.setMessengerContact(StringValue.of(user.messengerContact()));
    }
    if (convertSex(user.sex()) != null) {
      userBuilder.setSex(convertSex(user.sex()));
    }
    if (user.email() != null) {
      userBuilder.setEmail(user.email());
    }
    if (user.phoneNumber() != null) {
      userBuilder.setPhoneNumber(StringValue.of(user.phoneNumber()));
    }
    if (user.balance() != null) {
      userBuilder.setBalance(StringValue.of(user.balance().toString()));
    }
    if (user.isDeleted() != null) {
      userBuilder.setIsDeleted(BoolValue.of(user.isDeleted()));
    }

    return userBuilder.build();
  }

  @Override
  public User map(ru.hse.lmsteam.schema.api.users.User user) {
    var userBuilder = User.builder();
    if (!user.getId().isEmpty()) {
      userBuilder.id(UUID.fromString(user.getId()));
    }
    userBuilder.name(user.getName());
    userBuilder.surname(user.getSurname());
    if (user.hasPatronymic()) {
      userBuilder.patronymic(user.getPatronymic().getValue());
    }
    if (user.hasMessengerContact()) {
      userBuilder.messengerContact(user.getMessengerContact().getValue());
    }
    userBuilder.sex(convertSex(user.getSex()));
    userBuilder.email(user.getEmail());
    if (user.hasPhoneNumber()) {
      userBuilder.phoneNumber(user.getPhoneNumber().getValue());
    }
    if (user.hasBalance()) {
      userBuilder.balance(new BigDecimal(user.getBalance().getValue()));
    }
    if (user.hasIsDeleted()) {
      userBuilder.isDeleted(user.getIsDeleted().getValue());
    }

    return userBuilder.build();
  }

  private ru.hse.lmsteam.schema.api.users.Sex convertSex(Sex sex) {
    if (sex == null) return null;
    return switch (sex) {
      case FEMALE -> ru.hse.lmsteam.schema.api.users.Sex.FEMALE;
      case MALE -> ru.hse.lmsteam.schema.api.users.Sex.MALE;
    };
  }

  private Sex convertSex(ru.hse.lmsteam.schema.api.users.Sex sex) {
    return switch (sex) {
      case ru.hse.lmsteam.schema.api.users.Sex.FEMALE -> Sex.FEMALE;
      case ru.hse.lmsteam.schema.api.users.Sex.MALE -> Sex.MALE;
      case NOT_INITIALISED -> null;
      case UNRECOGNIZED -> null;
    };
  }
}
