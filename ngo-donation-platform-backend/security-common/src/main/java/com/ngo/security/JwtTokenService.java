package com.ngo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JwtTokenService {

    private final JwtProperties properties;

    public JwtTokenService(JwtProperties properties) {
        this.properties = properties;
    }

    private SecretKey signingKey() {
        byte[] keyBytes = properties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret must be at least 32 bytes for HS256");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(long userId, String username, Set<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + properties.getExpirationMs());
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("roles", new ArrayList<>(roles))
                .issuedAt(now)
                .expiration(exp)
                .signWith(signingKey())
                .compact();
    }

    @SuppressWarnings("unchecked")
    public Optional<JwtUserPrincipal> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);
            List<String> roleList = claims.get("roles", List.class);
            Set<String> roles = roleList == null ? Set.of() : new HashSet<>(roleList);
            return Optional.of(new JwtUserPrincipal(userId, username, roles));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
