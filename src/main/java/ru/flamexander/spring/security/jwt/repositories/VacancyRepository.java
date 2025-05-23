package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.Category;
import ru.flamexander.spring.security.jwt.entities.Vacancy;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {

    // Правильный метод поиска по названию категории
    @Query("SELECT v FROM Vacancy v JOIN v.category c WHERE c.title = :categoryName")
    List<Vacancy> findByCategoryTitle(@Param("categoryName") String categoryName);

    // Альтернативный вариант (используя соглашение об именовании)
    List<Vacancy> findByCategory_Title(String title);

    // Поиск вакансий по части названия (игнорируя регистр)
    List<Vacancy> findByTitleContainingIgnoreCase(String title);

    // Проверка существования вакансии по названию
    boolean existsByTitle(String title);

    Page<Vacancy> findByCategory(Category category, Pageable pageable);

    List<Vacancy> findByCategory(Category category);

    Page<Vacancy> findByCategoryAndTitleContainingIgnoreCase(Category category, String title, Pageable pageable);

    Page<Vacancy> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Optional<Vacancy> findVacancyByTitle(String title);

    // Альтернативный метод для поиска по части названия (игнорируя регистр)
    List<Vacancy> findByTitleIgnoreCase(String title);


    @Query("SELECT v FROM Vacancy v WHERE v.category.title = :categoryName AND v.title LIKE %:query%")
    List<Vacancy> findByCategoryAndTitle(@Param("categoryName") String categoryName, @Param("query") String query);

    @Query("SELECT v FROM Vacancy v ORDER BY v.salary ASC")
    List<Vacancy> findAllOrderBySalaryAsc();

    @Query("SELECT v FROM Vacancy v ORDER BY v.salary DESC")
    List<Vacancy> findAllOrderBySalaryDesc();

}

