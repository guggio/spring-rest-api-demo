package com.codenomads.springrestapidemo.dto;

import com.codenomads.springrestapidemo.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public User toUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .birthDate(userDto.getBirthDate())
                .build();
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getName(), user.getEmail(), user.getBirthDate());
    }
}
