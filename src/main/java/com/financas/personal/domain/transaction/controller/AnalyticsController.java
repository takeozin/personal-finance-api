package com.financas.personal.domain.transaction.controller;

import com.financas.personal.domain.transaction.repository.TransactionRepository;
import com.financas.personal.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controller para relatórios e análises financeiras.
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Relatórios", description = "Análises e resumos financeiros")
public class AnalyticsController {

    private final TransactionRepository transactionRepository;

    @GetMapping("/monthly-summary")
    @Operation(summary = "Resumo financeiro mensal (receitas vs despesas)")
    public ResponseEntity<Map<String, Object>> getMonthlySummary(
            Authentication auth,
            @RequestParam int month,
            @RequestParam int year) {

        User user = (User) auth.getPrincipal();
        Long userId = user.getId();

        BigDecimal totalIncome = transactionRepository.sumIncomeByMonth(userId, month, year);
        BigDecimal totalExpenses = transactionRepository.sumExpensesByMonth(userId, month, year);
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        Map<String, Object> summary = Map.of(
                "month", month,
                "year", year,
                "totalIncome", totalIncome,
                "totalExpenses", totalExpenses,
                "balance", balance,
                "savingsRate", totalIncome.compareTo(BigDecimal.ZERO) > 0
                        ? balance.multiply(BigDecimal.valueOf(100))
                                .divide(totalIncome, 2, java.math.RoundingMode.HALF_UP)
                        : BigDecimal.ZERO
        );

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/category-expenses")
    @Operation(summary = "Despesas agrupadas por categoria")
    public ResponseEntity<List<TransactionRepository.CategorySpending>> getCategoryExpenses(
            Authentication auth,
            @RequestParam int month,
            @RequestParam int year) {

        User user = (User) auth.getPrincipal();
        List<TransactionRepository.CategorySpending> spending = transactionRepository
                .sumExpensesByCategoryGrouped(user.getId(), month, year);

        return ResponseEntity.ok(spending);
    }
}
