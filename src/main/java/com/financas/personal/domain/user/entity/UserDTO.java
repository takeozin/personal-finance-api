package com.financas.personal.domain.user.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs para a entidade User.
 */
public class UserDTO {

    /**
     * DTO para requisição de cadastro de usuário.
     */
    public record RegisterRequest(
            @NotBlank(message = "O nome é obrigatório")
            @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
            String name,

            @NotBlank(message = "O e-mail é obrigatório")
            @Email(message = "Formato de e-mail inválido")
            String email,

            @NotBlank(message = "A senha é obrigatória")
            @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
            String password
    ) {}

    /**
     * DTO para requisição de login.
     */
    public record LoginRequest(
            @NotBlank(message = "O e-mail é obrigatório")
            @Email(message = "Formato de e-mail inválido")
            String email,

            @NotBlank(message = "A senha é obrigatória")
            String password
    ) {}

    /**
     * DTO de resposta com token JWT após autenticação.
     */
    public record AuthResponse(
            Long id,
            String name,
            String email,
            String token
    ) {}

    /**
     * DTO de resposta com dados do perfil do usuário.
     */
    public record ProfileResponse(
            Long id,
            String name,
            String email,
            String defaultCurrency,
            String createdAt
    ) {}
}
