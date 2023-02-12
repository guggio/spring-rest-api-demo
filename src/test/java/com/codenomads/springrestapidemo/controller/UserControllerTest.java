package com.codenomads.springrestapidemo.controller;

import com.codenomads.springrestapidemo.SpringRestApiDemoApplication;
import com.codenomads.springrestapidemo.dto.UserDto;
import com.codenomads.springrestapidemo.model.User;
import com.codenomads.springrestapidemo.repository.UserRepository;
import com.codenomads.springrestapidemo.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringRestApiDemoApplication.class)
@AutoConfigureMockMvc
@EnableWebMvc
@Transactional
class UserControllerTest {

    private static final String USERS_PATH = "/users";
    public static final String USER_NOT_FOUND_TEXT_FORMAT = "User with email '%s' could not be found.";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JsonUtils jsonUtils;

    @Test
    void shouldRetrieveUserForExistingEmail() throws Exception {
        String name = "Hans";
        String email = "hans@gmail.com";
        LocalDate birthDate = LocalDate.parse("1993-11-02");
        User user = User.builder()
                .name(name)
                .email(email)
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);
        assertTrue(userRepository.existsById(savedUser.getId()));

        ResultActions resultActions = mockMvc.perform(get(USERS_PATH + "/" + email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UserDto response = jsonUtils.deserializeResult(resultActions, UserDto.class);

        assertEquals(email, response.getEmail());
        assertEquals(name, response.getName());
        assertEquals(birthDate, response.getBirthDate());
    }

    @Test
    void shouldNotRetrieveAnyUserForNonExistentEmail() throws Exception {
        String name = "Hans";
        String email = "hans@gmail.com";
        String nonExistentEmail = "hans69@gmail.com";
        LocalDate birthDate = LocalDate.parse("1993-11-02");
        User user = User.builder()
                .name(name)
                .email(email)
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);
        assertTrue(userRepository.existsById(savedUser.getId()));

        ResultActions resultActions = mockMvc.perform(get(USERS_PATH + "/" + nonExistentEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertEquals(USER_NOT_FOUND_TEXT_FORMAT.formatted(nonExistentEmail),
                resultActions.andReturn().getResponse().getErrorMessage());
    }

    @Test
    void shouldCreateNewUserAndSaveToDatabase() throws Exception {
        String name = "Hans";
        String email = "hans@gmail.com";
        LocalDate birthDate = LocalDate.parse("1993-11-02");
        UserDto request = UserDto.builder()
                .name(name)
                .email(email)
                .birthDate(birthDate)
                .build();

        assertTrue(userRepository.findAll().isEmpty());

        ResultActions resultActions = mockMvc.perform(post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUtils.asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        UserDto response = jsonUtils.deserializeResult(resultActions, UserDto.class);

        assertEquals(email, response.getEmail());
        assertEquals(name, response.getName());
        assertEquals(birthDate, response.getBirthDate());

        assertEquals(1, userRepository.findAll().size());
        User savedUser = userRepository.findByEmail(email)
                .orElseThrow();
        assertEquals(email, savedUser.getEmail());
        assertEquals(name, savedUser.getName());
        assertEquals(birthDate, savedUser.getBirthDate());
    }

    @Test
    void shouldModifyExistingUserWithPutCall() throws Exception {
        String name = "Hans";
        String email = "hans@gmail.com";
        String modifiedName = "Maria";
        LocalDate birthDate = LocalDate.parse("1993-11-02");
        LocalDate modifiedBirthDate = LocalDate.parse("1999-11-02");
        User user = User.builder()
                .name(name)
                .email(email)
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);
        assertEquals(name, savedUser.getName());
        assertEquals(birthDate, savedUser.getBirthDate());

        UserDto request = UserDto.builder()
                .name(modifiedName)
                .email(email)
                .birthDate(modifiedBirthDate)
                .build();

        ResultActions resultActions = mockMvc.perform(put(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUtils.asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UserDto response = jsonUtils.deserializeResult(resultActions, UserDto.class);

        assertEquals(email, response.getEmail());
        assertEquals(modifiedName, response.getName());
        assertEquals(modifiedBirthDate, response.getBirthDate());

        User modifiedUser = userRepository.findByEmail(response.getEmail()).orElseThrow();
        assertEquals(email, modifiedUser.getEmail());
        assertEquals(modifiedName, modifiedUser.getName());
        assertEquals(modifiedBirthDate, modifiedUser.getBirthDate());
    }

    @Test
    void shouldCreateNewUserWithPutCallIfEmailNotExistent() throws Exception {
        String name = "Hans";
        String email = "hans@gmail.com";
        LocalDate birthDate = LocalDate.parse("1993-11-02");

        assertTrue(userRepository.findByEmail(email).isEmpty());

        UserDto request = UserDto.builder()
                .name(name)
                .email(email)
                .birthDate(birthDate)
                .build();


        ResultActions resultActions = mockMvc.perform(put(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUtils.asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UserDto response = jsonUtils.deserializeResult(resultActions, UserDto.class);

        assertEquals(email, response.getEmail());
        assertEquals(name, response.getName());
        assertEquals(birthDate, response.getBirthDate());

        User savedUser = userRepository.findByEmail(response.getEmail()).orElseThrow();
        assertEquals(name, savedUser.getName());
        assertEquals(birthDate, savedUser.getBirthDate());
    }

    @Test
    void shouldDeleteUserForExistingEmail() throws Exception {
        String name = "Hans";
        String email = "hans@gmail.com";
        LocalDate birthDate = LocalDate.parse("1993-11-02");
        User user = User.builder()
                .name(name)
                .email(email)
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);
        assertTrue(userRepository.existsById(savedUser.getId()));

        mockMvc.perform(delete(USERS_PATH + "/" + email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void shouldNotDeleteUserForNonExistentEmail() throws Exception {
        String name = "Hans";
        String email = "hans@gmail.com";
        LocalDate birthDate = LocalDate.parse("1993-11-02");
        User user = User.builder()
                .name(name)
                .email(email)
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);
        assertTrue(userRepository.existsById(savedUser.getId()));

        String nonExistentEmail = "hans69@gmail.com";

        ResultActions resultActions = mockMvc.perform(delete(USERS_PATH + "/" + nonExistentEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertEquals(USER_NOT_FOUND_TEXT_FORMAT.formatted(nonExistentEmail),
                resultActions.andReturn().getResponse().getErrorMessage());
    }
}