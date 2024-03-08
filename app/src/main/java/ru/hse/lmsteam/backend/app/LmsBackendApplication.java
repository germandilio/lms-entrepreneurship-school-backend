package ru.hse.lmsteam.backend.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("ru.hse.lmsteam.backend")
@SpringBootApplication
public class LmsBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(LmsBackendApplication.class, args);
  }
}
