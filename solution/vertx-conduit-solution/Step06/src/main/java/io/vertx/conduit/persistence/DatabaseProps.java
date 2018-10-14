package io.vertx.conduit.persistence;

public class DatabaseProps {

  protected static final String DB_DRIVER_KEY = "driver_class";
  protected static final String DB_POOL_SIZE_KEY = "max_pool_size";
  protected static final String DB_URL_KEY = "url";
  protected static final String DB_USER_KEY = "user";

  protected static final String DB_DRIVER_DEFAULT = "org.hsqldb.jdbcDriver";
  protected static final Integer DB_POOL_SIZE_DEFAULT = 30;
  protected static final String DB_URL_DEFAULT = "jdbc:hsqldb:file:db/conduit;shutdown=true";
  protected static final String DB_USER_DEFAULT = "sa";

}
