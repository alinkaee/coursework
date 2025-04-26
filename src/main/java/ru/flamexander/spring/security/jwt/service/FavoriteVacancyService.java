package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.repositories.FavoriteVacancyRepository;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteVacancyService {
    private final FavoriteVacancyRepository favoriteVacancyRepository;

    @Transactional
    public void removeVacancyFromFavorites(User user, Vacancy vacancy) {
        favoriteVacancyRepository.findByUserAndVacancy(user, vacancy)
                .ifPresent(favoriteVacancy -> {
                    favoriteVacancyRepository.delete(favoriteVacancy); // Только один вызов удаления
                });
    }
}
