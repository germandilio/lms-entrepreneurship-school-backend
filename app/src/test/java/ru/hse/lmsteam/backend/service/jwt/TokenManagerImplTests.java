package ru.hse.lmsteam.backend.service.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

public class TokenManagerImplTests {
  private static final String secretKey =
      "secretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKey";
  private static final long expirationInSeconds = 10000L;

  private final TokenManager tokenManager = new TokenManagerImpl(secretKey, expirationInSeconds);

  @Test
  void createTokenShouldReturnSomeToken() {
    UUID userId = UUID.randomUUID();
    String token = tokenManager.createToken(userId);

    assertThat(token).isNotBlank();
  }

  @Test
  void validateTokenShouldReturnTrueForValidNonExpiredToken() {
    UUID userId = UUID.randomUUID();
    String token = createTestToken(userId, false); // non-expired passwordResetToken

    boolean isValid = tokenManager.validate(token, userId);

    assertThat(isValid).isTrue();
  }

  @Test
  void validateTokenShouldReturnFalseForExpiredToken() {
    UUID userId = UUID.randomUUID();
    String token = createTestToken(userId, true); // expired passwordResetToken

    boolean isValid = tokenManager.validate(token, userId);

    assertThat(isValid).isFalse();
  }

  @Test
  void validateTokenShouldReturnFalseForInvalidToken() {
    UUID userId = UUID.randomUUID();
    String token = createTestToken(userId, false).substring(10); // invalid passwordResetToken

    boolean isValid = tokenManager.validate(token, userId);

    assertThat(isValid).isFalse();
  }

  @Test
  void validateTokenShouldThrowExceptionForNullToken() {
    UUID userId = UUID.randomUUID();

    assertThatThrownBy(() -> tokenManager.validate(null, userId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Token is null");
  }

  @Test
  void validateTokenShouldThrowExceptionForNullUserId() {
    String token = "valid-passwordResetToken";

    assertThatThrownBy(() -> tokenManager.validate(token, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("User id is null");
  }

  private String createTestToken(UUID userid, boolean expired) {
    var expiration =
        expired
            ? new Date(System.currentTimeMillis() - 1000)
            : new Date(System.currentTimeMillis() + expirationInSeconds * 1000);
    return Jwts.builder()
        .subject(userid.toString())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(expiration)
        .signWith(getSignInKey(), Jwts.SIG.HS512)
        .compact();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
