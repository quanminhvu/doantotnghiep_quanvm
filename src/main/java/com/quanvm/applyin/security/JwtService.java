package com.quanvm.applyin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${security.jwt.secret}")
  private String jwtSecretBase64;

  @Value("${security.jwt.expirationMs:3600000}")
  private long jwtExpirationMs;

  public String generateToken(String subject, Map<String, Object> claims) {
    Instant now = Instant.now();
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusMillis(jwtExpirationMs)))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public String extractDeviceId(String token) {
    return extractAllClaims(token).get("deviceId", String.class);
  }

  public boolean isTokenValid(String token, String username) {
    Claims claims = extractAllClaims(token);
    return username.equals(claims.getSubject()) && claims.getExpiration().after(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}


