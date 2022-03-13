package ch.uzh.ifi.hase.soprafs22.controller;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }
// get all the users in a list
  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
      // fetch all users in the internal representation
      List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

      // convert each user to the API representation
      for (User user : users) {
          userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
      }
        return userGetDTOs;
    }

//retrieve userProfile with userID
  @GetMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUsername(@PathVariable long id) {
      User user = userService.findByID(id);
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

  }
// confirm if user is Current user
  /*@GetMapping("/users/edit")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUsername(@RequestParam String token) {

      User currentUser = userService.findByToken(String.valueOf(token));
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(currentUser);
  }
   */
// LOGIN: does user alrdy exist?, check pw-match
  @PutMapping("/users/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO checkUsername(@RequestParam String username, String pw) {
      User user = userService.loginUser(username, pw);
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }
// register new user
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }


  @PutMapping("/users/logout/")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void doLogout(@RequestParam String token) {
     userService.logoutUser(token);
   }

  @PutMapping("/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void editProfile(@RequestBody UserPutDTO userPutDTO, @PathVariable long id) {
      userService.updateUser(userPutDTO);
  }
}
