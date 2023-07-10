package eformer.back.eformer_backend.utility.auth;

import eformer.back.eformer_backend.api.v1.request.AuthenticationRequest;
import eformer.back.eformer_backend.api.v1.request.RegisterRequest;
import eformer.back.eformer_backend.api.v1.response.AuthenticationResponse;
import eformer.back.eformer_backend.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import eformer.back.eformer_backend.model.User;
import org.springframework.stereotype.Service;


@Service
public class AuthenticationService {
    private final UserRepository userManager;

    private final PasswordEncoder encoder;

    private final JwtService tokenService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userManager,
                                 PasswordEncoder encoder,
                                 JwtService tokenService,
                                 AuthenticationManager authenticationManager) {
        this.userManager = userManager;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var user = new User(
                request.getUsername(),
                request.getEmail(),
                encoder.encode(request.getPassword()),
                request.getAdLevel(),
                request.getFullName()
        );

        if (userManager.existsByUsername(user.getUsername().toLowerCase())) {
            throw new RuntimeException("Already exists");
        }

        userManager.save(user);

        return new AuthenticationResponse(tokenService.generateToken(user));
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
        );

        var user = userManager.findByUsername(request.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException(request.getUsername() + " not found")
        );

        return new AuthenticationResponse(tokenService.generateToken(user));
    }
}
