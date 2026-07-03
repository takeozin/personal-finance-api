package com.financas.personal.domain.transaction.repository;

import com.financas.personal.domain.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Busca transações de uma conta específica, ordenadas por data decrescente.
     */
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);

    /**
     * Busca transações do usuário em um período específico.
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Soma os gastos de uma categoria em um mês/ano específico (para comparação com orçamento).
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.category.id = :categoryId " +
           "AND t.type = 'EXPENSE' " +
           "AND MONTH(t.transactionDate) = :month " +
           "AND YEAR(t.transactionDate) = :year")
    BigDecimal sumExpensesByCategoryAndMonth(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("month") int month,
            @Param("year") int year);

    /**
     * Soma total de receitas do usuário em um mês/ano.
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.type = 'INCOME' " +
           "AND MONTH(t.transactionDate) = :month " +
           "AND YEAR(t.transactionDate) = :year")
    BigDecimal sumIncomeByMonth(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);

    /**
     * Soma total de despesas do usuário em um mês/ano.
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.type = 'EXPENSE' " +
           "AND MONTH(t.transactionDate) = :month " +
           "AND YEAR(t.transactionDate) = :year")
    BigDecimal sumExpensesByMonth(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);

    interface CategorySpending {
        String getCategoryName();
        String getColorHex();
        BigDecimal getTotalAmount();
    }

    /**
     * Soma despesas agrupadas por categoria.
     */
    @Query("SELECT t.category.name AS categoryName, t.category.colorHex AS colorHex, SUM(t.amount) AS totalAmount " +
           "FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.type = 'EXPENSE' " +
           "AND MONTH(t.transactionDate) = :month " +
           "AND YEAR(t.transactionDate) = :year " +
           "GROUP BY t.category.name, t.category.colorHex " +
           "ORDER BY totalAmount DESC")
    List<CategorySpending> sumExpensesByCategoryGrouped(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year);
}
