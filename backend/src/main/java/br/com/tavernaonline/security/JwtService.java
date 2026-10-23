package br.com.tavernaonline.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMinutos;

    public JwtService(
            @Value("${security.jwt.secret}") String segredo,
            @Value("${security.jwt.expiration-minutes}") long expiracaoMinutos) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public String gerarToken(String subject) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plus(expiracaoMinutos, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .signWith(chave)
                .compact();
    }

    public String extrairSubject(String token) {
        return extrairClaims(token).getSubject();
    }

    public boolean tokenValido(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public long getExpiracaoMinutos() {
        return expiracaoMinutos;
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
