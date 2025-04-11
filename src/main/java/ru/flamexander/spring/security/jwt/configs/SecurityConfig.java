package ru.flamexander.spring.security.jwt.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.flamexander.spring.security.jwt.service.UserService;

import static ru.flamexander.spring.security.jwt.constants.SecurityConstants.ADMIN_ACCESS;
import static ru.flamexander.spring.security.jwt.constants.SecurityConstants.USERS_WHITE_LIST;
import static ru.flamexander.spring.security.jwt.constants.UserRoleConstant.ADMIN;

// Включаем поддержку WebSecurity и глобальную безопасность методов
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    // Сервис для работы с пользователями
    private UserService userService;



    // Внедрение зависимостей через сеттеры
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // Конфигурация цепочки фильтров безопасности
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем защиту от CSRF-атак
                .csrf().disable()
                // Отключаем CORS (Cross-Origin Resource Sharing)
                .cors().disable()
                // Устанавливаем правила доступа к запросам
                .authorizeHttpRequests((authz) -> authz
                        // Разрешаем доступ к странице логина без аутентификации
//                        .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
//                        .antMatchers("/login").permitAll()
                          .antMatchers("/").permitAll()
                          .antMatchers("/logout").authenticated()
                        // Разрешаем доступ к странице выхода без аутентификации
//                        .requestMatchers(new AntPathRequestMatcher("/logout")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/vacancies/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/categories/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/applications/apply/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/favorites/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/user/edit/**")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/user/update/**")).authenticated()
                        // Требуем аутентификацию для доступа к странице "/secured"
                        .requestMatchers(new AntPathRequestMatcher("/secured")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                        // Требуем аутентификацию для доступа к странице "/info"
                        .requestMatchers(new AntPathRequestMatcher("/info")).authenticated()
                        // Требуем роль "ADMIN" для доступа к странице "/admin"
                        .requestMatchers(ADMIN_ACCESS.stream()
                                .map(AntPathRequestMatcher::new)
                                .toArray(RequestMatcher[]::new))
                        .hasRole("ADMIN")
                        .requestMatchers(USERS_WHITE_LIST.stream()
                                .map(AntPathRequestMatcher::new)
                                .toArray(RequestMatcher[]::new))
                        .hasRole("USER")
                        // Разрешаем доступ ко всем остальным запросам без аутентификации
                        .anyRequest().permitAll()
                )
                // Конфигурация формы логина
                .formLogin(form -> form
                        // Устанавливаем страницу логина
                        .loginPage("/login")
                        // Устанавливаем URL для перенаправления после успешного логина
                        .defaultSuccessUrl("/index", true)
                        // Разрешаем доступ к форме логина без аутентификации
                        .permitAll()
                )
                // Конфигурация выхода из системы
                .logout((logout) -> logout
                        // Устанавливаем URL для выхода из системы
                        .logoutUrl("/logout")
                        // Устанавливаем URL для перенаправления после выхода
                        .logoutSuccessUrl("/login")
                        // Удаляем сессию после выхода
                        .invalidateHttpSession(true)
                        // Удаляем cookie сессии
                        .deleteCookies("JSESSIONID")
                        // Устанавливаем матчер для запроса на выход
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                );
        return http.build();
    }

    // Конфигурация провайдера аутентификации
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // Устанавливаем парсер паролей
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        // Устанавливаем сервис для загрузки пользователей
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    // Конфигурация парсера паролей
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Используем BCrypt для хеширования паролей
        return new BCryptPasswordEncoder();
    }

    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/uploads/**");
    }

    // Конфигурация менеджера аутентификации
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Получаем менеджер аутентификации из конфигурации
        return authenticationConfiguration.getAuthenticationManager();
    }
}
