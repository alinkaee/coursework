package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.FavoriteVacancy;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.entities.Vacancy;
import ru.flamexander.spring.security.jwt.repositories.FavoriteVacancyRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteVacancyService {
    private final FavoriteVacancyRepository favoriteVacancyRepository;


    @Transactional
    public void removeVacancyFromFavorites(Long userId, Long vacancyId) {
        int deleted = favoriteVacancyRepository.deleteByUserAndVacancy(userId, vacancyId);
        if (deleted == 0) {
            throw new IllegalStateException("Связь не найдена");
        }
        System.out.println("Удалено записей: " + deleted);
    }

    public List<Vacancy> getUserFavoriteVacancies(User user) {
        return favoriteVacancyRepository.findByUser(user).stream()
                .map(FavoriteVacancy::getVacancy)
                .collect(Collectors.toList());
    }
}
