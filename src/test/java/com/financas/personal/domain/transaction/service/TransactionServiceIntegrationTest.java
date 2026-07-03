package com.financas.personal.domain.transaction.service;

import com.financas.personal.domain.account.entity.Account;
import com.financas.personal.domain.account.repository.AccountRepository;
import com.financas.personal.domain.category.entity.Category;
import com.financas.personal.domain.category.repository.CategoryRepository;
import com.financas.personal.domain.transaction.entity.Transaction;
import com.financas.personal.domain.transaction.entity.TransactionDTO;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User testUser;
    private Account testAccount1;
    private Account testAccount2;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("encoded_pass")
                .build());

        testAccount1 = accountRepository.save(Account.builder()
                .user(testUser)
                .name("Main Account")
                .type(Account.AccountType.CURRENT)
                .balance(BigDecimal.valueOf(1000.00))
                .build());

        testAccount2 = accountRepository.save(Account.builder()
                .user(testUser)
                .name("Savings Account")
                .type(Account.AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(500.00))
                .build());

        testCategory = categoryRepository.save(Category.builder()
                .user(testUser)
                .name("Food")
                .colorHex("#FF0000")
                .type(Category.CategoryType.EXPENSE)
                .build());
    }

    @Test
    void shouldCreateIncomeTransactionAndIncreaseBalance() {
        // Arrange
        TransactionDTO.CreateRequest request = new TransactionDTO.CreateRequest(
                testAccount1.getId(),
                null,
                testCategory.getId(),
                "Salary Bonus",
                BigDecimal.valueOf(200.00),
                Transaction.TransactionType.INCOME,
                LocalDate.now()
        );

        // Act
        TransactionDTO.Response response = transactionService.create(testUser.getId(), request);

        // Assert
        assertThat(response.id()).isNotNull();
        assertThat(response.amount()).isEqualByComparingTo("200.00");
        
        Account updatedAccount = accountRepository.findById(testAccount1.getId()).orElseThrow();
        assertThat(updatedAccount.getBalance()).isEqualByComparingTo("1200.00"); // 1000 + 200
    }

    @Test
    void shouldCreateExpenseTransactionAndDecreaseBalance() {
        // Arrange
        TransactionDTO.CreateRequest request = new TransactionDTO.CreateRequest(
                testAccount1.getId(),
                null,
                testCategory.getId(),
                "Grocery",
                BigDecimal.valueOf(150.00),
                Transaction.TransactionType.EXPENSE,
                LocalDate.now()
        );

        // Act
        TransactionDTO.Response response = transactionService.create(testUser.getId(), request);

        // Assert
        assertThat(response.id()).isNotNull();
        
        Account updatedAccount = accountRepository.findById(testAccount1.getId()).orElseThrow();
        assertThat(updatedAccount.getBalance()).isEqualByComparingTo("850.00"); // 1000 - 150
    }

    @Test
    void shouldCreateTransferTransactionAndUpdateBothBalances() {
        // Arrange
        TransactionDTO.CreateRequest request = new TransactionDTO.CreateRequest(
                testAccount1.getId(),
                testAccount2.getId(),
                testCategory.getId(),
                "Transfer to savings",
                BigDecimal.valueOf(300.00),
                Transaction.TransactionType.TRANSFER,
                LocalDate.now()
        );

        // Act
        TransactionDTO.Response response = transactionService.create(testUser.getId(), request);

        // Assert
        assertThat(response.id()).isNotNull();
        
        Account updatedSource = accountRepository.findById(testAccount1.getId()).orElseThrow();
        Account updatedDest = accountRepository.findById(testAccount2.getId()).orElseThrow();
        
        assertThat(updatedSource.getBalance()).isEqualByComparingTo("700.00"); // 1000 - 300
        assertThat(updatedDest.getBalance()).isEqualByComparingTo("800.00"); // 500 + 300
    }
}
