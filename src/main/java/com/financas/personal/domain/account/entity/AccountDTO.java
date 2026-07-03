package com.financas.personal.domain.account.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTOs para a entidade Account.
 */
public class AccountDTO {

    /**
     * DTO para criação/atualização de conta.
     */
    public record CreateRequest(
            @NotBlank(message = "O nome da conta é obrigatório")
            String name,

            @NotNull(message = "O tipo da conta é obrigatório")
            Account.AccountType type,

            BigDecimal initialBalance
    ) {}

    /**
     * DTO de resposta com dados da conta.
     */
    public record Response(
            Long id,
            String name,
            String type,
            BigDecimal balance,
            String createdAt
    ) {}

    /**
     * DTO de resumo de saldos.
     */
    public record BalanceSummary(
            BigDecimal totalBalance,
            java.util.List<Response> accounts
    ) {}
}
