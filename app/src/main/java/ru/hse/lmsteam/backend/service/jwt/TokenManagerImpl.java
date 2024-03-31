package ru.hse.lmsteam.backend.service.jwt;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TokenManagerImpl implements TokenManager {

  private final String secretKey;
  private final Long jwtExpirationSeconds;
  private final JwtParser parser;

  public TokenManagerImpl(
      @Value("${application.security.jwt.secret-key}") String secretKey,
      @Value("${application.security.jwt.expiration}") Long jwtExpirationSeconds) {
    this.secretKey = secretKey;
    this.jwtExpirationSeconds = jwtExpirationSeconds;
    this.parser = Jwts.parser().verifyWith(getSignInKey()).build();
  }

  // todo plan to avoid using Date class

  @Override
  public String createToken(UUID userid) {
    return Jwts.builder()
        .subject(userid.toString())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpirationSeconds * 1000))
        .signWith(getSignInKey(), Jwts.SIG.HS512)
        .compact();
  }

  @Override
  public boolean validate(String token, UUID userId) {
    if (token == null) {
      throw new IllegalArgumentException("Token is null");
    }
    if (userId == null) {
      throw new IllegalArgumentException("User id is null");
    }

    try {
      var claims = parser.parseSignedClaims(token);

      return Objects.equals(claims.getPayload().getSubject(), userId.toString())
          && !claims.getPayload().getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
