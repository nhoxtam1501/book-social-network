package dev.ducku.bookapi.auth;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

public interface AuthenticationService {
    void register(ValidationRequest request) throws MessagingException;

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void activateAccount(String token) throws MessagingException;
}
