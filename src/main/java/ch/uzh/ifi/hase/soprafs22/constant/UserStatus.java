package ch.uzh.ifi.hase.soprafs22.constant;

public enum UserStatus {
  ONLINE(true),
  OFFLINE(false);

  private boolean log;
  private UserStatus(boolean b){log = b;}

  public boolean getBool() {
        return log;
    }
}
