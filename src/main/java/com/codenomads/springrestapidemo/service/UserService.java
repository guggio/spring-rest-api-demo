package com.codenomads.springrestapidemo.service;

import com.codenomads.springrestapidemo.controller.error.NotFoundException;
import com.codenomads.springrestapidemo.dto.UserDto;
import com.codenomads.springrestapidemo.dto.UserDtoMapper;
import com.codenomads.springrestapidemo.model.User;
import com.codenomads.springrestapidemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public UserDto getUserById(long userId) {
        return userRepository.findById(userId)
                .map(userDtoMapper::toDto)
                .orElseThrow(() -> new NotFoundException(String.format("User id '%d' does not exist", userId)));
    }

    public Page<UserDto> getAllUsersByPage(int pageNumber, int pageSize) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(pageNumber, pageSize));
        List<UserDto> userDtos = userPage.getContent().stream()
                .map(userDtoMapper::toDto)
                .toList();
        return new PageImpl<>(userDtos, userPage.getPageable(), userPage.getTotalPages());
    }

    public UserDto createUser(UserDto userDto) {
        User user = userDtoMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        return userDtoMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto createOrUpdateUser(UserDto userDto) {
        return userRepository.findById(userDto.getId())
                .map(user -> updateNameAndBirthDate(user, userDto))
                .map(userDtoMapper::toDto)
                .orElseGet(() -> createUser(userDto));
    }

    private User updateNameAndBirthDate(User user, UserDto userDto) {
        user.setName(userDto.getName());
        user.setBirthDate(userDto.getBirthDate());
        return user;
    }

    public void deleteUserById(long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(
                        userRepository::delete,
                        () -> {
                            throw new NotFoundException(String.format("User id '%d' does not exist", userId));
                        }
                );
    }
}
