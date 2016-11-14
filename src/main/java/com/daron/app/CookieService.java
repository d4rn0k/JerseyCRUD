package com.daron.app;

import java.net.HttpCookie;
import java.util.*;

class CookieUserWrapper {

    private HttpCookie cookie;
    private User user;

    CookieUserWrapper(HttpCookie cookie, User user) {
        this.cookie = cookie;
        this.user = user;
    }

    HttpCookie getCookie() {
        return cookie;
    }

    void setCookie(HttpCookie cookie) {
        this.cookie = cookie;
    }

    User getUser() {
        return user;
    }

    void setUser(User user) {
        this.user = user;
    }
}

class CookieService {

    private static HashMap<String, CookieUserWrapper> loggedUsersHashMap = new HashMap<>();

    String addNewSession(User user) {
        final String cookieName = "session";
        final String sessionID = UUID.randomUUID().toString();
        final String path = "/";
        final String domain = "localhost";
        final String comment = "Cookie session!";
        final int maxAgeInSeconds = 3600;
        final boolean isSecure = false;

        HttpCookie newCookie = new HttpCookie(cookieName, sessionID);

        newCookie.setMaxAge(maxAgeInSeconds);
        newCookie.setDomain(domain);
        newCookie.setComment(comment);
        newCookie.setSecure(isSecure);
        newCookie.setPath(path);

        CookieUserWrapper cuw = new CookieUserWrapper(newCookie, user);

        // Kasujemy starą sesję jeśli istnieje!
        // Bez tego może być kilka sesji dla jednego usera
        deleteOldSession(user);

        loggedUsersHashMap.put(sessionID, cuw);

        return sessionID;
    }

    private void deleteOldSession(User searchedUser){
        for(CookieUserWrapper cuw : loggedUsersHashMap.values()){
            if (cuw.getUser().getLogin().equals(searchedUser.getLogin())){
                String sessionID = cuw.getCookie().getValue();

                loggedUsersHashMap.remove(sessionID);
            }
        }
    }


    void invalidateSession(String sessionID) {
        loggedUsersHashMap.remove(sessionID);
    }

    List<User> getAllLoggedUsers(String sessionID) {
        List<User> loggedUsersList = new ArrayList<>();

        if (checkIsSessionValid(sessionID)) {
            loggedUsersHashMap.entrySet().forEach(cuw -> {
                if (checkIsSessionValid(cuw.getKey())) {
                    loggedUsersList.add(cuw.getValue().getUser());
                }
            });
        }
        return loggedUsersList;
    }

    private boolean checkIsSessionValid(String sessionID) {

        CookieUserWrapper cuw = getCookieUserWrapperForSession(sessionID);

        return cuw != null && !cuw.getCookie().hasExpired();
    }

    private CookieUserWrapper getCookieUserWrapperForSession(String sessionID) {
        return loggedUsersHashMap.get(sessionID);
    }

    private HttpCookie getSessionCookieFromAllCookies(Collection<HttpCookie> cookies) {
        for (HttpCookie cookie : cookies) {
            if (cookie.getName().equals("session")) {
                return cookie;
            }
        }
        return null;
    }
}
