package com.financas.personal.domain.budget.service;

import com.financas.personal.domain.budget.entity.Budget;
import com.financas.personal.domain.budget.entity.BudgetDTO;
import com.financas.personal.domain.budget.repository.BudgetRepository;
import com.financas.personal.domain.category.entity.Category;
import com.financas.personal.domain.category.service.CategoryService;
import com.financas.personal.domain.transaction.repository.TransactionRepository;
import com.financas.personal.domain.user.entity.User;
import com.financas.personal.domain.user.service.UserService;
import com.financas.personal.exception.DuplicateResourceException;
import com.financas.personal.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de orçamentos mensais.
 * Permite definir limites de gastos por categoria e monitorar o progresso.
 */
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    /**
     * Cria um novo orçamento para uma categoria em um mês/ano específico.
     */
    @Transactional
    public BudgetDTO.Response create(Long userId, BudgetDTO.CreateRequest request) {
        User user = userService.findById(userId);
        Category category = categoryService.findById(request.categoryId());

        // Verificar se já existe um orçamento para esta combinação
        budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                userId, request.categoryId(), request.month(), request.year()
        ).ifPresent(existing -> {
            throw new DuplicateResourceException(
                    "Já existe um orçamento para esta categoria no mês/ano especificado");
        });

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .limitAmount(request.limitAmount())
                .month(request.month())
                .year(request.year())
                .build();

        budget = budgetRepository.save(budget);
        return toResponse(budget, userId);
    }

    /**
     * Lista todos os orçamentos do usuário para um mês/ano,
     * incluindo progresso de gastos.
     */
    public List<BudgetDTO.Response> findByUserAndMonth(Long userId, int month, int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .stream()
                .map(budget -> toResponse(budget, userId))
                .toList();
    }

    /**
     * Atualiza o limite de um orçamento existente.
     */
    @Transactional
    public BudgetDTO.Response update(Long userId, Long budgetId, BigDecimal newLimit) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado"));

        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Orçamento não encontrado");
        }

        budget.setLimitAmount(newLimit);
        budget = budgetRepository.save(budget);
        return toResponse(budget, userId);
    }

    /**
     * Exclui um orçamento.
     */
    @Transactional
    public void delete(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado"));

        if (!budget.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Orçamento não encontrado");
        }

        budgetRepository.delete(budget);
    }

    private BudgetDTO.Response toResponse(Budget budget, Long userId) {
        BigDecimal spent = transactionRepository.sumExpensesByCategoryAndMonth(
                userId, budget.getCategory().getId(), budget.getMonth(), budget.getYear());

        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        double percentUsed = spent.divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        String status;
        if (percentUsed >= 100) {
            status = "OVER_LIMIT";
        } else if (percentUsed >= 80) {
            status = "WARNING";
        } else {
            status = "UNDER_LIMIT";
        }

        return new BudgetDTO.Response(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                budget.getCategory().getColorHex(),
                budget.getLimitAmount(),
                spent,
                remaining,
                percentUsed,
                budget.getMonth(),
                budget.getYear(),
                status
        );
    }
}
