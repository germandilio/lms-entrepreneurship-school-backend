package ru.hse.lmsteam.backend.config;

import java.util.function.Function;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebFluxConfiguration implements WebFluxConfigurer {
  private final PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver;
  private final Pattern idRegexp = Pattern.compile("/[a-f0-9\\-]+");

  private final Function<String, String> uriMappingFunction =
      (uri) -> idRegexp.matcher(uri).replaceAll("/{uuid}");

  @Override
  public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
    configurer.addCustomResolver(pageableHandlerMethodArgumentResolver);
  }

  @Bean
  public NettyServerCustomizer nettyServerCustomizer() {
    return httpServer -> httpServer.metrics(true, uriMappingFunction);
  }
}
