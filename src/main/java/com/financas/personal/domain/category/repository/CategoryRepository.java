package com.financas.personal.domain.category.repository;

import com.financas.personal.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Busca categorias padrão do sistema (user = null) e as personalizadas do usuário.
     */
    @Query("SELECT c FROM Category c WHERE c.user IS NULL OR c.user.id = :userId")
    List<Category> findAllByUserIdIncludingDefaults(@Param("userId") Long userId);

    List<Category> findByUserId(Long userId);

    List<Category> findByUserIsNull();
}
