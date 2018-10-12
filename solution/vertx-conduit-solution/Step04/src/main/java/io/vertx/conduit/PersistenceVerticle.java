package io.vertx.conduit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

public class PersistenceVerticle extends AbstractVerticle {

  public static final String PERSISTENCE_ADDRESS = "persistence-address";
  public static final String PERSISTENCE_ACTION = "action";
  public static final String PERSISTENCE_ACTION_REGISTER = "register";
  public static final String PERSISTENCE_OUTCOME = "outcome";
  public static final String PERSISTENCE_OUTCOME_SUCCESS = "success";
  public static final String PERSISTENCE_OUTCOME_FAILURE = "failure";


  @Override
  public void start(Future<Void> startFuture) throws Exception {

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
    message.reply(new JsonObject().put(PERSISTENCE_OUTCOME, PERSISTENCE_OUTCOME_FAILURE));
  }
}
