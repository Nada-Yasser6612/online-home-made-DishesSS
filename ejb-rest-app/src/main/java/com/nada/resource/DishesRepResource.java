
package com.nada.resource;

import com.nada.model.User;
import com.nada.service.DishesRepService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
@Path("/dishes-reps")

public class DishesRepResource {

    @EJB
    private DishesRepService dishesRepService;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginDishesRep(User loginData) {
        String result = dishesRepService.loginDishesRep(loginData.getEmail(), loginData.getPassword());

        if (result.startsWith("{\"status\":\"success\"")) {
            return Response.ok(result, MediaType.APPLICATION_JSON).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(result)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
