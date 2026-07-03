package com.financas.personal.domain.budget.service;

import com.financas.personal.domain.account.entity.Account;
import com.financas.personal.domain.account.repository.AccountRepository;
import com.financas.personal.domain.budget.entity.BudgetDTO;
import com.financas.personal.domain.category.entity.Category;
import com.financas.personal.domain.category.repository.CategoryRepository;
import com.financas.personal.domain.transaction.entity.Transaction;
import com.financas.personal.domain.transaction.repository.TransactionRepository;
import com.financas.personal.domain.user.entity.User;
import com.financas.personal.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class BudgetServiceIntegrationTest {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User testUser;
    private Category testCategory;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(User.builder()
                .name("Budget Tester")
                .email("budget@example.com")
                .password("encoded_pass")
                .build());

        testCategory = categoryRepository.save(Category.builder()
                .user(testUser)
                .name("Food")
                .colorHex("#FF0000")
                .type(Category.CategoryType.EXPENSE)
                .build());

        testAccount = accountRepository.save(Account.builder()
                .user(testUser)
                .name("Checking")
                .type(Account.AccountType.CURRENT)
                .balance(BigDecimal.valueOf(2000.00))
                .build());
    }

    @Test
    void shouldCreateBudgetAndReturnUnderLimitStatus() {
        // Arrange: limit = 1000, spent = 0
        BudgetDTO.CreateRequest request = new BudgetDTO.CreateRequest(
                testCategory.getId(),
                BigDecimal.valueOf(1000.00),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getYear()
        );

        // Act
        BudgetDTO.Response response = budgetService.create(testUser.getId(), request);

        // Assert
        assertThat(response.id()).isNotNull();
        assertThat(response.spentAmount()).isEqualByComparingTo("0.00");
        assertThat(response.status()).isEqualTo("UNDER_LIMIT");
    }

    @Test
    void shouldReturnWarningStatusWhenSpentIsAbove80Percent() {
        // Arrange
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        // Create budget of 1000
        budgetService.create(testUser.getId(), new BudgetDTO.CreateRequest(
                testCategory.getId(), BigDecimal.valueOf(1000.00), month, year));

        // Create transaction of 850 (85%)
        transactionRepository.save(Transaction.builder()
                .account(testAccount)
                .category(testCategory)
                .description("Groceries")
                .amount(BigDecimal.valueOf(850.00))
                .type(Transaction.TransactionType.EXPENSE)
                .transactionDate(LocalDate.now())
                .build());

        // Act
        List<BudgetDTO.Response> budgets = budgetService.findByUserAndMonth(testUser.getId(), month, year);

        // Assert
        assertThat(budgets).hasSize(1);
        BudgetDTO.Response response = budgets.get(0);
        assertThat(response.spentAmount()).isEqualByComparingTo("850.00");
        assertThat(response.status()).isEqualTo("WARNING");
    }

    @Test
    void shouldReturnOverLimitStatusWhenSpentIsAbove100Percent() {
        // Arrange
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        // Create budget of 500
        budgetService.create(testUser.getId(), new BudgetDTO.CreateRequest(
                testCategory.getId(), BigDecimal.valueOf(500.00), month, year));

        // Create transaction of 600 (120%)
        transactionRepository.save(Transaction.builder()
                .account(testAccount)
                .category(testCategory)
                .description("Groceries")
                .amount(BigDecimal.valueOf(600.00))
                .type(Transaction.TransactionType.EXPENSE)
                .transactionDate(LocalDate.now())
                .build());

        // Act
        List<BudgetDTO.Response> budgets = budgetService.findByUserAndMonth(testUser.getId(), month, year);

        // Assert
        assertThat(budgets).hasSize(1);
        BudgetDTO.Response response = budgets.get(0);
        assertThat(response.spentAmount()).isEqualByComparingTo("600.00");
        assertThat(response.status()).isEqualTo("OVER_LIMIT");
    }
}
