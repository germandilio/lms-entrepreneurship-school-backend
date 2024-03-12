package ru.hse.lmsteam.backend.api.v1.controllers;

import com.google.protobuf.StringValue;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.hse.lmsteam.backend.app.TestLmsBackendApplication;
import ru.hse.lmsteam.backend.domain.user.Sex;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.domain.user.UserRole;
import ru.hse.lmsteam.backend.service.UserManager;
import ru.hse.lmsteam.backend.service.model.UserUpsertModel;
import ru.hse.lmsteam.backend.utils.E2EInfrastructureBase;
import ru.hse.lmsteam.schema.api.users.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestLmsBackendApplication.class)
public class UserControllerE2ETests extends E2EInfrastructureBase {

  @Autowired private UserManager userManager;

  @Autowired private WebTestClient webTestClient;

  private User demoUser =
      User.builder()
          .name("e2eName")
          .surname("e2eSurname")
          .patronymic("e2ePatronymic")
          .messengerContact("telegram")
          .sex(Sex.MALE)
          .role(UserRole.STUDENT)
          .email("email@gmail.com")
          .phoneNumber("+77777777777")
          .build();

  @BeforeEach
  public void populateDb() {
    if (demoUser.id() == null) {
      var userRequest =
          UserUpsertModel.builder()
              .id(demoUser.id())
              .name(demoUser.name())
              .surname(demoUser.surname())
              .patronymic(demoUser.patronymic())
              .messengerContact(demoUser.messengerContact())
              .sex(demoUser.sex())
              .role(demoUser.role())
              .email(demoUser.email())
              .phoneNumber(demoUser.phoneNumber())
              .build();
      demoUser = userManager.create(userRequest).block();
    }

    Assertions.assertNotNull(demoUser);
    Assertions.assertNotNull(demoUser.id());
  }

  @Test
  @DisplayName("GET /api/v1/users/{id}")
  void getUserTest() {
    webTestClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/api/v1/users/{id}").build(demoUser.id().toString()))
        .accept(MediaType.APPLICATION_PROTOBUF)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(GetUser.Response.class)
        .consumeWith(
            r -> {
              var response = r.getResponseBody();
              Assertions.assertNotNull(response, "Response body is null!");

              var returnedUser = response.getUser();
              Assertions.assertNotEquals(demoUser.id(), returnedUser.getId());
              Assertions.assertEquals(demoUser.name(), returnedUser.getName());
              Assertions.assertEquals(demoUser.surname(), returnedUser.getSurname());
              Assertions.assertEquals(
                  demoUser.patronymic(), returnedUser.getPatronymic().getValue());
              Assertions.assertEquals(
                  demoUser.messengerContact(), returnedUser.getMessengerContact().getValue());
              Assertions.assertEquals(demoUser.sex().toString(), returnedUser.getSex().toString());
              Assertions.assertEquals(
                  demoUser.role().toString(), returnedUser.getRole().toString());
              Assertions.assertEquals(demoUser.email(), returnedUser.getEmail());
              Assertions.assertEquals(
                  demoUser.phoneNumber(), returnedUser.getPhoneNumber().getValue());
              Assertions.assertEquals(demoUser.balance().toString(), returnedUser.getBalance());
              Assertions.assertFalse(returnedUser.getIsDeleted());
            });
  }

