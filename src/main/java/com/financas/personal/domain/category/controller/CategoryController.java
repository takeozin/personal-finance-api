package com.financas.personal.domain.category.controller;

import com.financas.personal.domain.category.entity.Category;
import com.financas.personal.domain.category.service.CategoryService;
import com.financas.personal.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para gerenciamento de categorias de transação.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categorias", description = "Gerenciamento de categorias de transação")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Listar todas as categorias (sistema + personalizadas)")
    public ResponseEntity<List<Category>> findAll(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return ResponseEntity.ok(categoryService.findAllByUser(user.getId()));
    }

    @PostMapping
    @Operation(summary = "Criar categoria personalizada")
    public ResponseEntity<Category> create(
            Authentication auth,
            @RequestBody Map<String, String> request) {
        User user = (User) auth.getPrincipal();

        Category category = categoryService.create(
                user.getId(),
                request.get("name"),
                Category.CategoryType.valueOf(request.get("type")),
                request.get("colorHex"),
                request.get("icon")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria personalizada")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        User user = (User) auth.getPrincipal();
        categoryService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
