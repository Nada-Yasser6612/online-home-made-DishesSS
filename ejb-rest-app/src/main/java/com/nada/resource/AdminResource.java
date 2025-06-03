package com.nada.resource;
import com.nada.model.Customer;
import com.nada.model.DishesRepresentative;
import com.nada.model.User;

import com.nada.service.AdminService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/admin")
public class AdminResource {

    @EJB
    private AdminService adminService;
    @POST
    @Path("/create-reps")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRepresentatives(
            @HeaderParam("X-User-Id") int adminUserId,
            List<String> companyNames) {

        if (companyNames == null || companyNames.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Company name list cannot be empty").build();
        }

        if (!adminService.isAdmin(adminUserId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(" Access denied: Only admins can perform this action.")
                    .build();
        }

        Map<String, String> result = adminService.createRepAccounts(companyNames);
        return Response.ok(result).build();
    }
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginAdmin(User loginData) {
        String result = adminService.loginAdmin(loginData.getEmail(), loginData.getPassword());

        // Check if it's a structured JSON success response
        if (result.startsWith("{\"status\":\"success\"")) {
            return Response.ok(result, MediaType.APPLICATION_JSON).build();
        }

        // Handle known errors
        switch (result) {
            case "{\"status\":\"EMAIL_NOT_FOUND\"}":
                return Response.status(Response.Status.NOT_FOUND).entity(result).type(MediaType.APPLICATION_JSON).build();
            case "{\"status\":\"WRONG_PASSWORD\"}":
                return Response.status(Response.Status.UNAUTHORIZED).entity(result).type(MediaType.APPLICATION_JSON).build();
            case "{\"status\":\"NOT_ADMIN\"}":
                return Response.status(Response.Status.FORBIDDEN).entity(result).type(MediaType.APPLICATION_JSON).build();
            case "{\"status\":\"DB_ERROR\"}":
            default:
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).type(MediaType.APPLICATION_JSON).build();
        }
    }


    @GET
    @Path("/customers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers(@HeaderParam("X-User-Id") int adminId) {
        if (!adminService.isAdmin(adminId))
        {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Only admins can view customer list.").build();
        }
        List<Customer> customers = adminService.getAllCustomers();
        return Response.ok(customers).build();
    }


    @GET
    @Path("/reps")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDishesRepresentatives(@HeaderParam("X-User-Id") int adminUserId) {
        if (!adminService.isAdmin(adminUserId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: Only admins can view representatives.")
                    .build();
        }

        List<DishesRepresentative> reps = adminService.getAllDishesReps();
        return Response.ok(reps).build();
    }



}