  @Test
  @DisplayName("POST /api/v1/users")
  void createUserTest() {
    var requestBody =
        UpdateOrCreateUser.Request.newBuilder()
            .setName("fkmd")
            .setSurname("chick")
            .setSex(UserSexNamespace.Sex.FEMALE)
            .setRole(UserRoleNamespace.Role.STUDENT)
            .setEmail("email@gmail.com")
            .build();
    webTestClient
        .post()
        .uri(uriBuilder -> uriBuilder.path("/api/v1/users").build())
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_PROTOBUF)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(UpdateOrCreateUser.Response.class)
        .consumeWith(
            r -> {
              var response = r.getResponseBody();
              Assertions.assertNotNull(response, "Response body is null!");

              var returnedUser = response.getUser();
              Assertions.assertNotEquals("", returnedUser.getId());
              Assertions.assertEquals(requestBody.getName(), returnedUser.getName());
              Assertions.assertEquals(requestBody.getSurname(), returnedUser.getSurname());
              Assertions.assertFalse(returnedUser.hasPatronymic());
              Assertions.assertFalse(returnedUser.hasMessengerContact());
              Assertions.assertEquals(
                  requestBody.getSex().toString(), returnedUser.getSex().toString());
              Assertions.assertEquals(
                  requestBody.getRole().toString(), returnedUser.getRole().toString());
              Assertions.assertEquals(requestBody.getEmail(), returnedUser.getEmail());
              Assertions.assertFalse(returnedUser.hasPhoneNumber());
              Assertions.assertEquals("0.0000", returnedUser.getBalance());
              Assertions.assertFalse(returnedUser.getIsDeleted());
            });
  }

  @Test
  @DisplayName("PATCH /api/v1/users")
  void updateUserTest() {
    // updating demoUser with info same as in createUserTest
    var requestBody =
        UpdateOrCreateUser.Request.newBuilder()
            .setId(StringValue.of(demoUser.id().toString()))
            .setName("fkmd")
            .setSurname("chick")
            .setSex(UserSexNamespace.Sex.FEMALE)
            .setRole(UserRoleNamespace.Role.TRACKER)
            .setEmail("email@gmail.com")
            .build();
    webTestClient
        .patch()
        .uri(uriBuilder -> uriBuilder.path("/api/v1/users").build())
        .bodyValue(requestBody)
        .accept(MediaType.APPLICATION_PROTOBUF)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(UpdateOrCreateUser.Response.class)
        .consumeWith(
            r -> {
              var response = r.getResponseBody();
              Assertions.assertNotNull(response, "Response body is null!");

              var returnedUser = response.getUser();
              Assertions.assertEquals(demoUser.id().toString(), returnedUser.getId());
              Assertions.assertEquals(requestBody.getName(), returnedUser.getName());
              Assertions.assertEquals(requestBody.getSurname(), returnedUser.getSurname());
              Assertions.assertEquals(
                  demoUser.patronymic(), returnedUser.getPatronymic().getValue());
              Assertions.assertEquals(
                  demoUser.messengerContact(), returnedUser.getMessengerContact().getValue());
              Assertions.assertEquals(
                  requestBody.getSex().toString(), returnedUser.getSex().toString());
              Assertions.assertEquals(
                  requestBody.getRole().toString(), returnedUser.getRole().toString());
              Assertions.assertEquals(requestBody.getEmail(), returnedUser.getEmail());
              Assertions.assertEquals(
                  demoUser.phoneNumber(), returnedUser.getPhoneNumber().getValue());
              Assertions.assertEquals(demoUser.balance().toString(), returnedUser.getBalance());
              Assertions.assertFalse(returnedUser.getIsDeleted());
            });

    // clear demoUser after mutation, to be reinitialized
    demoUser = null;
  }

  @Test
  @DisplayName("DELETE /api/v1/users/{id}")
  void deleteUserTest() {
    webTestClient
        .delete()
        .uri(uriBuilder -> uriBuilder.path("/api/v1/users/{id}").build(demoUser.id().toString()))
        .accept(MediaType.APPLICATION_PROTOBUF)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(DeleteUser.Response.class)
        .consumeWith(
            r -> {
              var response = r.getResponseBody();
              Assertions.assertNotNull(response, "Response body is null!");

              Assertions.assertEquals(1L, response.getEntitiesDeleted());

              // check that GET /api/v1/users/{id} return null response body after deletion
              webTestClient
                  .get()
                  .uri(
                      uriBuilder ->
                          uriBuilder.path("/api/v1/users/{id}").build(demoUser.id().toString()))
                  .accept(MediaType.APPLICATION_PROTOBUF)
                  .exchange()
                  .expectStatus()
                  .isOk()
                  .expectBody(GetUser.Response.class)
                  .consumeWith(
                      r2 -> {
                        var response2 = r2.getResponseBody();
                        Assertions.assertNotNull(response2, "Response body is null!");
                        Assertions.assertTrue(response2.getUser().getIsDeleted());
                      });
            });

    // clear demoUser after mutation, to be reinitialized
    demoUser = null;
  }
}
