package ru.flamexander.spring.security.jwt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.VacancyDto;
import ru.flamexander.spring.security.jwt.service.VacancyService;

import java.util.List;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping("/add")
    public ResponseEntity<VacancyDto> createVacancy(@RequestBody VacancyDto vacancyDto) {
        return ResponseEntity.ok(vacancyService.createVacancy(vacancyDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VacancyDto> updateVacancy(
            @PathVariable Long id,
            @RequestBody VacancyDto vacancyDto) {
        return ResponseEntity.ok(vacancyService.updateVacancy(id, vacancyDto));
    }

    @GetMapping
    public ResponseEntity<List<VacancyDto>> getAllVacancy() {
        return ResponseEntity.ok(vacancyService.getAllVacancy()); // было gatAllVacancy()
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacancyDto> getVacancyById(@PathVariable Long id) {
        return ResponseEntity.ok(vacancyService.getVacancyById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable Long id) {
        vacancyService.deleteVacancy(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<VacancyDto>> searchVacancy(@RequestParam String name) {
        return ResponseEntity.ok(vacancyService.searchVacancy(name));
    }

}
