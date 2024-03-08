package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import com.google.protobuf.StringValue;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.user.Sex;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.service.model.UserUpsertModel;
import ru.hse.lmsteam.schema.api.users.UpdateOrCreateUser;

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
      userBuilder.setBalance(user.balance().toString());
    }
    if (user.isDeleted() != null) {
      userBuilder.setIsDeleted(user.isDeleted());
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
    if (!user.getBalance().isEmpty()) {
      userBuilder.balance(new BigDecimal(user.getBalance()));
    }
    userBuilder.isDeleted(user.getIsDeleted());

    return userBuilder.build();
  }

  @Override
  public UserUpsertModel map(UpdateOrCreateUser.Request request) {
    var userModelBuilder = UserUpsertModel.builder();
    if (request.hasId()) {
      userModelBuilder.id(UUID.fromString(request.getId().getValue()));
    }
    userModelBuilder.name(request.getName());
    userModelBuilder.surname(request.getSurname());
    if (request.hasPatronymic()) {
      userModelBuilder.patronymic(request.getPatronymic().getValue());
    }
    if (request.hasMessengerContact()) {
      userModelBuilder.messengerContact(request.getMessengerContact().getValue());
    }
    if (convertSex(request.getSex()) != null) {
      userModelBuilder.sex(convertSex(request.getSex()));
    }

    userModelBuilder.email(request.getEmail());
    if (request.hasPhoneNumber()) {
      userModelBuilder.phoneNumber(request.getPhoneNumber().getValue());
    }

    return userModelBuilder.build();
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
