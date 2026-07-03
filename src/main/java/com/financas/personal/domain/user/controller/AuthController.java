package com.financas.personal.domain.user.controller;

import com.financas.personal.domain.user.entity.User;
import com.financas.personal.domain.user.entity.UserDTO;
import com.financas.personal.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticação (endpoints públicos).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de cadastro e login")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Cadastrar novo usuário")
    public ResponseEntity<UserDTO.AuthResponse> register(@Valid @RequestBody UserDTO.RegisterRequest request) {
        UserDTO.AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário")
    public ResponseEntity<UserDTO.AuthResponse> login(@Valid @RequestBody UserDTO.LoginRequest request) {
        UserDTO.AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @Operation(summary = "Obter perfil do usuário autenticado")
    public ResponseEntity<UserDTO.ProfileResponse> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserDTO.ProfileResponse response = userService.getProfile(user.getId());
        return ResponseEntity.ok(response);
    }
}
