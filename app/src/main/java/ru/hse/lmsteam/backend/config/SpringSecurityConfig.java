package ru.hse.lmsteam.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.server.WebFilterChainProxy;

@Configuration
public class SpringSecurityConfig {
  @Bean
  public WebFilterChainProxy webSecurityCustomizer() {
    return new WebFilterChainProxy();
  }
}
