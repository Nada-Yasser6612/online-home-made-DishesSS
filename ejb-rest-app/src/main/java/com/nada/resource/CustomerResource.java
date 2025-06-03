package com.nada.resource;

import com.nada.model.Customer;
import com.nada.service.CustomerService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/customers")
public class CustomerResource {

    private static final Logger logger = Logger.getLogger(CustomerResource.class.getName());

    @EJB
    private CustomerService customerService;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerCustomer(Customer customer) {
        logger.info("Registration attempt for: " + customer.getEmail());

        String result = customerService.registerCustomer(customer);

        if (result.startsWith("✅")) {
            logger.info("Registration successful for: " + customer.getEmail());
            return Response.status(Response.Status.CREATED).entity(result).build();
        } else {
            logger.warning("Registration failed for: " + customer.getEmail() + " — Reason: " + result);
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
    }
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginCustomer(Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            if (email == null || password == null || email.isBlank() || password.isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Collections.singletonMap("error", "Email and password are required"))
                        .build();
            }

            logger.info("Login attempt for: " + email);

            String result = customerService.loginCustomer(email, password);

            if (result.startsWith("SUCCESS:")) {
                logger.info("Login successful for: " + email);
                String[] parts = result.split(":", 3);
                int userId = Integer.parseInt(parts[1]);
                String username = parts[2];

                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("userId", userId);
                response.put("username", username);

                return Response.ok(response).build();

            } else if ("EMAIL_NOT_FOUND".equals(result)) {
                logger.warning("Login failed – Email not found: " + email);
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Collections.singletonMap("error", "Email not found")).build();

            } else if ("WRONG_PASSWORD".equals(result)) {
                logger.warning("Login failed – Wrong password for: " + email);
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Collections.singletonMap("error", "Wrong password")).build();

            } else {
                logger.severe("Login failed – Unexpected error code for: " + email);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(Collections.singletonMap("error", "Unexpected server error")).build();
            }
        } catch (Exception e) {
            logger.severe("Login exception for email: " + credentials.get("email") + " - " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Collections.singletonMap("error", "Login failed due to server error")).build();
        }
    }


}
