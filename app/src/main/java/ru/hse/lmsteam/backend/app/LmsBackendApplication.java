package ru.hse.lmsteam.backend.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan("ru.hse.lmsteam.backend")
@SpringBootApplication
@EnableScheduling
public class LmsBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(LmsBackendApplication.class, args);
  }
}
