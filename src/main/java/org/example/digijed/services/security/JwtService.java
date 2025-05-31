package org.example.digijed.services.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.security.SignatureException;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration-ms}")
    private Long jwtLifetime;

    public String generateJwtToken(UserDetails userDetails) {
        log.debug("Generating JWT token for user: {}", userDetails.getUsername());
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtLifetime))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws SignatureException {
        log.debug("Validating JWT token for user: {}", userDetails.getUsername());
        final String login = extractLogin(token);
        return login.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractLogin(String jwt) throws SignatureException {
        return extractAllClaimsFromToken(jwt, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) throws SignatureException {
        boolean expired = extractExpiration(token).before(new Date());
        if (expired) {
            log.warn("Token has expired");
        }
        return expired;
    }

    private Date extractExpiration(String token) throws SignatureException {
        return extractAllClaimsFromToken(token, Claims::getExpiration);
    }

    public <T> T extractAllClaimsFromToken(String token, Function<Claims, T> claimsResolver) throws SignatureException {
        final Claims claims = extractAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaimsFromToken(String token) {
        log.debug("Extracting all claims from JWT token");
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw e;
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
