package ru.hse.lmsteam.backend.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.UsersApiProtoConverter;
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
    return null;
  }
}
