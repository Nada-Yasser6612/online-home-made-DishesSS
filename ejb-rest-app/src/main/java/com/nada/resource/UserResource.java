package com.nada.resource;

import com.nada.dto.UserDetailsDTO;
import com.nada.service.UserService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserResource {

    @EJB
    private UserService userService;

    @GET
    @Path("/details/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserDetails(@PathParam("id") int id) {
        UserDetailsDTO user = userService.getUserDetailsById(id);

        if (user != null) {
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"status\":\"USER_NOT_FOUND\"}")
                    .build();
        }
    }
}
