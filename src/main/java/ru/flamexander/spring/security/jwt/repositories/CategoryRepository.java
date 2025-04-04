package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Метод для поиска по точному совпадению названия
    Optional<Category> findByTitle(String title);

    // Метод для поиска без учета регистра
    Optional<Category> findByTitleIgnoreCase(String title);

    // Метод для поиска содержащего подстроку
    List<Category> findByTitleContaining(String title);

    @EntityGraph(attributePaths = "vacancies")
    @Query("SELECT c FROM Category c")
    List<Category> findAllWithVacancies();

}