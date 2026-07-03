package com.financas.personal.domain.budget.controller;

import com.financas.personal.domain.budget.entity.BudgetDTO;
import com.financas.personal.domain.budget.service.BudgetService;
import com.financas.personal.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller para gerenciamento de orçamentos mensais.
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orçamentos", description = "Gerenciamento de metas de gastos mensais")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Criar novo orçamento mensal")
    public ResponseEntity<BudgetDTO.Response> create(
            Authentication auth,
            @Valid @RequestBody BudgetDTO.CreateRequest request) {
        User user = (User) auth.getPrincipal();
        BudgetDTO.Response response = budgetService.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar orçamentos de um mês/ano com progresso de gastos")
    public ResponseEntity<List<BudgetDTO.Response>> findByMonth(
            Authentication auth,
            @RequestParam int month,
            @RequestParam int year) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(budgetService.findByUserAndMonth(user.getId(), month, year));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar limite do orçamento")
    public ResponseEntity<BudgetDTO.Response> update(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request) {
        User user = (User) auth.getPrincipal();
        BudgetDTO.Response response = budgetService.update(user.getId(), id, request.get("limitAmount"));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir orçamento")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        User user = (User) auth.getPrincipal();
        budgetService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
