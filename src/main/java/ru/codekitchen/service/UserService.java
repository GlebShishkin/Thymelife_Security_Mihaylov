package ru.codekitchen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.codekitchen.entity.User;
import ru.codekitchen.entity.UserRole;
import ru.codekitchen.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public List<User> findAllByRoleIn(Iterable<UserRole> roles) {
        return userRepository.findAllByRoleInOrderById(roles);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void updateRole(int id, UserRole newRole) {
        userRepository.updateRole(id, newRole);
    }

    public void deleteById(int id){
        userRepository.deleteById(id);
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
