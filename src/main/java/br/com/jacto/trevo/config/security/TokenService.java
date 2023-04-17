package br.com.jacto.trevo.config.security;

import br.com.jacto.trevo.controller.auth.dto.TokenDto;
import br.com.jacto.trevo.model.account.Account;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public TokenDto token(Account account) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String tokenJWT = JWT.create()
                    .withIssuer("API trevo")
                    .withSubject(account.getUsername())
                    .withClaim("id", account.getAccountId().toString())
                    .withExpiresAt(dateExpiration())
                    .sign(algorithm);
            return new TokenDto(account.getAccountId(), tokenJWT);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error ao gerar token", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("API trevo")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }

    @Bean
    private Instant dateExpiration() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}
