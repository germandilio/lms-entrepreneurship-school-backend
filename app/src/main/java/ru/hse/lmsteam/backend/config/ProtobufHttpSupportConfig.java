package ru.hse.lmsteam.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class ProtobufHttpSupportConfig implements WebFluxConfigurer {
  @Bean
  public ProtobufJsonFormatHttpMessageConverter protobufHttpMessageConverter() {
    return new ProtobufJsonFormatHttpMessageConverter();
  }
}
