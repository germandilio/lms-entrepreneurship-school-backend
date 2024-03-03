package ru.hse.lmsteam.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import io.swagger.v3.core.jackson.ModelResolver;
import org.springframework.context.annotation.Bean;

public class SwaggerProtobufSupportConfig {
  @Bean
  public ObjectMapper objectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new ProtobufModule());
    objectMapper.registerModule(new ProtobufCustomPropertiesModule());
    return objectMapper;
  }

  @Bean
  public ModelResolver modelResolver(final ObjectMapper objectMapper) {
    return new ModelResolver(objectMapper);
  }
}
