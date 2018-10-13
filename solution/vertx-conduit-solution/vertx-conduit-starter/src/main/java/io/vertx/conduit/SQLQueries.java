package io.vertx.conduit;

public class SQLQueries {

  public static final String SQL_REGISTER_USER = "insert into USER (\"username\",\"email\",\"bio\",\"password\") values (?, ?, ?, ?);";


}
