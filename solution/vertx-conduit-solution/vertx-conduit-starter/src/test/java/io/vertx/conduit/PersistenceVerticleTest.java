package io.vertx.conduit;

import io.vertx.conduit.model.User;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.vertx.conduit.PersistenceVerticle;

import static io.vertx.conduit.PersistenceVerticle.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Persistence Event Bus Tests")
@ExtendWith(VertxExtension.class)
public class PersistenceVerticleTest {

  @Test
  @DisplayName("Register User Test")
  @Timeout(2000)
  void testServerRegisterUserOverEventBus(Vertx vertx, VertxTestContext testContext) {

    Checkpoint deploymentCheckpoint = testContext.checkpoint();
    Checkpoint replyCheckpoint = testContext.checkpoint();

    User user = new User("user1@user.com", null, "user1", "user1's bio", null);

    JsonObject message = new JsonObject()
      .put(PERSISTENCE_ACTION, PERSISTENCE_ACTION_REGISTER)
      .put("user", Json.encode(user));

    vertx.deployVerticle(new PersistenceVerticle(), testContext.succeeding(id -> {
      deploymentCheckpoint.flag();
      vertx.eventBus().send(PERSISTENCE_ADDRESS, message, testContext.succeeding(ar -> {
        testContext.verify(() -> {
          System.out.println(ar.body());
          assertEquals(PERSISTENCE_OUTCOME_SUCCESS, ((JsonObject) ar.body()).getString("outcome"));
          replyCheckpoint.flag();
          testContext.completeNow();
        });
      }));
    }));


  }

}
