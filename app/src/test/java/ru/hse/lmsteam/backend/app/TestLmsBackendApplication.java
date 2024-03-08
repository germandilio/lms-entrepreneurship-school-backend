package ru.hse.lmsteam.backend.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.hse.lmsteam.backend")
public class TestLmsBackendApplication {
  public static void main(String[] args) {
    SpringApplication.from(LmsBackendApplication::main)
        .with(TestLmsBackendApplication.class)
        .run(args);
  }
}
