package io.vertx.conduit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

public class PersistenceVerticle extends AbstractVerticle {

  public static final String PERSISTENCE_ADDRESS = "persistence-address";
  public static final String PERSISTENCE_ACTION = "action";
  public static final String PERSISTENCE_ACTION_REGISTER = "register";
  public static final String PERSISTENCE_OUTCOME = "outcome";
  public static final String PERSISTENCE_OUTCOME_SUCCESS = "success";
  public static final String PERSISTENCE_OUTCOME_FAILURE = "failure";

  public static final String DB_DRIVER_KEY = "driver_class";
  public static final String DB_POOL_SIZE_KEY = "max_pool_size";
  public static final String DB_URL_KEY = "url";
  public static final String DB_USER_KEY = "user";

  private static final String DB_DRIVER_DEFAULT = "org.hsqldb.jdbcDriver";
  private static final Integer DB_POOL_SIZE_DEFAULT = 30;
  private static final String DB_URL_DEFAULT = "jdbc:hsqldb:file:db/conduit;shutdown=true";
  private static final String DB_USER_DEFAULT = "sa";

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
    message.reply(new JsonObject().put(PERSISTENCE_OUTCOME, PERSISTENCE_OUTCOME_SUCCESS));
  }
}
