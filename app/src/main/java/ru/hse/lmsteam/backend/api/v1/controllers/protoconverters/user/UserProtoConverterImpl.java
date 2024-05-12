package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user;

import com.google.protobuf.StringValue;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams.TeamSnippetConverter;
import ru.hse.lmsteam.backend.domain.user_teams.Sex;
import ru.hse.lmsteam.backend.domain.user_teams.Team;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.domain.user_teams.UserRole;
import ru.hse.lmsteam.backend.service.model.user.UserUpsertModel;
import ru.hse.lmsteam.schema.api.users.CreateOrUpdateUser;
import ru.hse.lmsteam.schema.api.users.UserRoleNamespace;
import ru.hse.lmsteam.schema.api.users.UserSnippet;

@Component
@RequiredArgsConstructor
public class UserProtoConverterImpl implements UserProtoConverter {
  private final TeamSnippetConverter teamSnippetConverter;

  @Override
  public ru.hse.lmsteam.schema.api.users.User map(User user, Collection<Team> userTeams) {
    return map(user, userTeams, false);
  }

  @Override
  public ru.hse.lmsteam.schema.api.users.User map(
      User user, Collection<Team> userTeams, boolean forPublicUser) {
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
    if (convertUserRole(user.role()) != null) {
      userBuilder.setRole(convertUserRole(user.role()));
    }

    if (!forPublicUser) {
      if (user.balance() != null) {
        userBuilder.setBalance(user.balance().toString());
      }

      if (userTeams != null && !userTeams.isEmpty()) {
        userBuilder.addAllMemberOfTeams(
            userTeams.stream().map(teamSnippetConverter::toSnippet).toList());
      }
    }
    return userBuilder.build();
  }

  @Override
  public UserSnippet toSnippet(User user) {
    var b =
        UserSnippet.newBuilder()
            .setId(user.id().toString())
            .setName(user.name())
            .setSurname(user.surname())
            .setEmail(user.email());

    if (user.patronymic() != null) {
      b.setPatronymic(StringValue.of(user.patronymic()));
    }
    if (convertUserRole(user.role()) != null) {
      b.setRole(convertUserRole(user.role()));
    }
    return b.build();
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
    userBuilder.role(convertUserRole(user.getRole()));

    return userBuilder.build();
  }

  @Override
  public UserUpsertModel map(CreateOrUpdateUser.Request request) {
    var userModelBuilder = UserUpsertModel.builder();
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
    if (convertUserRole(request.getRole()) != null) {
      userModelBuilder.role(convertUserRole(request.getRole()));
    }

    return userModelBuilder.build();
  }

  private ru.hse.lmsteam.schema.api.users.UserSexNamespace.Sex convertSex(Sex sex) {
    if (sex == null) return null;
    return switch (sex) {
      case FEMALE -> ru.hse.lmsteam.schema.api.users.UserSexNamespace.Sex.FEMALE;
      case MALE -> ru.hse.lmsteam.schema.api.users.UserSexNamespace.Sex.MALE;
    };
  }

  private Sex convertSex(ru.hse.lmsteam.schema.api.users.UserSexNamespace.Sex sex) {
    return switch (sex) {
      case ru.hse.lmsteam.schema.api.users.UserSexNamespace.Sex.FEMALE -> Sex.FEMALE;
      case ru.hse.lmsteam.schema.api.users.UserSexNamespace.Sex.MALE -> Sex.MALE;
      case NOT_INITIALISED -> null;
      case UNRECOGNIZED -> null;
    };
  }

  public static UserRoleNamespace.Role convertUserRole(UserRole role) {
    if (role == null) return null;
    return switch (role) {
      case ADMIN -> UserRoleNamespace.Role.ADMIN;
      case LEARNER -> UserRoleNamespace.Role.LEARNER;
      case TRACKER -> UserRoleNamespace.Role.TRACKER;
      case EXTERNAL_TEACHER -> UserRoleNamespace.Role.EXTERNAL_TEACHER;
    };
  }

  public static UserRole convertUserRole(UserRoleNamespace.Role role) {
    return switch (role) {
      case UserRoleNamespace.Role.ADMIN -> UserRole.ADMIN;
      case UserRoleNamespace.Role.LEARNER -> UserRole.LEARNER;
      case UserRoleNamespace.Role.TRACKER -> UserRole.TRACKER;
      case UserRoleNamespace.Role.EXTERNAL_TEACHER -> UserRole.EXTERNAL_TEACHER;
      case NOT_INITIALISED, UNRECOGNIZED -> null;
    };
  }
}
