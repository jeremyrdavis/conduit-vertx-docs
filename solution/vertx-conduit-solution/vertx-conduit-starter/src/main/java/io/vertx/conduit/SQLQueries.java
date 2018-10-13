package io.vertx.conduit;

public class SQLQueries {

  public static final String SQL_REGISTER_USER = "insert into USER (\"username\",\"email\",\"bio\",\"password\") values (?, ?, ?, ?);";
  public static final String SQL_SELECT_USER_BY_EMAIL = "select * from USER where \"email\" = ?";

}
