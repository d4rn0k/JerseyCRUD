package com.daron.app;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class UsersService {

    private static MessageDigest messageDigest;

    private static HashMap<String, User> userHashMap = new HashMap<>();

    // Blok statyczny do inicjalizacji
    {
        userHashMap.put("admin", new User("admin", getHashedPass("admin")));
        userHashMap.put("konrad", new User("konrad", getHashedPass("konrad")));
        userHashMap.put("kamil", new User("kamil", getHashedPass("kamil")));
        userHashMap.put("waclaw", new User("waclaw", getHashedPass("waclaw")));
        userHashMap.put("ryszard", new User("ryszard", getHashedPass("ryszard")));
        userHashMap.put("janSebastian", new User("janSebastian", getHashedPass("janSebastian")));
        userHashMap.put("karina", new User("karina", getHashedPass("karina")));
        userHashMap.put("karina1", new User("karina1", getHashedPass("karina1")));
        userHashMap.put("karolina", new User("karolina", getHashedPass("karolina")));
        userHashMap.put("jola", new User("jola", getHashedPass("jola")));
        userHashMap.put("jola2", new User("jola2", getHashedPass("jola")));
    }


    /*
        Create
     */
    void addUser(String login, String textPlainPassword) {
        userHashMap.put(login, new User(login, getHashedPass(textPlainPassword)));
    }

    User getUser(String login) {
        return userHashMap.get(login);
    }

    List<User> getAllUsers() {
        return new ArrayList<>(userHashMap.values());
    }

    /*
        Update
     */
    void updateUser(String oldLogin, String newLogin, String newTextPlainPassword) {
        userHashMap.remove(oldLogin);
        addUser(newLogin, newTextPlainPassword);
    }

    void deleteUser(String login) {
        userHashMap.remove(login);
    }


    boolean isUserExist(String login) {
        return userHashMap.get(login) != null;
    }

    boolean isValidPassword(String login, String textPlainPassword) {
        return isUserExist(login) &&
                getHashedPass(textPlainPassword).equals(userHashMap.get(login).getHashedPassword());
    }


    private String getHashedPass(String input) {

        String hashingAlgorithm = "SHA-256";
        try {
            messageDigest = MessageDigest.getInstance(hashingAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(StandardCharsets.UTF_8.encode(input));
        return String.format("%064x", new BigInteger(1, messageDigest.digest()));
    }
}
