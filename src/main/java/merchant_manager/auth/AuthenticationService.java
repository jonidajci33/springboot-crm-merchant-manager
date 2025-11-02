package merchant_manager.auth;

import merchant_manager.config.AuthConfigs.JwtService;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.enums.TokenType;
import merchant_manager.models.token.Token;
import merchant_manager.repository.TokenRepository;
import merchant_manager.models.User;
import lombok.extern.slf4j.Slf4j;
import merchant_manager.service.implementation.UserServiceImp;
import org.springframework.security.authentication.BadCredentialsException;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.stereotype.Service;


import java.io.IOException;

@Service
@Slf4j
public class AuthenticationService {

    private final UserServiceImp userServiceImp;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserServiceImp userServiceImp, TokenRepository tokenRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userServiceImp = userServiceImp;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            // Handle authentication failure
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            log.info("User: " + request.getUsername() + " authenticated successfully.");
        } catch (BadCredentialsException e) {
            // Authentication failed
            log.error("User: " + request.getUsername() + " had an error on authentication.");
            throw new CustomExceptions.UnauthorizedAccessException("Invalid username or password");
        }

        // Handle user not found
        User user = userServiceImp.findByUsername(request.getUsername());

        try {
            // Handle JWT token generation
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            // Revoke old tokens and save new ones
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            // Return success response
            AuthenticationResponse authResponse = new AuthenticationResponse();
            authResponse.setUser(user);
            authResponse.setAccessToken(jwtToken);
            authResponse.setRefreshToken(refreshToken);

            return authResponse;

        } catch (Exception e) {
            // Handle any token generation or database operation failures
            throw new CustomExceptions.ResourceNotFoundException("Token generation or saving failed");
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = new Token();
        token.setUser(user);
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setExpired(false);
        token.setRevoked(false);

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.userServiceImp.findByUsername(username);
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);

                AuthenticationResponse authResponse = new AuthenticationResponse();
                authResponse.setAccessToken(accessToken);
                authResponse.setRefreshToken(refreshToken);

                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}