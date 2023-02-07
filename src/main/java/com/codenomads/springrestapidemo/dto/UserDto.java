package com.codenomads.springrestapidemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotEmpty
    @Size(min = 3, max = 30)
    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthDate;

}
