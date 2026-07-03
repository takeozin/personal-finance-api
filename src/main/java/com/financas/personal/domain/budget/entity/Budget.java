package com.financas.personal.domain.budget.entity;

import com.financas.personal.domain.category.entity.Category;
import com.financas.personal.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidade que representa um orçamento mensal por categoria.
 * O usuário define limites de gastos por categoria para cada mês/ano.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "budgets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "category_id", "budget_month", "budget_year"})
})
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull(message = "O valor limite é obrigatório")
    @DecimalMin(value = "0.01", message = "O limite deve ser maior que zero")
    @Column(name = "limit_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal limitAmount;

    @NotNull(message = "O mês é obrigatório")
    @Min(value = 1, message = "O mês deve ser entre 1 e 12")
    @Max(value = 12, message = "O mês deve ser entre 1 e 12")
    @Column(name = "budget_month", nullable = false)
    private Integer month;

    @NotNull(message = "O ano é obrigatório")
    @Min(value = 2020, message = "O ano deve ser válido")
    @Column(name = "budget_year", nullable = false)
    private Integer year;
}
