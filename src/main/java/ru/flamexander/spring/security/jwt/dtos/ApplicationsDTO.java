package ru.flamexander.spring.security.jwt.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationsDTO {
    private Long id;
    private UserDto user;
    private VacancyDto vacancy;
    private Date date;
    private String status;
}
