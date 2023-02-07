package com.codenomads.springrestapidemo.controller;

import com.codenomads.springrestapidemo.dto.UserDto;
import com.codenomads.springrestapidemo.dto.UserDtoMapper;
import com.codenomads.springrestapidemo.model.User;
import com.codenomads.springrestapidemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(value = "users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable UUID userId) {
        User user = userService.getUserById(userId);
        return userDtoMapper.toDto(user);
    }

    @GetMapping
    public Page<UserDto> getAllUsersByPage(@PageableDefault(size = 50) Pageable pageable) {
        return userService.getAllUsersByPage(pageable)
                .map(userDtoMapper::toDto);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return saveUser(userDto);
    }

    @PutMapping
    public UserDto createOrUpdateUser(@RequestBody @Valid UserDto userDto) {
        return saveUser(userDto);
    }

    private UserDto saveUser(UserDto userDto) {
        User userToCreate = userDtoMapper.toUser(userDto);
        User savedUser = userService.createUser(userToCreate);
        return userDtoMapper.toDto(savedUser);
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUserById(userId);
    }

}
