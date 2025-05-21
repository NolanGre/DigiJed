package org.example.digijed.services.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SignatureException;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration-ms}")
    private Long jwtLifetime;

    public String generateJwtToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtLifetime))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws SignatureException {
        final String login = extractLogin(token);
        return login.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractLogin(String jwt) throws SignatureException {
        return extractAllClaimsFromToken(jwt, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) throws SignatureException {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws SignatureException {
        return extractAllClaimsFromToken(token, Claims::getExpiration);
    }

    public <T> T extractAllClaimsFromToken(String token, Function<Claims, T> claimsResolver) throws SignatureException {
        final Claims claims = extractAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaimsFromToken(String token) throws SignatureException {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
