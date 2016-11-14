package com.daron.app;


public class User {

    private String login;
    private String hashedPassword;

    public User() {

    }

    @Override
    public String toString() {
        return login + " " + hashedPassword;
    }

    String getLogin() {
        return login;
    }

    void setLogin(String login) {
        this.login = login;
    }

    String getHashedPassword() {
        return hashedPassword;
    }

    void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    User(String login, String hashedPassword) {
        this.login = login;
        this.hashedPassword = hashedPassword;
    }
}
