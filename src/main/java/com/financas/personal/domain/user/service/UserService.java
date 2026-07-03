package com.financas.personal.domain.user.service;

import com.financas.personal.config.JwtUtil;
import com.financas.personal.domain.user.entity.User;
import com.financas.personal.domain.user.entity.UserDTO;
import com.financas.personal.domain.user.repository.UserRepository;
import com.financas.personal.exception.BusinessException;
import com.financas.personal.exception.DuplicateResourceException;
import com.financas.personal.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pelas regras de negócio do usuário,
 * incluindo cadastro e autenticação.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Registra um novo usuário no sistema.
     */
    @Transactional
    public UserDTO.AuthResponse register(UserDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Já existe um usuário com o e-mail: " + request.email());
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        return new UserDTO.AuthResponse(user.getId(), user.getName(), user.getEmail(), token);
    }

    /**
     * Autentica um usuário e retorna o token JWT.
     */
    public UserDTO.AuthResponse login(UserDTO.LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("E-mail ou senha inválidos");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new UserDTO.AuthResponse(user.getId(), user.getName(), user.getEmail(), token);
    }

    /**
     * Retorna o perfil do usuário pelo ID.
     */
    public UserDTO.ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

        return new UserDTO.ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDefaultCurrency(),
                user.getCreatedAt().toString()
        );
    }

    /**
     * Busca a entidade User pelo ID (uso interno dos outros serviços).
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));
    }
}
