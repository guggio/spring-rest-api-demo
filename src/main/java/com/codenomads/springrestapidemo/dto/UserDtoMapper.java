package com.codenomads.springrestapidemo.dto;

import com.codenomads.springrestapidemo.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .birthDate(userDto.getBirthDate())
                .build();
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getBirthDate());
    }
}
