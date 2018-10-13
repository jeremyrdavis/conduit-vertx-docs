package io.vertx.conduit.persistence;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.vertx.conduit.persistence.DatabaseProps.*;
import static io.vertx.conduit.persistence.TestDatabaseProps.*;

@DisplayName("Persistence Event Bus Tests")
@ExtendWith(VertxExtension.class)
public class NewPersistenceVerticleTest {

  private JDBCClient jdbcClient;


  @BeforeEach
  void setUp(Vertx vertx, VertxTestContext testContext) {

    jdbcClient = JDBCClient.createShared(vertx, new JsonObject()
      .put(DB_URL_KEY, DB_URL_TEST)
      .put(DB_DRIVER_KEY, DB_DRIVER_TEST)
      .put(DB_USER_KEY, DB_USER_TEST)
      .put(DB_POOL_SIZE_KEY, DB_POOL_SIZE_TEST));

    Flyway flyway = Flyway.configure().dataSource(DB_URL_TEST, DB_USER_TEST, null).load();
    flyway.migrate();

    testContext.completeNow();
  }

  @Test
  @DisplayName("Register User Test")
  @Timeout(10000)
  void testServerRegisterUserOverEventBus(Vertx vertx, VertxTestContext testContext) {

    Assertions.assertTrue(false);
    testContext.completeNow();
  }
}
