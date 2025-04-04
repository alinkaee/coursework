package ru.flamexander.spring.security.jwt.constants;

import java.util.List;

public interface SecurityConstants {
    // Список ресурсов, доступных всем
    public static final List<String> RESOURCES_WHITE_LIST = List.of(
            "/resources/**",
            "/static/**",
            "/css/**",
            "/images/**",
            "/",
            "/swagger-ui/**",
            "/webjars/bootstrap/5.3.3/**",
            "/v3/api-docs/**"
    );

    // Список URL для пользователей
    public static final List<String> USERS_WHITE_LIST = List.of(
            "/login",
            "/"
    );

    // Список URL для администратора
    public static final List<String> ADMIN_ACCESS = List.of(
            "/admin/users", // Просмотр списка пользователей
            "/admin/users/{id}", // Просмотр информации о пользователе
            "/admin/users/delete/{id}", // Удаление пользователя
            "/admin/statements", // Просмотр списка заявлений
            "/admin/statements/{id}", // Просмотр информации о заявлении
            "/admin/statements/delete/{id}", // Удаление заявления
            "/admin/statements/edit/{id}", // Редактирование заявления
            "/admin/statements/add", // Добавление нового заявления
            "categories/view-all-categories"
    );
}
