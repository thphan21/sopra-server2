package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test // find all users; OK - given
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.ONLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    @Test // register user; OK - given
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.creation_date", is(user.getCreation_date())))
        ;
    }

    @Test // register, username already taken; OK
    public void createUser_UsernameTaken() throws Exception {
        //create user with username anna first
        User user0 = new User();
        user0.setId(1L);
        user0.setName("Test User0");
        user0.setUsername("anna");
        user0.setToken("1");
        user0.setStatus(UserStatus.ONLINE);
        userService.createUser(user0);

        UserPostDTO userPostDTO1 = new UserPostDTO();
        userPostDTO1.setName("Test User");
        userPostDTO1.setUsername("anna");

        // post request for anna STATUS OK
        MockHttpServletRequestBuilder firstPostRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO1));

        given(userService.createUser(Mockito.any())).willReturn(user0);
        mockMvc.perform(firstPostRequest)
                .andExpect(status().isCreated());

        // create user2 with username anna too
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("anna");

        given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Error: reason<string>"));

        // creating 2nd anna should cause STATUS CONFLICT
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isConflict());
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }

    @Test //with ID get userProfile; JSONPATH? (OK)
    public void givenUserID_getUserProfile_thenReturnJsonArray() throws Exception {
        // create user and set ID
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setId(1L);
        User createdUser = userService.createUser(user);

        // this mocks the UserService -> we define above what the userService should
        // return when findByID() is called
        given(userService.findByID(Mockito.anyLong())).willReturn(createdUser);

        // build GET request
        MockHttpServletRequestBuilder getRequest = get("/users/"+user.getId());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
        //.andExpect(jsonPath("$", hasSize(1)))
        //.andExpect(jsonPath("$.name", is(user.getName())))
        //.andExpect(jsonPath("$.username", is(createdUser.getUsername())))
        //.andExpect(jsonPath("$.status", is(createdUser.getStatus().toString())))
        ;
    }


    @Test // getProfile user with INVALID userID NOT found: OK
    public void givenINVALIDUserID_getUserProfile() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setName("123");

        given(userService.findByID(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: reason<string>"));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/"+user.getId());

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());

    }

    @Test // update profile: OK
    public void edit_userProfile_success() throws Exception {
        // create user first
        User user0 = new User();
        user0.setId(1L);
        user0.setName("pw");
        user0.setUsername("anna");
        user0.setToken("1");
        user0.setStatus(UserStatus.ONLINE);
        userService.createUser(user0);

        UserPostDTO userPostDTO1 = new UserPostDTO();
        userPostDTO1.setName("pw");
        userPostDTO1.setUsername("anna");

        MockHttpServletRequestBuilder firstPostRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO1));

        given(userService.createUser(Mockito.any())).willReturn(user0);
        mockMvc.perform(firstPostRequest).andExpect(status().isCreated());
        // create after-update user
        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setUsername("new");
        userGetDTO.setId(user0.getId());
        userGetDTO.setStatus(user0.getStatus());
        userGetDTO.setCreation_date(user0.getCreation_date());
        userGetDTO.setLogged_in(user0.getLogged_in());
        userGetDTO.setBirthday(null);

        // userPutDTO contains new information
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("new");
        userPutDTO.setId(user0.getId());
        userPutDTO.setStatus(user0.getStatus());
        userPutDTO.setCreation_date(user0.getCreation_date());
        userPutDTO.setLogged_in(user0.getLogged_in());
        userPutDTO.setBirthday(null);

        given(userService.updateUser(Mockito.any())).willReturn(userGetDTO);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/"+user0.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent())
        //.andExpect(jsonPath("$.id", is(user.getId().intValue())))
        //.andExpect(jsonPath("$.username", is(userPutDTO.getUsername())))
        //.andExpect(jsonPath("$.status", is(user.getStatus().toString())))
        //.andExpect(jsonPath("$.creation_date", is(user.getCreation_date())))
        ;
    }

    @Test // update profile not found: OK
    public void edit_notfound_NotSuccess() throws Exception {

        User user = new User();
        user.setUsername("testUsername");
        user.setId(1L);

        UserPostDTO userPutDTO = new UserPostDTO();
        userPutDTO.setUsername("newName");

        given(userService.updateUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: reason<string>"));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/"+user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));
        // then
        mockMvc.perform(putRequest).andExpect(status().isNotFound());
    }


}