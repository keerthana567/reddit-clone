package com.example.RedditClone.controller;

import com.example.RedditClone.dto.AuthenticationResponse;
import com.example.RedditClone.dto.LoginRequest;
import com.example.RedditClone.dto.RefreshTokenRequest;
import com.example.RedditClone.dto.RegisterRequest;
import com.example.RedditClone.service.AuthService;
import com.example.RedditClone.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;


    @GetMapping("/VerifyEmail/{email}")
    public ResponseEntity<?> verifyEmail(@PathVariable String email) {
        authService.verifyUser(email);
        return new ResponseEntity<>(authService.verifyUser(email), HttpStatus.OK);
    }
    @GetMapping("/VerifyUser/{username}")
    public ResponseEntity<?> verifyUser(@PathVariable String username) {
        authService.verifyUserName(username);
        return new ResponseEntity<>( authService.verifyUserName(username), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> verified(@RequestBody RegisterRequest registerRequest) {

     return new ResponseEntity<>(authService.signup(registerRequest), HttpStatus.OK);

    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated Successfully.", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        System.out.println("login");
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body("Refresh Token Deleted Successfully!!");
    }
}
