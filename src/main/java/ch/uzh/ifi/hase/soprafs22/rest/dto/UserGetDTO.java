package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;

import java.util.Date;

public class UserGetDTO {

  private Long id;
  private String name;
  private String username;
  private Date creation_date;
  private String token;
  private boolean logged_in;
  private UserStatus status;
  private Date birthday;

  public Date getCreation_date(){return creation_date;}
  public void setCreation_date(Date date){this.creation_date = date;}

  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }


  public String getToken() {
        return token;
    }
  public void setToken(String token) {
        this.token = token;
    }

  public boolean getLogged_in() {
        return this.status.getBool();
    }
  public void setLogged_in(boolean logged) {
        this.logged_in = logged;
    }

  public UserStatus getStatus() {return status;}
  public void setStatus(UserStatus b) {this.status = b;}

  public Date getBirthday() {
        return this.birthday;
    }
  public void setBirthday(Date date) {
        this.birthday = date;
    }
}
