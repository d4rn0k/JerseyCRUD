package com.daron.app;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class MainApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();

        // register root resource
        classes.add(UsersController.class);

        return classes;
    }

    @GET
    @Path("")
    public Response index() {
        return Response.ok("This works!\nSiaba daba da! ").build();
    }


}