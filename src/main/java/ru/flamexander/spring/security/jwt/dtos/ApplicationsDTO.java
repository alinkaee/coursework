package ru.flamexander.spring.security.jwt.dtos;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApplicationsDTO {
    private Long id; // Идентификатор заявки
    private Long userId; // Идентификатор пользователя (вместо userEmail)
    private String vacancyTitle; // Название вакансии
    private Date date; // Дата создания заявки
    private String status; // Статус заявки
}

