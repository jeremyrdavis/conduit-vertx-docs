package io.vertx.conduit.persistence;

public enum PersistenceErrorCodes {

  DB_CONNECTION_ERROR("Database Connection Error: "),
  DB_INSERT_FAILURE("Insert Failed: "),
  NOT_FOUND("Not found");


  public final String message;

  private PersistenceErrorCodes(String msg){
    this.message = msg;
  }

}
