package ru.hse.lmsteam.backend.api.v1.controllers;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.hse.lmsteam.backend.app.TestLmsBackendApplication;
import ru.hse.lmsteam.backend.domain.user.Sex;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.repository.UserRepository;
import ru.hse.lmsteam.backend.utils.E2EInfrastructureBase;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestLmsBackendApplication.class)
public class UserControllerE2ETests extends E2EInfrastructureBase {

  @Autowired private UsersController usersController;

  @Autowired private UserRepository userRepository;

  @Autowired private WebTestClient webTestClient;

  private User demoUser =
      new User(
          null,
          "e2eName",
          "e2eSurname",
          "e2ePatronymic",
          "telegram",
          Sex.MALE,
          "email@gmail.com",
          "+77777777777",
          null,
          null);

  @Before
  public void populateDb() {
    var dbUser = userRepository.saveOne(demoUser).block();
    Assertions.assertNotNull(dbUser);
    Assertions.assertNotNull(dbUser.id());
    demoUser = dbUser;
  }

  @Test
  @DisplayName("GET /api/v1/users/{id}")
  void myTest() {}
}
