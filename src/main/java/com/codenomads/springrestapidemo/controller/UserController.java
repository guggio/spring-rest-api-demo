package com.codenomads.springrestapidemo.controller;

import com.codenomads.springrestapidemo.dto.UserDto;
import com.codenomads.springrestapidemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(value = "users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable @Positive long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    public Page<UserDto> getAllUsersByPage(
            @RequestParam(defaultValue = "1", required = false)
            @Positive
            @Max(10000)
            int pageNumber,
            @RequestParam(defaultValue = "50", required = false)
            @Positive
            @Max(500)
            int pageSize
    ) {
        return userService.getAllUsersByPage(pageNumber - 1, pageSize);
    }

    @PostMapping
    public UserDto createUser(
            @RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PutMapping
    public UserDto createOrUpdateUser(@RequestBody @Valid UserDto userDto) {
        return userService.createOrUpdateUser(userDto);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

}
