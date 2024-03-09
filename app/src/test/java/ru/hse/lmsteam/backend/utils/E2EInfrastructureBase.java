package ru.hse.lmsteam.backend.utils;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@Testcontainers(disabledWithoutDocker = true)
public abstract class E2EInfrastructureBase {
  private static String r2dbcUrl;

  @Bean
  @ServiceConnection
  static PostgreSQLContainer<?> postgresContainer() {
    var container =
        new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.2-alpine"))
            .withInitScript("schema.sql")
            .withNetworkMode("HOST");
    r2dbcUrl = container.getJdbcUrl().replace("jdbc", "r2dbc");
    return container;
  }

  @DynamicPropertySource
  static void postgresProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.url", () -> r2dbcUrl);
    registry.add("spring.database.master.url", () -> r2dbcUrl);
    registry.add("spring.database.slave.url", () -> r2dbcUrl);
  }
}
