package com.codenomads.springrestapidemo.service;

import com.codenomads.springrestapidemo.model.User;
import com.codenomads.springrestapidemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> createUserNotFoundException(email));
    }

    public Page<User> getAllUsersByPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User createOrUpdateUser(User user) {
        return userRepository.findByEmail(user.getEmail())
                .map(userToUpdate -> {
                    userToUpdate.setName(user.getName());
                    userToUpdate.setBirthDate(user.getBirthDate());
                    return userToUpdate;
                })
                .orElseGet(() -> userRepository.save(user));
    }

    public void deleteUserByEmail(String email) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(userRepository::delete,
                        () -> {
                            throw createUserNotFoundException(email);
                        });
    }

    private ResponseStatusException createUserNotFoundException(String email) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User with email '%s' could not be found.".formatted(email));
    }
}
