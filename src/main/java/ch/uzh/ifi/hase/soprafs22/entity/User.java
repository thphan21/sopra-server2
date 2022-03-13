package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @DateTimeFormat
  //@Column (nullable = false)
  @GeneratedValue
  private Date creation_date;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(unique = true)
  private String token;

  @Column(nullable = false)
  private boolean logged_in;

  @Column(nullable = false)
  private UserStatus status;

  @DateTimeFormat
  private Date birthday;



  public void setStatus(UserStatus s){this.status = s;}
  public UserStatus getStatus(){return this.status;}

  public void setLogged_in(Boolean b){this.logged_in = b;}
  public Boolean getLogged_in(){return this.status.getBool();}

  public Date getBirthday(){return birthday;}
  public void setBirthday(Date date){this.birthday = date;}

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


}
