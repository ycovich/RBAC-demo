package by.ycovich.service.impl;

import by.ycovich.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;
    @Autowired
    public LogoutService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")){
            return;
        }
        token = token.substring(7);
        var storedToken = tokenRepository.findByToken(token)
                .orElse(null);
        System.out.println(storedToken.getToken());
        if (storedToken != null){
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }
}
