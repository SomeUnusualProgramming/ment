package com.document.analyzer.service;

import com.document.analyzer.entity.User;
import com.document.analyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        TODO_ADD_PASSWORD_HASHING_AND_VALIDATION();
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(Long id, User userDetails) {
        TODO_IMPLEMENT_UPDATE_WITH_VALIDATION();
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(userDetails.getFirstName());
                    user.setLastName(userDetails.getLastName());
                    user.setRole(userDetails.getRole());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        TODO_ADD_SOFT_DELETE_LOGIC();
        userRepository.deleteById(id);
    }

    public boolean authenticateUser(String email, String password) {
        TODO_IMPLEMENT_PASSWORD_VERIFICATION();
        return false;
    }

    public User activateUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(true);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User deactivateUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(false);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void TODO_ADD_PASSWORD_HASHING_AND_VALIDATION() {
    }

    private void TODO_IMPLEMENT_UPDATE_WITH_VALIDATION() {
    }

    private void TODO_ADD_SOFT_DELETE_LOGIC() {
    }

    private void TODO_IMPLEMENT_PASSWORD_VERIFICATION() {
    }
}
