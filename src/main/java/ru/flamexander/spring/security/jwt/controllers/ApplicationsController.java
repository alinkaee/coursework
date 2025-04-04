package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.entities.Applications;
import ru.flamexander.spring.security.jwt.service.ApplicationsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/applications")
//@RequiredArgsConstructor
public class ApplicationsController {

    private final ApplicationsService applicationsService;

    @Autowired
    public ApplicationsController(ApplicationsService applicationsService) {
        this.applicationsService = applicationsService;
    }

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Applications> createApplication(
            @ModelAttribute ApplicationsService.ApplicationsDTO application
    ) {
        Applications createdApplication = applicationsService.createApplication(application);
        return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Applications> getApplicationById(@PathVariable Long id) {
        Optional<Applications> application = applicationsService.getApplicationById(id);
        return application.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Applications>> getAllApplications() {
        List<Applications> applications = applicationsService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Applications> updateApplication(@PathVariable Long id, @RequestBody ApplicationsService.ApplicationsDTO applicationsDto) {
        Applications updatedApplication = applicationsService.updateApplication(id, applicationsDto);
        return ResponseEntity.ok(updatedApplication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        applicationsService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}


