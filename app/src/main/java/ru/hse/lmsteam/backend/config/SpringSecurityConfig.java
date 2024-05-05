package ru.hse.lmsteam.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
  // TODO when thansit to netty, uncomment this (also remove @EnableWebSecurity)
  //  @Bean
  //  public WebFilterChainProxy webSecurityCustomizer() {
  //    return new WebFilterChainProxy();
  //  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().anyRequest();
  }
}
