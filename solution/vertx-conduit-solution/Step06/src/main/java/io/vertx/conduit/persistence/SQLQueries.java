package io.vertx.conduit.persistence;

public class SQLQueries {

  public static final String SQL_REGISTER_USER = "insert into USER (\"username\",\"email\",\"bio\",\"password\", \"password_salt\") values (?, ?, ?, ?, ?);";
  public static final String SQL_SELECT_USER_BY_EMAIL = "select * from USER where \"email\" = ?";
  public static final String SQL_LOGIN_QUERY = "select \"password\", \"password_salt\" from USER where \"email\" = ?";
}
