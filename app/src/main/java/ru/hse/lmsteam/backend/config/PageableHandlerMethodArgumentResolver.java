package ru.hse.lmsteam.backend.config;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class PageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

  private final String defaultPage;
  private final String defaultSize;
  private final Integer maxPageSize;

  public PageableHandlerMethodArgumentResolver(
      @Value("${spring.pageable.default-page}") Integer defaultPage,
      @Value("${spring.pageable.default-page-size}") Integer defaultSize,
      @Value("${spring.pageable.max-page-size}") Integer maxPageSize) {
    this.defaultPage = defaultPage.toString();
    this.defaultSize = defaultSize.toString();
    this.maxPageSize = maxPageSize;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return Pageable.class.equals(parameter.getParameterType());
  }

  @NotNull @Override
  public Mono<Object> resolveArgument(
      @NotNull MethodParameter methodParameter,
      @NotNull BindingContext bindingContext,
      ServerWebExchange serverWebExchange) {
    List<String> pageValues =
        serverWebExchange.getRequest().getQueryParams().getOrDefault("page", List.of(defaultPage));
    List<String> sizeValues =
        serverWebExchange.getRequest().getQueryParams().getOrDefault("size", List.of(defaultSize));

    String page = pageValues.getFirst();

    String sortParam = serverWebExchange.getRequest().getQueryParams().getFirst("sort");
    Sort sort = Sort.unsorted();

    if (sortParam != null) {
      String[] parts = sortParam.split(",");
      if (parts.length == 2) {
        String property = parts[0];
        Sort.Direction direction = Sort.Direction.fromString(parts[1]);
        sort = Sort.by(direction, property);
      }
    }

    return Mono.just(
        PageRequest.of(
            Integer.parseInt(page),
            Math.min(Integer.parseInt(sizeValues.getFirst()), maxPageSize),
            sort));
  }
}
