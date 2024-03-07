package ru.hse.lmsteam.backend.app;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.UsersApiProtoConverter;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.repository.UserRepository;

@ComponentScan("ru.hse.lmsteam.backend")
@SpringBootApplication
public class LmsBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(LmsBackendApplication.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner(
      UserRepository userRepository, UsersApiProtoConverter usersConverter) {
    //    return args -> {
    //      userRepository
    //          .saveOne(
    //              new User(
    //                  null, "Ivan", "Ivanov", "Ivanovich", "telegram", Sex.MALE, "", "", null,
    // null))
    //          .subscribe(System.out::println);
    //    };

    return args -> {
      //      userRepository
      //          .findById(UUID.fromString("4ccb1dda-f4de-432e-8dd0-69dc96911625"))
      //          .subscribe(System.out::println);

      var converterUser =
          usersConverter
              .buildGetUserResponse(
                  new User(
                      UUID.fromString("4ccb1dda-f4de-432e-8dd0-69dc96911625"),
                      "Ivan",
                      "Ivanov",
                      null,
                      null,
                      null,
                      "",
                      "",
                      new BigDecimal(0),
                      false))
              .getUser();

      System.out.println("converterUser = " + converterUser);

      System.out.println("converterUser.hasIsDeleted() = " + converterUser.hasIsDeleted());
    };
  }
}
