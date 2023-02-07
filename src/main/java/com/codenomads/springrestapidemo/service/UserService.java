package com.codenomads.springrestapidemo.service;

import com.codenomads.springrestapidemo.model.User;
import com.codenomads.springrestapidemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> createUserNotFoundException(userId));
    }

    public Page<User> getAllUsersByPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUserById(UUID userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(
                        userRepository::delete,
                        () -> {
                            throw createUserNotFoundException(userId);
                        }
                );
    }

    private ResponseStatusException createUserNotFoundException(UUID userId) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User with id '%s' could not be found.".formatted(userId));
    }
}
