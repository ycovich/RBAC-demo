package by.ycovich.controller;

import by.ycovich.dto.AuthenticationResponse;
import by.ycovich.dto.LoginRequest;
import by.ycovich.dto.RegisterRequest;
import by.ycovich.service.impl.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest){
        return authenticationService.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest){
        return authenticationService.login(loginRequest);
    }
}
