package ru.hse.lmsteam.backend.app;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import ru.hse.lmsteam.backend.model.User;

@ComponentScan("ru.hse.lmsteam.backend")
@SpringBootApplication
public class LmsBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(LmsBackendApplication.class, args);
  }

  @Bean
  public CommandLineRunner demo(
      @Qualifier("slaveR2dbcEntityTemplate") R2dbcEntityTemplate slaveEntityTemplate,
      @Qualifier("masterR2dbcEntityTemplate") R2dbcEntityTemplate masterEntityTemplate) {
    return (args) -> {
      slaveEntityTemplate
          .select(User.class)
          .all()
          .map(
              user -> {
                System.out.println(user);
                return user;
              })
          .blockLast();
    };
  }
}
