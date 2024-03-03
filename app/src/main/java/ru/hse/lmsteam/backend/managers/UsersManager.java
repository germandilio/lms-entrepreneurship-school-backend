package ru.hse.lmsteam.backend.managers;

import com.google.protobuf.StringValue;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.dao.UserDbActions;
import ru.hse.lmsteam.schema.api.users.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersManager {
  private final UserDbActions userDbActions;

  public Mono<GetAllUsers.Response> getAllUsers(GetAllUsers.Request request) {
    log.info("Received request: {}", request);
    return userDbActions
        .findAll(request.getLimit(), request.getOffset())
        .collectList()
        .map(
            users ->
                GetAllUsers.Response.newBuilder()
                    .addAllUsers(users.stream().map(this::toProtoUser).toList())
                    .build());
  }

  public Mono<GetUserById.Response> getUserById(GetUserById.Request request) {
    return userDbActions
        .findById(request.getId())
        .map(user -> GetUserById.Response.newBuilder().setUser(toProtoUser(user)).build());
  }

  private User toProtoUser(ru.hse.lmsteam.backend.model.User user) {
    var userBuilder =
        User.newBuilder()
            .setId(user.id())
            .setName(user.name())
            .setSurname(user.surname())
            .setEmail(user.email())
            .setPhoneNumber(user.phoneNumber())
            .setBalance(
                Money.newBuilder()
                    .setCurrencyCode("RUB")
                    .setUnits(user.balance().intValue())
                    .build())
            .setMessengerContact(user.messengerContact())
            .setFinalGradeBonus(user.finalGradeBonus())
            .setSex(Sex.valueOf(user.sex().toString()));

    Optional.ofNullable(user.patronymicOpt())
        .map(StringValue::of)
        .ifPresent(userBuilder::setPatronymic);
    return userBuilder.build();
  }
}
