package com.financas.personal.domain.budget.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTOs para a entidade Budget.
 */
public class BudgetDTO {

    /**
     * DTO para criação/atualização de orçamento.
     */
    public record CreateRequest(
            @NotNull(message = "O ID da categoria é obrigatório")
            Long categoryId,

            @NotNull(message = "O valor limite é obrigatório")
            @DecimalMin(value = "0.01", message = "O limite deve ser maior que zero")
            BigDecimal limitAmount,

            @NotNull(message = "O mês é obrigatório")
            @Min(1) @Max(12)
            Integer month,

            @NotNull(message = "O ano é obrigatório")
            @Min(2020)
            Integer year
    ) {}

    /**
     * DTO de resposta com dados do orçamento e progresso de gastos.
     */
    public record Response(
            Long id,
            Long categoryId,
            String categoryName,
            String categoryColor,
            BigDecimal limitAmount,
            BigDecimal spentAmount,
            BigDecimal remainingAmount,
            double percentUsed,
            Integer month,
            Integer year,
            String status // UNDER_LIMIT, WARNING, OVER_LIMIT
    ) {}
}
