package ru.flamexander.spring.security.jwt.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурационный класс для настройки веб-слоя приложения.
 * Реализует WebMvcConfigurer для кастомизации конфигурации Spring MVC.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Настройка обработчиков статических ресурсов.
     * Регистрирует обработчик для доступа к загруженным файлам через URL /uploads/**.
     *
     * @param registry Реестр обработчиков ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Маппинг URL-пути /uploads/** на файловую систему (папка uploads в корне проекта)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    /**
     * Создает бин для обработки multipart-запросов (загрузка файлов).
     *
     * @return Стандартный резолвер для multipart-запросов
     */
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}