package com.financas.personal.config;

import com.financas.personal.domain.category.entity.Category;
import com.financas.personal.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Inicializador de dados padrão do sistema.
 * Insere categorias padrão quando o banco está vazio.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.findByUserIsNull().isEmpty()) {
            log.info("Inserindo categorias padrão do sistema...");

            List<Category> defaultCategories = List.of(
                    // Categorias de Receita
                    buildCategory("Salário", Category.CategoryType.INCOME, "#22C55E", "💰"),
                    buildCategory("Freelance", Category.CategoryType.INCOME, "#10B981", "💻"),
                    buildCategory("Investimentos", Category.CategoryType.INCOME, "#06B6D4", "📈"),
                    buildCategory("Outros (Receita)", Category.CategoryType.INCOME, "#8B5CF6", "📥"),

                    // Categorias de Despesa
                    buildCategory("Alimentação", Category.CategoryType.EXPENSE, "#EF4444", "🍔"),
                    buildCategory("Transporte", Category.CategoryType.EXPENSE, "#F97316", "🚗"),
                    buildCategory("Moradia", Category.CategoryType.EXPENSE, "#F59E0B", "🏠"),
                    buildCategory("Saúde", Category.CategoryType.EXPENSE, "#EC4899", "🏥"),
                    buildCategory("Educação", Category.CategoryType.EXPENSE, "#6366F1", "📚"),
                    buildCategory("Lazer", Category.CategoryType.EXPENSE, "#8B5CF6", "🎮"),
                    buildCategory("Vestuário", Category.CategoryType.EXPENSE, "#D946EF", "👕"),
                    buildCategory("Contas Fixas", Category.CategoryType.EXPENSE, "#64748B", "📄"),
                    buildCategory("Outros (Despesa)", Category.CategoryType.EXPENSE, "#94A3B8", "📤")
            );

            categoryRepository.saveAll(defaultCategories);
            log.info("{} categorias padrão inseridas com sucesso.", defaultCategories.size());
        }
    }

    private Category buildCategory(String name, Category.CategoryType type, String color, String icon) {
        return Category.builder()
                .user(null) // Categoria do sistema
                .name(name)
                .type(type)
                .colorHex(color)
                .icon(icon)
                .build();
    }
}
