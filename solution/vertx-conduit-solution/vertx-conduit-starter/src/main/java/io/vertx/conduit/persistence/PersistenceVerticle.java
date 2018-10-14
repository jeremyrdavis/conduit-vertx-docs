package io.vertx.conduit.persistence;

import io.vertx.conduit.model.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;

import static io.vertx.conduit.ApplicationProps.*;
import static io.vertx.conduit.persistence.SQLQueries.SQL_LOGIN_QUERY;
import static io.vertx.conduit.persistence.SQLQueries.SQL_REGISTER_USER;
import static io.vertx.conduit.persistence.DatabaseProps.*;
import static io.vertx.conduit.persistence.SQLQueries.SQL_SELECT_USER_BY_EMAIL;

public class PersistenceVerticle extends AbstractVerticle {

  private JDBCClient jdbcClient;

  private JDBCAuth authProvider;


  @Override
  public void start(Future<Void> startFuture) throws Exception {

    System.out.println(config().getString(DB_URL_KEY));

    jdbcClient = JDBCClient.createShared(vertx, new JsonObject()
      .put(DB_URL_KEY, config().getString(DB_URL_KEY, DB_URL_DEFAULT))
      .put(DB_DRIVER_KEY, config().getString(DB_DRIVER_KEY, DB_DRIVER_DEFAULT))
      .put(DB_USER_KEY, config().getString(DB_USER_KEY, DB_USER_DEFAULT))
      .put(DB_POOL_SIZE_KEY, config().getInteger(DB_POOL_SIZE_KEY, DB_POOL_SIZE_DEFAULT)));

    authProvider = JDBCAuth.create(vertx, jdbcClient);
    authProvider.setAuthenticationQuery(SQL_LOGIN_QUERY);


    EventBus eventBus = vertx.eventBus();
    MessageConsumer<JsonObject> consumer = eventBus.consumer(PERSISTENCE_ADDRESS);
    consumer.handler(message -> {

      String action = message.body().getString(PERSISTENCE_ACTION);
      System.out.println(action);

      switch (action) {
        case PERSISTENCE_ACTION_REGISTER:
          registerUser(message);
          break;
        case PERSISTENCE_ACTION_LOGIN:
          loginUser(message);
          break;
        default:
          message.fail(1, "Unkown action: " + message.body());
      }
    });

    startFuture.complete();

  }

  private void loginUser(Message<JsonObject> message) {

    JsonObject userJson = new JsonObject(message.body().getString("user"));

    JsonObject authInfo = new JsonObject()
      .put("username", userJson.getString("email"))
      .put("password", userJson.getString("password"));

    String salt = authProvider.generateSalt();
    String saltedPassword = authProvider.computeHash("jakejake", salt);

    System.out.println(salt);
    System.out.println(saltedPassword);


    authProvider.authenticate(authInfo, ar -> {

      if (ar.failed()) {
        message.reply(
          new JsonObject()
            .put(PERSISTENCE_OUTCOME, PERSISTENCE_OUTCOME_FAILURE)
            .put(PERSISTENCE_OUTCOME_MESSAGE, ar.cause()));
      }else{
        jdbcClient.queryWithParams(
          SQL_SELECT_USER_BY_EMAIL,
          new JsonArray().add(userJson.getString("email")),
          res -> {
            if (res.failed()) {
              message.reply(
                new JsonObject()
                  .put(PERSISTENCE_OUTCOME, PERSISTENCE_OUTCOME_FAILURE)
                  .put(PERSISTENCE_OUTCOME_MESSAGE, res.cause()));
            }else{
              ResultSet resultSet = res.result();
              if (resultSet.getNumRows() == 0) {
                message.reply(
                  new JsonObject()
                    .put(PERSISTENCE_OUTCOME, PERSISTENCE_OUTCOME_FAILURE)
                    .put(PERSISTENCE_OUTCOME_MESSAGE, PersistenceErrorCodes.NOT_FOUND.message));
              }else{
                JsonArray rs = resultSet.getResults().get(0);
                message.reply(
                  new JsonObject()
                    .put(PERSISTENCE_OUTCOME, PERSISTENCE_OUTCOME_SUCCESS));
              }
            }
          });
      }

    });
  }


  private void registerUser(Message<JsonObject> message) {

    JsonObject userJson = new JsonObject(message.body().getString("user"));
    User user = new User(userJson);

    String salt = authProvider.generateSalt();
    String saltedPassword = authProvider.computeHash(user.getPassword(), salt);
    user.setPassword(saltedPassword);
    user.setPassword_salt(salt);

    jdbcClient.updateWithParams(SQL_REGISTER_USER, new JsonArray()
      .add(user.getUsername())
      .add(user.getEmail())
      .add(user.getPassword())
      .add(user.getPassword_salt())
      , res -> {
      if (res.succeeded()) {
        UpdateResult updateResult = res.result();
        System.out.println("No. of rows updated: " + updateResult.getUpdated());
        if (updateResult.getUpdated() >= 1) {
          message.reply(new JsonObject().put(PERSISTENCE_OUTCOME, PERSISTENCE_OUTCOME_SUCCESS));
        }else{
          message.fail(PersistenceErrorCodes.DB_INSERT_FAILURE.ordinal(), PersistenceErrorCodes.DB_INSERT_FAILURE  + res.cause().getMessage());
        }
      } else {
        message.fail(PersistenceErrorCodes.DB_CONNECTION_ERROR.ordinal(), PersistenceErrorCodes.DB_CONNECTION_ERROR + res.cause().getMessage());
      }

    });
  }

}
