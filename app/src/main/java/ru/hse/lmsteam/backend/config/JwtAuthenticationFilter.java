package ru.hse.lmsteam.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.hse.lmsteam.backend.domain.UserRole;
import ru.hse.lmsteam.backend.service.AuthorizationManager;
import ru.hse.lmsteam.backend.service.model.FailedAuthorizationResult;
import ru.hse.lmsteam.backend.service.model.SuccessfulAuthorizationResult;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final AuthorizationManager authManager;
  private final String authRoute;
  private final boolean authEnabled;

  public JwtAuthenticationFilter(
      @Value("${application.auth.route}") String authRoute,
      @Value("${application.auth.enabled}") boolean authEnabled,
      @Autowired AuthorizationManager authManager) {

    this.authRoute = authRoute;
    this.authManager = authManager;
    this.authEnabled = authEnabled;
    log.info("Enabled: {}", authEnabled);
    log.info("Unprotected routes: {}/*", authRoute);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (!authEnabled || request.getServletPath().contains(authRoute)) {
      filterChain.doFilter(request, response);
      return;
    }
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      response.setStatus(401);
      return;
    }
    final String jwt = authHeader.substring(7);
    final var authResult = authManager.authorize(jwt).block();
    switch (authResult) {
      case SuccessfulAuthorizationResult(UUID id, UserRole role) ->
          filterChain.doFilter(request, response);

      case FailedAuthorizationResult() -> response.setStatus(401);

      case null -> response.setStatus(403);
      default -> throw new IllegalStateException("Unexpected value: " + authResult);
    }
    ;
  }
}
