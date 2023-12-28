package by.ycovich.service.impl;

import by.ycovich.entity.UserEntity;
import by.ycovich.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Log
@Component
public class JwtTokenService {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    @Autowired
    private final TokenRepository tokenRepository;


    public String generateToken(UserEntity user){
        String username = user.getUsername();
        return generateToken(username);
    }

    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        return generateToken(username);
    }

    private String generateToken(String username){

        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + 1000 * 60 * 24);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUsernameFromToken(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        var isTokenValid = tokenRepository.findByToken(token)
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);
        if (!isTokenValid) return false;

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.severe("Expired or invalid JWT token: " + e.getMessage());
            return false;
        }
    }

}
