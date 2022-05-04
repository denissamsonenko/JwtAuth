package com.company.security.sec;

import com.company.security.config.AppProperties;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author : Denis Samsonenko
 * @created : 04.05.2022
 */

@Slf4j
@Service
public class TokenProvider {

    @Value("${spring.app.auth.tokenSecret}")
    private String tokenSecret;

    @Value("${spring.app.auth.tokenExpirationMsec}")
    private Integer tokenExpirationMsec;

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date expireDate = new Date(new Date().getTime() + tokenExpirationMsec);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .claim("authority", userPrincipal.getAuthorities())
                .setExpiration(expireDate)
                // TODO: find info how change method
                .signWith(SignatureAlgorithm.HS512, tokenSecret.getBytes())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        //TODO: find info how to parse jwt
        Claims claims = (Claims) Jwts.parserBuilder()
                .setSigningKey(tokenSecret.getBytes())
                .build()
                .parse(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(tokenSecret.getBytes()).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
