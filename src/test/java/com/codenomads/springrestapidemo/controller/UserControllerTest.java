package com.codenomads.springrestapidemo.controller;

import com.codenomads.springrestapidemo.SpringRestApiDemoApplication;
import com.codenomads.springrestapidemo.dto.UserDto;
import com.codenomads.springrestapidemo.model.User;
import com.codenomads.springrestapidemo.repository.UserRepository;
import com.codenomads.springrestapidemo.utils.DateParser;
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
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringRestApiDemoApplication.class)
@AutoConfigureMockMvc
@EnableWebMvc
@Transactional
class UserControllerTest {

    private static final String USERS_PATH = "/users";
    public static final String USER_NOT_FOUND_TEXT_FORMAT = "User with id '%s' could not be found.";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DateParser dateParser;

    @Autowired
    private JsonUtils jsonUtils;

    @Test
    void shouldRetrieveUserForExistingId() throws Exception {
        String name = "Hans";
        Date birthDate = dateParser.parse("1993-11-02");
        User user = User.builder()
                .name(name)
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);
        assertTrue(userRepository.existsById(savedUser.getId()));

        ResultActions resultActions = mockMvc.perform(get(USERS_PATH + "/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UserDto response = jsonUtils.deserializeResult(resultActions, UserDto.class);

        assertEquals(savedUser.getId(), response.getId());
        assertEquals(name, response.getName());
        assertEquals(birthDate, response.getBirthDate());
    }

    @Test
    void shouldNotRetrieveAnyUserForNonExistentId() throws Exception {
        String name = "Hans";
        Date birthDate = dateParser.parse("1993-11-02");
        User user = User.builder()
                .name(name)
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);
        assertTrue(userRepository.existsById(savedUser.getId()));
        UUID nonExistentId = UUID.randomUUID();

        ResultActions resultActions = mockMvc.perform(get(USERS_PATH + "/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertEquals(USER_NOT_FOUND_TEXT_FORMAT.formatted(nonExistentId),
                resultActions.andReturn().getResponse().getErrorMessage());
    }

    @Test
    void shouldCreateNewUserAndSaveToDatabase() throws Exception {
        String name = "Hans";
        Date birthDate = dateParser.parse("1993-11-02");
        UserDto request = UserDto.builder()
                .name(name)
                .birthDate(birthDate)
                .build();

        assertTrue(userRepository.findAll().isEmpty());

        ResultActions resultActions = mockMvc.perform(post(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUtils.asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UserDto response = jsonUtils.deserializeResult(resultActions, UserDto.class);

        assertNotNull(response.getId());
        assertEquals(name, response.getName());
        assertEquals(birthDate, response.getBirthDate());

        assertEquals(1, userRepository.findAll().size());
        User savedUser = userRepository.findById(response.getId())
                .orElseThrow();
        assertEquals(name, savedUser.getName());
        assertEquals(birthDate, savedUser.getBirthDate());
    }

    @Test
    void shouldModifyExistingUserWithPutCall() throws Exception {
        String name = "Hans";
        String modifiedName = "Maria";
        Date birthDate = dateParser.parse("1993-11-02");
        User user = User.builder()
                .name(name)
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);
        assertEquals(name, savedUser.getName());
        assertEquals(birthDate, savedUser.getBirthDate());

        UserDto request = UserDto.builder()
                .id(savedUser.getId())
                .name(modifiedName)
                .birthDate(birthDate)
                .build();

        ResultActions resultActions = mockMvc.perform(put(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUtils.asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UserDto response = jsonUtils.deserializeResult(resultActions, UserDto.class);

        assertNotNull(response.getId());
        assertEquals(modifiedName, response.getName());
        assertEquals(birthDate, response.getBirthDate());

        User modifiedUser = userRepository.getReferenceById(response.getId());
        assertEquals(modifiedName, modifiedUser.getName());
        assertEquals(birthDate, modifiedUser.getBirthDate());
    }

    @Test
    void shouldCreateNewUserWithPutCallIfIdNotExistent() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "Hans";
        Date birthDate = dateParser.parse("1993-11-02");

        assertFalse(userRepository.existsById(id));

        UserDto request = UserDto.builder()
                .id(id)
                .name(name)
                .birthDate(birthDate)
                .build();

        ResultActions resultActions = mockMvc.perform(put(USERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUtils.asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        UserDto response = jsonUtils.deserializeResult(resultActions, UserDto.class);

        assertNotNull(response.getId());
        assertEquals(name, response.getName());
        assertEquals(birthDate, response.getBirthDate());

        User savedUser = userRepository.getReferenceById(response.getId());
        assertEquals(name, savedUser.getName());
        assertEquals(birthDate, savedUser.getBirthDate());
    }

    @Test
    void shouldDeleteUserForExistingId() throws Exception {
        String name = "Hans";
        Date birthDate = dateParser.parse("1993-11-02");
        User user = User.builder()
                .name(name)
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);
        assertTrue(userRepository.existsById(savedUser.getId()));

        mockMvc.perform(delete(USERS_PATH + "/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    void shouldNotDeleteUserForNonExistentId() throws Exception {
        String name = "Hans";
        Date birthDate = dateParser.parse("1993-11-02");
        User user = User.builder()
                .name(name)
                .birthDate(birthDate)
                .build();

        User savedUser = userRepository.save(user);
        assertTrue(userRepository.existsById(savedUser.getId()));
        UUID nonExistentId = UUID.randomUUID();

        ResultActions resultActions = mockMvc.perform(delete(USERS_PATH + "/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertEquals(USER_NOT_FOUND_TEXT_FORMAT.formatted(nonExistentId),
                resultActions.andReturn().getResponse().getErrorMessage());
    }
}