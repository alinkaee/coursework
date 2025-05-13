package ru.flamexander.spring.security.jwt.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationUserDto {
//    @NotBlank(message = "Имя не может быть пустым")
    private String username;
//    @NotBlank(message = "Пароль не может быть пустым")
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$", message = "Пароль должен содержать минимум 8 символов, одну цифру, одну заглавную и одну строчную букву")
    private String password;
//    @NotBlank(message = "Подтверждение пароля не может быть пустым")
    private String confirmPassword;
//    @Email(message = "Некорректный email")
//    @NotBlank(message = "Email не может быть пустым")
    private String email;
}
