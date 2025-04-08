//package ru.flamexander.spring.security.jwt.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class AvatarService {
//    private final Path rootLocation = Paths.get("uploads/avatars");
//
//    @PostConstruct
//    public void init() {
//        try {
//            Files.createDirectories(rootLocation);
//        } catch (IOException e) {
//            throw new RuntimeException("Could not initialize storage", e);
//        }
//    }
//
//    public String store(MultipartFile file) {
//        try {
//            if (file.isEmpty()) {
//                throw new RuntimeException("Failed to store empty file");
//            }
//
//            // Генерируем уникальное имя файла
//            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//            // Копируем файл в целевое место
//            Files.copy(file.getInputStream(),
//                    this.rootLocation.resolve(filename),
//                    StandardCopyOption.REPLACE_EXISTING);
//
//            return filename;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to store file", e);
//        }
//    }
//
//    public Resource load(String filename) {
//        try {
//            Path file = rootLocation.resolve(filename);
//            Resource resource = new UrlResource(file.toUri());
//
//            if (resource.exists() || resource.isReadable()) {
//                return resource;
//            } else {
//                throw new RuntimeException("Could not read file: " + filename);
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("Could not read file: " + filename, e);
//        }
//    }
//
//    public void delete(String filename) {
//        try {
//            Path file = rootLocation.resolve(filename);
//            Files.deleteIfExists(file);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to delete file", e);
//        }
//    }
//}
