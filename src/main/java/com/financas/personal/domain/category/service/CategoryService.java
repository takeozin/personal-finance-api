package com.financas.personal.domain.category.service;

import com.financas.personal.domain.category.entity.Category;
import com.financas.personal.domain.category.repository.CategoryRepository;
import com.financas.personal.domain.user.entity.User;
import com.financas.personal.domain.user.service.UserService;
import com.financas.personal.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio das categorias de transação.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    /**
     * Lista todas as categorias disponíveis para o usuário (sistema + personalizadas).
     */
    public List<Category> findAllByUser(Long userId) {
        return categoryRepository.findAllByUserIdIncludingDefaults(userId);
    }

    /**
     * Cria uma categoria personalizada para o usuário.
     */
    @Transactional
    public Category create(Long userId, String name, Category.CategoryType type, String colorHex, String icon) {
        User user = userService.findById(userId);

        Category category = Category.builder()
                .user(user)
                .name(name)
                .type(type)
                .colorHex(colorHex != null ? colorHex : "#6366F1")
                .icon(icon)
                .build();

        return categoryRepository.save(category);
    }

    /**
     * Busca uma categoria pelo ID (uso interno).
     */
    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + categoryId));
    }

    /**
     * Exclui uma categoria personalizada do usuário.
     */
    @Transactional
    public void delete(Long userId, Long categoryId) {
        Category category = findById(categoryId);
        if (category.getUser() == null || !category.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Categoria não encontrada ou é uma categoria do sistema");
        }
        categoryRepository.delete(category);
    }
}
