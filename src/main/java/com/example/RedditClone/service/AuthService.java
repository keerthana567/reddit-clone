package com.example.RedditClone.service;

import com.example.RedditClone.dto.AuthenticationResponse;
import com.example.RedditClone.dto.LoginRequest;
import com.example.RedditClone.dto.RefreshTokenRequest;
import com.example.RedditClone.dto.RegisterRequest;
import com.example.RedditClone.exceptions.SpringRedditException;
import com.example.RedditClone.model.NotificationEmail;
import com.example.RedditClone.model.User;
import com.example.RedditClone.model.VerificationToken;
import com.example.RedditClone.repository.UserRepository;
import com.example.RedditClone.repository.VerificationTokenRepository;
import com.example.RedditClone.security.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    public Boolean signup(RegisterRequest registerRequest) {

        User user = new User();
//        List userList = new ArrayList();
        user.setUserName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedDate(Instant.now());
        user.setEnabled(false);
//        userList = (userRepository.ExistByEmail(registerRequest.getEmail()));
        boolean isExists = userRepository.existsByEmail(registerRequest.getEmail());
        boolean isExistUser = userRepository.existsByUserName(registerRequest.getUsername());

        if (!isExists && !isExistUser ) {

                userRepository.save(user);

                System.out.println("signup..........." + registerRequest.getUsername());
                String token = generateVerificationToken(user);
                mailService.sendMail(new NotificationEmail("Please Activate your Account",
                        user.getEmail(), "Thank you for signing up to Spring Reddit, " +
                        "please click on the below url to activate your account : " +
                        "http://localhost:8080/api/auth/accountVerification/" + token));
                return true;
            }


        else {
            log.info("User already exists");
            return false;

        }
    }
    public Boolean verifyUser(String email)
{
    try
    {
        boolean isExists = userRepository.existsByEmail(email);
        if (!isExists)
        {
            return true;
        }
    }
    catch (Exception e )
    {
        throw e;
    }
    return false;
}

    public  Boolean verifyUserName(String username)
    {

//        isExistUser() -> new SpringRedditException("Invalid Token"));
       try
       {
           boolean isExistUser = userRepository.existsByUserName(username);
           if (!isExistUser)
           {
               return true;
           }
       }
       catch (Exception e )
       {
           throw e;
       }
        return false;

    }





    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        System.out.println(verificationToken.getUser().getUserName());
        String userName = verificationToken.getUser().getUserName();
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new SpringRedditException("User not found."));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        System.out.println(authenticate + "...............");
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }

    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUserName(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
}
