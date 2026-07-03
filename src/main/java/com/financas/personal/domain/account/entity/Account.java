package com.financas.personal.domain.account.entity;

import com.financas.personal.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma conta financeira do usuário.
 * Exemplos: Conta Corrente, Poupança, Carteira, Cartão de Crédito.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "O nome da conta é obrigatório")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "O tipo da conta é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Tipos de conta suportados pelo sistema.
     */
    public enum AccountType {
        CURRENT,      // Conta Corrente
        SAVINGS,      // Poupança
        CASH,         // Dinheiro em Espécie
        CREDIT_CARD,  // Cartão de Crédito
        INVESTMENT    // Investimentos
    }
}
