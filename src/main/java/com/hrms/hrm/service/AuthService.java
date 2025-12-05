package com.hrms.hrm.service;

import com.hrms.hrm.dto.LoginRequestDto;
import com.hrms.hrm.dto.LoginResponseDto;
import com.hrms.hrm.dto.SignupRequestDto;
import com.hrms.hrm.dto.SignupResponseDto;
import com.hrms.hrm.error.BadCredentialsException;
import com.hrms.hrm.model.User;
import com.hrms.hrm.repository.UserRepository;
import com.hrms.hrm.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 10;

    public LoginResponseDto login(LoginRequestDto request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    return new BadCredentialsException("Invalid credentials");
                });
        
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new BadCredentialsException("Account locked. Try again later");
        }

       try {

           Authentication auth = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

           userRepository.updateFailedLoginAttempts(user.getUsername(), 0);
           userRepository.lastLogin(user.getUsername(), LocalDateTime.now());
           UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
           String token = jwtUtil.generateToken(userDetails.getUsername(), user);

           UUID employeeId = null;
           if(user.getEmployee() != null) {
               employeeId = user.getEmployee().getId();
           }

           return LoginResponseDto.builder()
                   .username(user.getUsername())
                   .employeeId(employeeId)
                   .token(token)
                   .refreshToken(token)
                   .expiresIn(86400L)
                   .role(user.getRole().name())
                   .build();

       } catch (Exception ex) {
            int attempts = (user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts() + 1);
            log.warn("Authentication failed for user: {} - Attempt {}/{}", 
                   user.getUsername(), attempts, MAX_FAILED_ATTEMPTS);
            
            userRepository.updateFailedLoginAttempts(user.getUsername(), attempts);
            
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(LOCK_MINUTES);
                log.warn("Max login attempts reached. Locking account for user: {} until {}", 
                       user.getUsername(), lockUntil);
                userRepository.lockUser(user.getUsername(), lockUntil);
            }
            
            log.error("Authentication error for user: {} - {}", user.getUsername(), ex.getMessage(), ex);
            throw ex;
       }
    }

    public SignupResponseDto signup(SignupRequestDto request) {

        String username = request.getUsername();

        if(userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }


        User.Role role;

        try {
            if(request.getRole() == null || request.getRole().isBlank()) {
                role = User.Role.ROLE_HR;
            }else {
                String normalized = request.getRole().trim().toUpperCase();
                if(!normalized.startsWith("ROLE_")) {
                    normalized = "ROLE_" + normalized;
                }

                role = User.Role.valueOf(normalized);
            }

        }catch (IllegalArgumentException ex) {
            role = User.Role.ROLE_HR;
        }

        User newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isActive(true)
                .failedLoginAttempts(0)
                .build();
        User saved = userRepository.save(newUser);

        return  SignupResponseDto.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .role(saved.getRole().name())
                .build();
    }
}
