package dev.ducku.bookapi.auth;

import dev.ducku.bookapi.email.EmailService;
import dev.ducku.bookapi.role.RoleRepository;
import dev.ducku.bookapi.security.JwtService;
import dev.ducku.bookapi.user.Token;
import dev.ducku.bookapi.user.TokenRepository;
import dev.ducku.bookapi.user.User;
import dev.ducku.bookapi.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final int ACTIVATION_CODE_LENGTH = 6;
    private final UserDetailsService userDetailsService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Override
    public void register(ValidationRequest request) throws MessagingException {
        var defaultRole = roleRepository
                .findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Invalid role USER, maybe it was not initialized"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(defaultRole))
                .build();
        userRepository.save(user);
        sendVerificationEmail(user);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authenticated = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        Map<String, Object> claims = new HashMap<>();
        User user = (User) authenticated.getPrincipal();
        claims.put("fullName", user.fullName());
        String jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }


    private void sendVerificationEmail(User user) throws MessagingException {
        String activationCode = generateAndSaveActivationCode(user);
        emailService.sendEmail(user.getEmail(),
                user.fullName(), EmailService.EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl, activationCode,
                "Account Activation");
    }

    private String generateAndSaveActivationCode(User user) {
        String activationCode = generateActivationCode();
        Token token = Token.builder()
                .token(activationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(20))
                .user(user)
                .build();
        tokenRepository.save(token);
        return activationCode;
    }

    private String generateActivationCode() {
        String codeCharacters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < AuthenticationServiceImpl.ACTIVATION_CODE_LENGTH; i++) {
            int randomInt = random.nextInt(codeCharacters.length()); //0..9
            codeBuilder.append(codeCharacters.charAt(randomInt));
        }
        return codeBuilder.toString();
    }
}
