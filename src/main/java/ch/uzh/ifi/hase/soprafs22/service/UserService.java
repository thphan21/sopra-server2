package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    //.setLogged_in(true);
    newUser.setStatus(UserStatus.ONLINE);

    /*
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
    LocalDateTime now = LocalDateTime.now();
    String today = dtf.format(now);
     */

    Date currentTime = new Date();
    newUser.setCreation_date(currentTime);

    checkIfUserExists(newUser);

    // saves the given entity but data is only persisted in the database once
    // flush() is called

    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */

  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    User userByName = userRepository.findByName(userToBeCreated.getName());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null && userByName != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          String.format(baseErrorMessage, "username and the name", "are"));
    } else if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
    }
    /* COMMENT OUT BECAUSE I'LL TREAT NAME AS THE PASSWORD (RENAMED LATER MAYBE)
    else if (userByName != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "name", "is"));
    }

     */
  }

  public User loginUser(String username, String pw){
      User userByUsername = userRepository.findByUsername(username);

      String baseErrorMessage = "USERNAME DOESN'T EXIST :)";
      String wrong = "WRONG PASSWORD PROVIDED!";
      if (userByUsername == null){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,baseErrorMessage);
      }
      userByUsername.setToken(UUID.randomUUID().toString());
      //userByUsername.setLogged_in(true);


      String shouldPassword = userByUsername.getName();

      if (shouldPassword.equals(pw)){
          userByUsername.setStatus(UserStatus.ONLINE);
          userRepository.flush();
          return userByUsername;
      }

      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, wrong);

  }

  public User findByID(long userID){
      User userByUserID = userRepository.findById(userID);

      String baseErrorMessage = "USER DOESN'T EXIST :)";

      if (userByUserID == null){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND,baseErrorMessage);
      }
      return userByUserID;
  }

  public User findByToken(String token){
      String baseErrorMessage = "TOKEN DOESN'T EXIST :)";
      return userRepository.findByToken(token);

  }

  public void logoutUser(String token){
      User user = findByToken(token);
      user.setStatus(UserStatus.OFFLINE);
      userRepository.flush();
  }

  public UserGetDTO updateUser(UserPutDTO userPutDTO){
      long idd = userPutDTO.getId();
      User user = userRepository.findById(idd);
      if (user == null){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND);
      }
      //User newUser = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
      setBirthday(user, DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO));

      User sameName = userRepository.findByUsername(userPutDTO.getUsername());
      if (sameName != null){
          throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
      }
      if (userPutDTO.getUsername() != ""){
          user.setUsername(userPutDTO.getUsername());
          userRepository.flush();
      }
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

  }


  public void setBirthday(User og, User update){
       /*
      String date = userPutDTO.getBirthday();
      if (date != null){
          Date bDay = new SimpleDateFormat("dd.MM.yyyy").parse(date);
          user.setBirthday(bDay);
          userRepository.flush();
      }
         */
      if (og.getBirthday() != update.getBirthday() && update.getBirthday() != null){
          og.setBirthday(update.getBirthday());
          userRepository.flush();
      }
  }

}

/*
@Test // register, username already taken; OK
    public void createUser_INVALIDInput_userCreated() throws Exception {
        User user0 = new User();
        user0.setId(1L);
        user0.setName("Test User0");
        user0.setUsername("a");
        user0.setToken("1");
        user0.setStatus(UserStatus.ONLINE);
        userService.createUser(user0);

        UserPostDTO userPostDTO1 = new UserPostDTO();
        userPostDTO1.setName("Test User");
        userPostDTO1.setUsername("a");

        MockHttpServletRequestBuilder firstPostRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO1));

        given(userService.createUser(Mockito.any())).willReturn(user0);
        mockMvc.perform(firstPostRequest)
                .andExpect(status().isCreated());

        User user = new User();
        user.setId(2L);
        user.setName("Test User");
        user.setUsername("a");
        user.setToken("2");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("a");

        given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then


        mockMvc.perform(postRequest)
                .andExpect(status().isConflict())
                //.andExpect(jsonPath("$.id", is(user.getId().intValue())))
                //.andExpect(jsonPath("$.name", is(user.getName())))
                //.andExpect(jsonPath("$.username", is(user.getUsername())))
                //.andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                //.andExpect(jsonPath("$.creation_date", is(user.getCreation_date())))
        ;
    }


 */
