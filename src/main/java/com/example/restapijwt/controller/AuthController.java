package com.example.restapijwt.controller;

import com.example.restapijwt.model.Role;
import com.example.restapijwt.model.User;
import com.example.restapijwt.payload.JwtResponse;
import com.example.restapijwt.payload.LoginRequest;
import com.example.restapijwt.payload.SignupRequest;
import com.example.restapijwt.repository.UserRepository;
import com.example.restapijwt.security.jwt.JwtUtils;
import com.example.restapijwt.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        // Create new user's account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (signUpRequest.getRoles() != null && signUpRequest.getRoles().contains("ADMIN")) {
            roles.add(Role.ADMIN);
        } else {
            roles.add(Role.USER);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}