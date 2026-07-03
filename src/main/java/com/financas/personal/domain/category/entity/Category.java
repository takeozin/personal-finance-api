package com.financas.personal.domain.category.entity;

import com.financas.personal.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade que representa uma categoria de transação.
 * Categorias podem ser do sistema (user = null) ou criadas pelo usuário.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // null = categoria padrão do sistema

    @NotBlank(message = "O nome da categoria é obrigatório")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "O tipo da categoria é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    @Column(name = "color_hex")
    @Builder.Default
    private String colorHex = "#6366F1"; // Indigo padrão

    private String icon;

    /**
     * Tipos de categoria.
     */
    public enum CategoryType {
        INCOME,   // Receita
        EXPENSE   // Despesa
    }
}
