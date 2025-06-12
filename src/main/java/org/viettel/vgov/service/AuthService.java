package org.viettel.vgov.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.viettel.vgov.dto.request.LoginRequestDto;
import org.viettel.vgov.dto.response.JwtResponseDto;
import org.viettel.vgov.dto.response.UserResponseDto;
import org.viettel.vgov.mapper.UserMapper;
import org.viettel.vgov.model.User;
import org.viettel.vgov.repository.UserRepository;
import org.viettel.vgov.security.JwtUtils;
import org.viettel.vgov.security.UserPrincipal;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    
    public JwtResponseDto authenticateUser(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserResponseDto userResponse = userMapper.toResponseDto(user);
        
        return new JwtResponseDto(jwt, userResponse);
    }
    
    public UserResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userMapper.toResponseDto(user);
    }
    
    public String refreshToken(String email) {
        return jwtUtils.generateTokenFromEmail(email);
    }
    
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
