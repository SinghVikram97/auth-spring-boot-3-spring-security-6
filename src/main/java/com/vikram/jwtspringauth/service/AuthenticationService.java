package com.vikram.jwtspringauth.service;

import com.vikram.jwtspringauth.dto.AuthenticationRequest;
import com.vikram.jwtspringauth.dto.AuthenticationResponse;
import com.vikram.jwtspringauth.dto.RegisterRequest;
import com.vikram.jwtspringauth.entity.Role;
import com.vikram.jwtspringauth.entity.User;
import com.vikram.jwtspringauth.jwt.JWTService;
import com.vikram.jwtspringauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        final User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
         try{
             authenticationManager.authenticate(
                     new UsernamePasswordAuthenticationToken(
                             request.getEmail(),
                             request.getPassword()
                     )
             );
         }catch (Exception e){
             System.out.println(e);
             throw e;
         }


        final User user =  userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
