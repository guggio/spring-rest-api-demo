package com.codenomads.springrestapidemo.controller;

import com.codenomads.springrestapidemo.dto.UserDto;
import com.codenomads.springrestapidemo.dto.UserDtoMapper;
import com.codenomads.springrestapidemo.model.User;
import com.codenomads.springrestapidemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping(value = "users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @GetMapping("{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserByEmail(@PathVariable
                                  @NotEmpty
                                  @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                                  String email) {
        User user = userService.getUserByEmail(email);
        return userDtoMapper.toDto(user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> getAllUsersByPage(@PageableDefault(size = 50) Pageable pageable) {
        return userService.getAllUsersByPage(pageable)
                .map(userDtoMapper::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        User userToCreate = userDtoMapper.toUser(userDto);
        User savedUser = userService.createUser(userToCreate);
        return userDtoMapper.toDto(savedUser);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto createOrUpdateUser(@RequestBody @Valid UserDto userDto) {
        User userToCreateOrUpdate = userDtoMapper.toUser(userDto);
        User savedUser = userService.createOrUpdateUser(userToCreateOrUpdate);
        return userDtoMapper.toDto(savedUser);
    }

    @DeleteMapping("{email}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable
                           @NotEmpty
                           @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                           String email) {
        userService.deleteUserByEmail(email);
    }

}
