package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;

import java.util.Collections;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // Используйте email как username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), // Используйте email как username для UserDetails
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
