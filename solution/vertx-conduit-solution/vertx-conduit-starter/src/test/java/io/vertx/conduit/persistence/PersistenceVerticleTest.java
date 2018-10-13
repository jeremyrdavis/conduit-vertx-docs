package io.vertx.conduit.persistence;

import io.vertx.conduit.model.User;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.vertx.conduit.ApplicationProps.*;
import static io.vertx.conduit.TestProps.*;
import static io.vertx.conduit.persistence.DatabaseProps.*;
import static io.vertx.conduit.persistence.SQLQueries.SQL_SELECT_USER_BY_EMAIL;

@DisplayName("Persistence Event Bus Tests")
@ExtendWith(VertxExtension.class)
public class PersistenceVerticleTest {

  private JDBCClient jdbcClient;


  @BeforeEach
  void setUp(Vertx vertx, VertxTestContext testContext) {

    jdbcClient = JDBCClient.createShared(vertx, new JsonObject()
      .put(DB_URL_KEY, DB_URL_TEST)
      .put(DB_DRIVER_KEY, DB_DRIVER_TEST)
      .put(DB_USER_KEY, DB_USER_TEST)
      .put(DB_POOL_SIZE_KEY, DB_POOL_SIZE_TEST));

    Flyway flyway = Flyway.configure().dataSource(DB_URL_TEST, DB_USER_TEST, null).load();
    // Run the migration
    flyway.migrate();

    Checkpoint deploymentCheckpoint = testContext.checkpoint();

    JsonObject eventBusDeploymentConfig = new JsonObject()
      .put(DB_URL_KEY, DB_URL_TEST)
      .put(DB_DRIVER_KEY, DB_DRIVER_TEST)
      .put(DB_USER_KEY, DB_USER_TEST)
      .put(DB_POOL_SIZE_KEY, DB_POOL_SIZE_TEST);

    vertx.deployVerticle(new PersistenceVerticle(), new DeploymentOptions().setConfig(eventBusDeploymentConfig),testContext.succeeding(id -> {
        deploymentCheckpoint.flag();
        Assertions.assertFalse(id.isEmpty());
    }));

    testContext.completeNow();
  }

  @Test
  @DisplayName("Authenticate User Test")
  @Timeout(10000)
  void testJDBCAuthorizationOverEventBus(Vertx vertx, VertxTestContext testContext) {
    Checkpoint replyCheckpoint = testContext.checkpoint();

    User user = new User("jake@jake.jake", "password");

    JsonObject message = new JsonObject()
      .put(PERSISTENCE_ACTION, PERSISTENCE_ACTION_LOGIN)
      .put("user", Json.encode(user));

    vertx.eventBus().send(PERSISTENCE_ADDRESS, message, testContext.succeeding(ar -> {
      testContext.verify(() -> {
        System.out.println(ar.body());
        Assertions.assertEquals(PERSISTENCE_OUTCOME_SUCCESS, ((JsonObject) ar.body()).getString("outcome"));
        replyCheckpoint.flag();
        testContext.completeNow();
      });

    }));
  }

  @Test
  @DisplayName("Register User Test")
  @Timeout(10000)
  void testServerRegisterUserOverEventBus(Vertx vertx, VertxTestContext testContext) {

    Checkpoint replyCheckpoint = testContext.checkpoint();

    User user = new User("user1@user.com", null, "user1", "user1's bio", null, "password");

    JsonObject message = new JsonObject()
      .put(PERSISTENCE_ACTION, PERSISTENCE_ACTION_REGISTER)
      .put("user", Json.encode(user));

    JsonObject eventBusDeploymentConfig = new JsonObject()
      .put(DB_URL_KEY, DB_URL_TEST)
      .put(DB_DRIVER_KEY, DB_DRIVER_TEST)
      .put(DB_USER_KEY, DB_USER_TEST)
      .put(DB_POOL_SIZE_KEY, DB_POOL_SIZE_TEST);


      vertx.eventBus().send(PERSISTENCE_ADDRESS, message, testContext.succeeding(ar -> {
        testContext.verify(() -> {
          System.out.println(ar.body());

          // query database to verify insert
          jdbcClient.getConnection(conn ->{
            if (conn.failed()) {
              Assertions.assertTrue(conn.succeeded());
              testContext.completeNow();
            }
            final SQLConnection connection = conn.result();

            connection.queryWithParams(SQL_SELECT_USER_BY_EMAIL, new JsonArray().add(user.getEmail()), rs -> {
              if (rs.failed()) {
                Assertions.assertTrue(rs.succeeded());
                testContext.completeNow();
              }
              Assertions.assertEquals(1, rs.result().getNumRows());
              System.out.println(rs.result().getResults().get(0).encode());
            });
          });

          Assertions.assertEquals(PERSISTENCE_OUTCOME_SUCCESS, ((JsonObject) ar.body()).getString("outcome"));
          replyCheckpoint.flag();
          testContext.completeNow();
        });
      }));
  }

}
