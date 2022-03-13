package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserPutDTO {
    private Long id;
    private String username;
    private Date creation_date;
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

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getLogged_in() {
        return this.status.getBool();
    }
    public void setLogged_in(boolean logged) {
        this.logged_in = logged;
    }

    public UserStatus getStatus() {return status;}
    public void setStatus(UserStatus b) {this.status = b;}

    public Date getBirthday(){return this.birthday;}
    public void setBirthday(Date date) {
        this.birthday = date;
    }
}
