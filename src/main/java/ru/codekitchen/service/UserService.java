package ru.codekitchen.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codekitchen.entity.User;
import ru.codekitchen.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User getCurrentUser() {
        // получим логин (email) из контекста текущего пользователя
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // вернем пользователя из бд по логину из контекста
        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email = " + email + " not found"));
    }
}
