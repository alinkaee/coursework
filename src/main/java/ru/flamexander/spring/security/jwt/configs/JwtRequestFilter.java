package ru.flamexander.spring.security.jwt.configs;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.flamexander.spring.security.jwt.utils.JwtTokenUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Фильтр для обработки JWT-токенов в запросах.
 * Наследуется от OncePerRequestFilter, чтобы гарантировать однократное выполнение для каждого запроса.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;

    /**
     * Основной метод фильтрации, обрабатывающий каждый HTTP-запрос.
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param filterChain цепочка фильтров для продолжения обработки
     * @throws ServletException в случае ошибок сервлета
     * @throws IOException в случае IO-ошибок
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Получаем заголовок Authorization из запроса
        String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Проверяем наличие и формат заголовка Authorization
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Извлекаем JWT-токен (удаляем префикс "Bearer ")
            jwt = authHeader.substring(7);
            try {
                // Получаем имя пользователя из токена
                username = jwtTokenUtils.getUsername(jwt);
            } catch (ExpiredJwtException e) {
                // Логируем истечение срока действия токена
                log.debug("Время жизни токена вышло");
            } catch (SignatureException e) {
                // Логируем неверную подпись токена
                log.debug("Подпись неправильная");
            }
        }

        // Если пользователь найден и контекст безопасности еще не установлен
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Создаем объект аутентификации с именем пользователя и его ролями
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtTokenUtils.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            // Устанавливаем аутентификацию в контекст безопасности
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
}