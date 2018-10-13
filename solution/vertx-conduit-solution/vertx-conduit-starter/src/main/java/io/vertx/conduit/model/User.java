package io.vertx.conduit.model;

import io.vertx.core.json.JsonObject;

public class User {

  String email;

  String token;

  String username;

  String bio;

  String image;

  String password;

  String password_salt;

  public JsonObject toJsonObject() {

    return new JsonObject()
      .put("user", new JsonObject()
        .put("email", this.email)
        .put("token", this.token)
        .put("username", this.username)
        .put("bio", this.bio)
        .put("image", this.image));
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
    this.password = jsonObject.getString("password");
  }


  public User() {
  }

  public User(String email, String token, String username, String bio, String image, String password) {
    this.email = email;
    this.token = token;
    this.username = username;
    this.bio = bio;
    this.image = image;
    this.password = password;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword_salt() {
    return password_salt;
  }

  public void setPassword_salt(String password_salt) {
    this.password_salt = password_salt;
  }
}
