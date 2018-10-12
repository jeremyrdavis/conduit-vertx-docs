package io.vertx.conduit;

import io.vertx.conduit.model.User;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.vertx.conduit.DatabaseVerticle.PERSISTENCE_ACTION;
import static io.vertx.conduit.DatabaseVerticle.PERSISTENCE_ADDRESS;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Database Event Bus Tests")
@ExtendWith(VertxExtension.class)
public class DatabaseVerticleTest {

  @BeforeEach
  void setUp(Vertx vertx, VertxTestContext testContext) {
    Checkpoint deploymentCheckpoint = testContext.checkpoint();
    vertx.deployVerticle(new DatabaseVerticle(), testContext.succeeding(id -> {
      assertNotNull(id);
      deploymentCheckpoint.flag();
    }));
  }

  @AfterEach
  void tearDown(Vertx vertx, VertxTestContext testContext) {
    vertx.close();
  }

  @Test
  @DisplayName("Register User Test")
  void testServerRegisterUserOverEventBus(Vertx vertx, VertxTestContext testContext) {

    Checkpoint replyCheckpoint = testContext.checkpoint();

    User user = new User("user1@user.com", null, "user1", "user1's bio", null);

    JsonObject message = new JsonObject()
      .put(PERSISTENCE_ACTION, "registerUser")
      .put("user", Json.encode(user));

    vertx.eventBus().send(PERSISTENCE_ADDRESS, message, ar -> {
      assertTrue(ar.succeeded());
      JsonObject result = (JsonObject) ar.result().body();
      assertEquals("success", result.getString("outcome"));
      replyCheckpoint.flag();
    });
  }

}
