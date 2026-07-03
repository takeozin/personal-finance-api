package com.financas.personal.domain.transaction.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTOs para a entidade Transaction.
 */
public class TransactionDTO {

    /**
     * DTO para criação de transação.
     */
    public record CreateRequest(
            @NotNull(message = "O ID da conta é obrigatório")
            Long accountId,

            Long destinationAccountId, // Apenas para transferências

            @NotNull(message = "O ID da categoria é obrigatório")
            Long categoryId,

            String description,

            @NotNull(message = "O valor é obrigatório")
            @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
            BigDecimal amount,

            @NotNull(message = "O tipo da transação é obrigatório")
            Transaction.TransactionType type,

            @NotNull(message = "A data da transação é obrigatória")
            LocalDate transactionDate
    ) {}

    /**
     * DTO de resposta com dados da transação.
     */
    public record Response(
            Long id,
            Long accountId,
            String accountName,
            Long destinationAccountId,
            String destinationAccountName,
            Long categoryId,
            String categoryName,
            String categoryColor,
            String description,
            BigDecimal amount,
            String type,
            LocalDate transactionDate,
            String createdAt
    ) {}
}
