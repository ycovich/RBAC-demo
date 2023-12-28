package by.ycovich.service.impl;

import by.ycovich.dto.AuthenticationResponse;
import by.ycovich.dto.LoginRequest;
import by.ycovich.dto.RegisterRequest;
import by.ycovich.entity.Role;
import by.ycovich.entity.Token;
import by.ycovich.entity.UserEntity;
import by.ycovich.enums.TokenType;
import by.ycovich.exception.UserNotFoundException;
import by.ycovich.repository.RoleRepository;
import by.ycovich.repository.TokenRepository;
import by.ycovich.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class AuthenticationService{
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public ResponseEntity<?> register(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("username is already taken");
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(role));
        var savedUser = userRepository.save(user);
        var jwtToken = jwtTokenService.generateToken(savedUser);

        saveToken(savedUser, jwtToken);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("[successful registration]\n" + new AuthenticationResponse(jwtToken));
    }

    public ResponseEntity<AuthenticationResponse> login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenService.generateToken(authentication);

        revokeAllUserTokens(authentication);
        saveToken(authentication, token);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AuthenticationResponse(token));
    }

    private void saveToken(Authentication authentication, String token){
        var user = getUserFromAuthentication(authentication);
        saveToken(user, token);
    }

    private void saveToken(UserEntity user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Authentication authentication){
        var user = getUserFromAuthentication(authentication);

        var validTokens = tokenRepository.findAllValidTokensByUserId(user.getId());
        if (validTokens.isEmpty()) return;
        validTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    private UserEntity getUserFromAuthentication(Authentication authentication){
        return userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException
                        ("user not found"));
    }
}
