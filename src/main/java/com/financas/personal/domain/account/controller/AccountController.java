package com.financas.personal.domain.account.controller;

import com.financas.personal.domain.account.entity.AccountDTO;
import com.financas.personal.domain.account.service.AccountService;
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

import java.util.List;

/**
 * Controller para gerenciamento de contas financeiras.
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Contas", description = "Gerenciamento de contas financeiras")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Criar nova conta")
    public ResponseEntity<AccountDTO.Response> create(
            Authentication auth,
            @Valid @RequestBody AccountDTO.CreateRequest request) {
        User user = (User) auth.getPrincipal();
        AccountDTO.Response response = accountService.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as contas do usuário")
    public ResponseEntity<List<AccountDTO.Response>> findAll(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(accountService.findAllByUser(user.getId()));
    }

    @GetMapping("/summary")
    @Operation(summary = "Obter resumo de saldos (total + por conta)")
    public ResponseEntity<AccountDTO.BalanceSummary> getBalanceSummary(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(accountService.getBalanceSummary(user.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir conta")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        User user = (User) auth.getPrincipal();
        accountService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
