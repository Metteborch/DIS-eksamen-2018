package com.cbsexam;

import cache.UserCache;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Log;

@Path("user")
public class UserEndpoints {

  private static UserCache userCache = new UserCache();

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON: FIXED
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);

    // We add encryption to rawString via the method encryptDecryptXOR method with a given parameter
    json = Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down? FIXED
    //If the user is not null, the program will be running as planned
    if (user != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
      //If the user is null, then there will be returned an error 400 (bad request error).
    } else {
      return Response.status(400).entity("Could not find the user - please try again").build();
    }
  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON: FIXED
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);

    //We add encryption to rawString via the method encryptDecryptXOR method with a given parameter
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system. FIXED WITHOUT TOKEN
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String x) {

    User loginUser = new Gson().fromJson(x, User.class);

    User databaseUser = UserController.login(loginUser);

    String json = new Gson().toJson(databaseUser);

    if (databaseUser != null) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Endpoint not implemented yet").build();
    }
  }

  // TODO: Make the system able to deleteUser users - FIXED
  @DELETE
  @Path("delete/")
  public Response deleteUser(String token) {

    //Here we call the deleteUser method in the userController and sends an ID as parameter
    boolean delete = UserController.deleteUser(token);

    //Updates the cache
    userCache.getUsers(true);

    if (delete) {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(new Gson().toJson("The user was succesfully deleted")).build();
    } else {
      return Response.status(400).entity("The user was not found").build();
    }
  }


  // TODO: Make the system able to update users
  @POST
  @Path("/update")
  public Response updateUser(String updateInformation) {

    User userInfo = new Gson().fromJson(updateInformation, User.class);

    User updatedUser = UserController.updateUser(userInfo);
    String json = new Gson().toJson(updatedUser);

    userCache.getUsers(true);

    // Return a response with status 200 and JSON as type
    if (updatedUser != null){
      return Response.status(200).entity("Your new token and information is: " + json).build();
    } else{
      return Response.status(400).entity("Updated user was not found").build();
    }
  }
}
