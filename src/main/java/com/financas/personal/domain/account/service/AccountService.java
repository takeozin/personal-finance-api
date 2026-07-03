package com.financas.personal.domain.account.service;

import com.financas.personal.domain.account.entity.Account;
import com.financas.personal.domain.account.entity.AccountDTO;
import com.financas.personal.domain.account.repository.AccountRepository;
import com.financas.personal.domain.user.entity.User;
import com.financas.personal.domain.user.service.UserService;
import com.financas.personal.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio das contas financeiras.
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    /**
     * Cria uma nova conta para o usuário.
     */
    @Transactional
    public AccountDTO.Response create(Long userId, AccountDTO.CreateRequest request) {
        User user = userService.findById(userId);

        Account account = Account.builder()
                .user(user)
                .name(request.name())
                .type(request.type())
                .balance(request.initialBalance() != null ? request.initialBalance() : BigDecimal.ZERO)
                .build();

        account = accountRepository.save(account);
        return toResponse(account);
    }

    /**
     * Lista todas as contas do usuário.
     */
    public List<AccountDTO.Response> findAllByUser(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Retorna o resumo de saldos (total + lista de contas).
     */
    public AccountDTO.BalanceSummary getBalanceSummary(Long userId) {
        BigDecimal totalBalance = accountRepository.getTotalBalanceByUserId(userId);
        List<AccountDTO.Response> accounts = findAllByUser(userId);
        return new AccountDTO.BalanceSummary(totalBalance, accounts);
    }

    /**
     * Busca uma conta pelo ID (uso interno).
     */
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com ID: " + accountId));
    }

    /**
     * Atualiza o saldo da conta (uso interno nas transações).
     */
    @Transactional
    public void updateBalance(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    /**
     * Exclui uma conta do usuário.
     */
    @Transactional
    public void delete(Long userId, Long accountId) {
        Account account = findById(accountId);
        if (!account.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Conta não encontrada");
        }
        accountRepository.delete(account);
    }

    private AccountDTO.Response toResponse(Account account) {
        return new AccountDTO.Response(
                account.getId(),
                account.getName(),
                account.getType().name(),
                account.getBalance(),
                account.getCreatedAt().toString()
        );
    }
}
