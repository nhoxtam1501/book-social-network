package dev.ducku.bookapi.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("auth")
//@Tag(name = "Authentication")
public class AuthenticationController {

//    private final AuthenticationService service;
//
//    public AuthenticationController(AuthenticationService service) {
//        this.service = service;
//    }
//
//    @PostMapping("/register")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public ResponseEntity<?> register(@RequestBody @Valid ValidationRequest request) throws MessagingException {
//        service.register(request);
//        return ResponseEntity.accepted().build();
//    }
//
//    @PostMapping("/authenticate")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) throws MessagingException {
//        return ResponseEntity.ok(service.authenticate(request));
//    }
//
//    @GetMapping("/ativate-account")
//    public void confrim(@RequestParam String token) throws MessagingException {
//        service.activateAccount(token);
//    }
}
