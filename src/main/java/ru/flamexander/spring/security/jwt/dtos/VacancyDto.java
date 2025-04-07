package ru.flamexander.spring.security.jwt.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PositiveOrZero;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacancyDto {
    private Long id;
    private String title;
    private String description;
//    @NotNull(message = "Зарплата обязательна")
    @PositiveOrZero(message = "Зарплата должна быть положительной")
    private Integer salary;

//    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    private boolean hasApplied;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSalary() { return salary; }
    public void setSalary(Integer salary) { this.salary = salary; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public boolean isHasApplied() {
        return hasApplied;
    }

    public void setHasApplied(boolean hasApplied) {
        this.hasApplied = hasApplied;
    }


}
