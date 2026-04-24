package com.nutrition.dss.controller.api;

import com.nutrition.dss.dto.*;
import com.nutrition.dss.model.User;
import com.nutrition.dss.security.JwtUtil;
import com.nutrition.dss.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API for authentication (register + login).
 * Returns JWT tokens for API access.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthApiController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getFullName(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(userService.toDTO(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var userOpt = userService.login(request.getEmail(), request.getPassword());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return ResponseEntity.ok(new LoginResponse(token, userService.toDTO(user)));
    }
}
