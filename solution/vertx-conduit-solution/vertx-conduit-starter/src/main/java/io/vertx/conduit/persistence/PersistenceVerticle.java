package io.vertx.conduit.persistence;

import io.vertx.conduit.model.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.UpdateResult;

import static io.vertx.conduit.persistence.SQLQueries.SQL_REGISTER_USER;
import static io.vertx.conduit.persistence.DatabaseProps.*;

public class PersistenceVerticle extends AbstractVerticle {

  public static final String PERSISTENCE_ADDRESS = "persistence-address";
  public static final String PERSISTENCE_ACTION = "action";
  public static final String PERSISTENCE_ACTION_REGISTER = "register";
  public static final String PERSISTENCE_OUTCOME = "outcome";
  public static final String PERSISTENCE_OUTCOME_SUCCESS = "success";
  public static final String PERSISTENCE_OUTCOME_FAILURE = "failure";

  private JDBCClient jdbcClient;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    System.out.println(config().getString(DB_URL_KEY));

    jdbcClient = JDBCClient.createShared(vertx, new JsonObject()
      .put(DB_URL_KEY, config().getString(DB_URL_KEY, DB_URL_DEFAULT))
      .put(DB_DRIVER_KEY, config().getString(DB_DRIVER_KEY, DB_DRIVER_DEFAULT))
      .put(DB_USER_KEY, config().getString(DB_USER_KEY, DB_USER_DEFAULT))
      .put(DB_POOL_SIZE_KEY, config().getInteger(DB_POOL_SIZE_KEY, DB_POOL_SIZE_DEFAULT)));

    EventBus eventBus = vertx.eventBus();
    MessageConsumer<JsonObject> consumer = eventBus.consumer(PERSISTENCE_ADDRESS);
    consumer.handler(message -> {

      String action = message.body().getString(PERSISTENCE_ACTION);

      switch (action) {
        case PERSISTENCE_ACTION_REGISTER:
          registerUser(message);
          break;
        default:
          message.fail(1, "Unkown action: " + message.body());
      }
    });

    startFuture.complete();

  }

  private void registerUser(Message<JsonObject> message) {

    JsonObject userJson = new JsonObject(message.body().getString("user"));
    User user = new User(userJson);

    jdbcClient.updateWithParams(SQL_REGISTER_USER, new JsonArray()
      .add(user.getUsername())
      .add(user.getEmail())
      .add(user.getBio())
      .add(user.getPassword())
      , res -> {
      if (res.succeeded()) {
        UpdateResult updateResult = res.result();
        System.out.println("No. of rows updated: " + updateResult.getUpdated());
        if (updateResult.getUpdated() >= 1) {
          message.reply(new JsonObject().put(PERSISTENCE_OUTCOME, PERSISTENCE_OUTCOME_SUCCESS));
        }else{
          message.fail(1, "Error: " + res.cause().getMessage());
//          message.fail(FailureCodes.DB_ERROR.ordinal(), FailureCodes.DB_ERROR.failureCodeMessage + res.cause().getMessage());
        }
      } else {
        message.fail(1, "Error: " + res.cause().getMessage());
//        message.fail(FailureCodes.DB_ERROR.ordinal(), FailureCodes.DB_ERROR.failureCodeMessage + res.cause().getMessage());
      }

    });
  }
}
