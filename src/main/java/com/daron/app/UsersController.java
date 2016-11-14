package com.daron.app;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


@Path("/users")
public class UsersController {

    private static final String COOKIE_NAME = "mySessionCookie";

    private static UsersService usersService = new UsersService();
    private static CookieService cookieService = new CookieService();

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response createUser(String inputString) {

        Scanner scanner = new Scanner(inputString);
        String login = scanner.nextLine();
        String textPlainPassword = scanner.nextLine();

        if (login.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("newUser ma pusty login!").build();
        }

        if (textPlainPassword.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("newUser nie ma ustawionego hasła!").build();
        }

        usersService.addUser(login, textPlainPassword);

        return Response.ok("Dodano usera!").build();
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
    public Response retrieveAllUsers(
            @QueryParam("sortBy") final String sortBy,
            @QueryParam("sortDir") final String sortDir,
            @QueryParam("page") int page,
            @QueryParam("count") int count) {

        List<User> usersToReturn;

        if (count == 0) {
            usersToReturn = usersService.getAllUsers();
        } else {
            usersToReturn = usersService.getAllUsers()
                    .stream()
                    .sorted(new Comparator<User>() {
                        @Override
                        public int compare(User o1, User o2) {
                            if (sortDir == null) {
                                return 0;
                            }
                            if (sortDir.equals("ASC")) {
                                return o1.getLogin().compareTo(o2.getLogin());
                            } else if (sortDir.equals("DESC")) {
                                return o2.getLogin().compareTo(o1.getLogin());
                            }
                            return 0;
                        }
                    })
                    .skip(page * count)
                    .limit(count)
                    .collect(Collectors.<User>toList());
        }

        return Response.ok(getUsersString(usersToReturn)).build();

    }


    @PUT
    @Path("/{login}")
    public Response updateUser(@PathParam("login") String login, String inputString) {

        Scanner scanner = new Scanner(inputString);
        String newLogin = scanner.nextLine();
        String textPlainPassword = scanner.nextLine();

        if (newLogin.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("updatedUser ma pusty login!").build();
        }

        if (textPlainPassword.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("updatedUser nie ma ustawionego hasła!").build();
        }

        if (!usersService.isUserExist(login)) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Nie ma takiego usera!\nNie ma co aktualizować!").build();
        }

        usersService.updateUser(login, newLogin, textPlainPassword);

        return Response.ok(String.format("Zakturalizowano usera: [%s] => [%s]", login, newLogin)).build();

    }

    @DELETE
    @Path("/{login}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteUser(@PathParam("login") String login) {

        if (login.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("updatedUser ma pusty login!").build();
        } else if (!usersService.isUserExist(login)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nie ma takiego usera!").build();
        }

        usersService.deleteUser(login);

        return Response.ok("Usunięto usera: [" + login + "]").build();
    }

    @GET
    @Path("/{login}")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
    public Response getUserById(@PathParam("login") String login) {

        if (usersService.isUserExist(login)) {
            return Response.ok(new GenericEntity<MyResponse>(new MyResponse("User istnieje!", false)) {
            }).build();
        } else {
            return Response.ok(new GenericEntity<MyResponse>(new MyResponse("Nie ma takiego usera w bazie!", true)) {
            }).build();
        }

    }


    //Cookies!!!
    //Metody z cookies

    @POST
    @Path("/login")
    public Response cookieLogin(String inputString) {

        Scanner scanner = new Scanner(inputString);
        String login = scanner.nextLine();
        String textPlainPassword = scanner.nextLine();


        if (usersService.isValidPassword(login, textPlainPassword)) {

            String sessionID = cookieService.addNewSession(usersService.getUser(login));

            NewCookie newCookie = new NewCookie(COOKIE_NAME, sessionID);

            return Response.ok("Ustawiono nowe cookie z sessionId=" + sessionID).cookie(newCookie).build();
        } else {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("Błąd! Błędne hasło lub usera brak w bazie!")
                    .build();
        }

    }

    @GET
    @Path("/logout")
    public Response cookieLogout(@CookieParam(COOKIE_NAME) String mySessionCookie) {

        NewCookie newCookie = new NewCookie(COOKIE_NAME, "INVALIDE_SESSION");

        cookieService.invalidateSession(mySessionCookie);

        return Response
                .ok("Wylogowano!")
                .cookie(newCookie).build();
    }

    @GET
    @Path("/login")
    public Response showLoggedUsers(@CookieParam(COOKIE_NAME) String mySessionCookieValue) {

        System.err.println("SessionIdVal = " + mySessionCookieValue);

        List<User> loggedUsers = cookieService.getAllLoggedUsers(mySessionCookieValue);

        return Response.ok()
                .entity("Lista zalogowanych userow!\n" + getUsersString(loggedUsers))
                .build();
    }


    // Potrzebne do wylistowania userów i wyplucie jako string
    // Bez tego wywala się, choć w teorii nie powinno (powinna być domyślna implementacja dla Jersey'a
    // (albo zła konfiguracja bo nie potrafi on zwrócić nawet listy stringów: List<String> WTF!? ))
    private String getUsersString(List<User> usersList) {
        StringBuilder sb = new StringBuilder();

        sb.append("Login:\t hash:\n");

        for (User singleUser : usersList) {
            sb.append(singleUser.toString()).append("\n");
        }

        return sb.toString();
    }
}
