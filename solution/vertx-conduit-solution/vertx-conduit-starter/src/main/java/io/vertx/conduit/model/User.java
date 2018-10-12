package io.vertx.conduit.model;

import io.vertx.core.json.JsonObject;

public class User {

  String email;

  String token;

  String username;

  String bio;

  String image;

  public JsonObject toJsonObject() {

  }

  /**
   * Constructor that takes a JsonObject representing the User
   *
   * @param jsonObject
   */
  public User(JsonObject jsonObject) {
    this.email = jsonObject.getString("email");
    this.token = jsonObject.getString("token");
    this.username = jsonObject.getString("username");
    this.bio = jsonObject.getString("bio");
    this.image = jsonObject.getString("image");
  }


  public User() {
  }

  public User(String email, String token, String username, String bio, String image) {
    this.email = email;
    this.token = token;
    this.username = username;
    this.bio = bio;
    this.image = image;
  }

  @Override
  public String toString() {
    return "User{" +
      "email='" + email + '\'' +
      ", token='" + token + '\'' +
      ", username='" + username + '\'' +
      ", bio='" + bio + '\'' +
      ", image='" + image + '\'' +
      '}';
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }
}
