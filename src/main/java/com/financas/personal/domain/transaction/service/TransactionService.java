package com.financas.personal.domain.transaction.service;

import com.financas.personal.domain.account.entity.Account;
import com.financas.personal.domain.account.service.AccountService;
import com.financas.personal.domain.category.entity.Category;
import com.financas.personal.domain.category.service.CategoryService;
import com.financas.personal.domain.transaction.entity.Transaction;
import com.financas.personal.domain.transaction.entity.TransactionDTO;
import com.financas.personal.domain.transaction.repository.TransactionRepository;
import com.financas.personal.exception.BusinessException;
import com.financas.personal.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de transações financeiras.
 * Gerencia receitas, despesas e transferências entre contas.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CategoryService categoryService;

    /**
     * Cria uma nova transação e atualiza os saldos das contas envolvidas.
     */
    @Transactional
    public TransactionDTO.Response create(Long userId, TransactionDTO.CreateRequest request) {
        Account account = accountService.findById(request.accountId());
        validateAccountOwnership(account, userId);

        Category category = categoryService.findById(request.categoryId());

        Transaction transaction = Transaction.builder()
                .account(account)
                .category(category)
                .description(request.description())
                .amount(request.amount())
                .type(request.type())
                .transactionDate(request.transactionDate())
                .build();

        switch (request.type()) {
            case INCOME -> {
                // Receita: soma ao saldo da conta
                accountService.updateBalance(account, request.amount());
            }
            case EXPENSE -> {
                // Despesa: subtrai do saldo da conta
                accountService.updateBalance(account, request.amount().negate());
            }
            case TRANSFER -> {
                if (request.destinationAccountId() == null) {
                    throw new BusinessException("A conta de destino é obrigatória para transferências");
                }
                Account destAccount = accountService.findById(request.destinationAccountId());
                validateAccountOwnership(destAccount, userId);

                if (account.getId().equals(destAccount.getId())) {
                    throw new BusinessException("A conta de origem e destino não podem ser a mesma");
                }

                transaction.setDestinationAccount(destAccount);

                // Subtrai da origem e soma ao destino
                accountService.updateBalance(account, request.amount().negate());
                accountService.updateBalance(destAccount, request.amount());
            }
        }

        transaction = transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    /**
     * Lista transações do usuário por período.
     */
    public List<TransactionDTO.Response> findByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Lista transações de uma conta específica.
     */
    public List<TransactionDTO.Response> findByAccount(Long userId, Long accountId) {
        Account account = accountService.findById(accountId);
        validateAccountOwnership(account, userId);

        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Exclui uma transação e reverte o saldo.
     */
    @Transactional
    public void delete(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        validateAccountOwnership(transaction.getAccount(), userId);

        // Reverter os saldos
        switch (transaction.getType()) {
            case INCOME -> accountService.updateBalance(transaction.getAccount(), transaction.getAmount().negate());
            case EXPENSE -> accountService.updateBalance(transaction.getAccount(), transaction.getAmount());
            case TRANSFER -> {
                accountService.updateBalance(transaction.getAccount(), transaction.getAmount());
                if (transaction.getDestinationAccount() != null) {
                    accountService.updateBalance(transaction.getDestinationAccount(), transaction.getAmount().negate());
                }
            }
        }

        transactionRepository.delete(transaction);
    }

    private void validateAccountOwnership(Account account, Long userId) {
        if (!account.getUser().getId().equals(userId)) {
            throw new BusinessException("Você não tem permissão para acessar esta conta");
        }
    }

    private TransactionDTO.Response toResponse(Transaction t) {
        return new TransactionDTO.Response(
                t.getId(),
                t.getAccount().getId(),
                t.getAccount().getName(),
                t.getDestinationAccount() != null ? t.getDestinationAccount().getId() : null,
                t.getDestinationAccount() != null ? t.getDestinationAccount().getName() : null,
                t.getCategory().getId(),
                t.getCategory().getName(),
                t.getCategory().getColorHex(),
                t.getDescription(),
                t.getAmount(),
                t.getType().name(),
                t.getTransactionDate(),
                t.getCreatedAt().toString()
        );
    }
}
