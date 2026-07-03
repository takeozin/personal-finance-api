package com.financas.personal.domain.transaction.controller;

import com.financas.personal.domain.transaction.entity.TransactionDTO;
import com.financas.personal.domain.transaction.service.TransactionService;
import com.financas.personal.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller para gerenciamento de transações financeiras.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transações", description = "Gerenciamento de receitas, despesas e transferências")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Registrar nova transação")
    public ResponseEntity<TransactionDTO.Response> create(
            Authentication auth,
            @Valid @RequestBody TransactionDTO.CreateRequest request) {
        User user = (User) auth.getPrincipal();
        TransactionDTO.Response response = transactionService.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar transações por período")
    public ResponseEntity<List<TransactionDTO.Response>> findByPeriod(
            Authentication auth,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(transactionService.findByUserAndDateRange(user.getId(), startDate, endDate));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Listar transações de uma conta específica")
    public ResponseEntity<List<TransactionDTO.Response>> findByAccount(
            Authentication auth,
            @PathVariable Long accountId) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(transactionService.findByAccount(user.getId(), accountId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir transação (reverte saldo)")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        User user = (User) auth.getPrincipal();
        transactionService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
