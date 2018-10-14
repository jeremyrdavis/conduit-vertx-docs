package io.vertx.conduit;

import io.vertx.conduit.model.User;
import io.vertx.conduit.persistence.PersistenceVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.vertx.conduit.TestProps.*;
import static io.vertx.conduit.persistence.DatabaseProps.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Registration Endpoint Tests")
@ExtendWith(VertxExtension.class)
public class RegistrationEndpointTest {

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

    JsonObject eventBusDeploymentConfig = new JsonObject()
      .put(DB_URL_KEY, DB_URL_TEST)
      .put(DB_DRIVER_KEY, DB_DRIVER_TEST)
      .put(DB_USER_KEY, DB_USER_TEST)
      .put(DB_POOL_SIZE_KEY, DB_POOL_SIZE_TEST);


    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> {
      testContext.completeNow();
    }));
    vertx.deployVerticle(new PersistenceVerticle(),new DeploymentOptions().setConfig(eventBusDeploymentConfig), testContext.succeeding(ar ->  {
      testContext.completeNow();
    }));
  }

  @AfterEach
  void tearDown(Vertx vertx, VertxTestContext testContext) {
    vertx.close();
    testContext.completeNow();
  }


  @Test @Timeout(5000)
  public void testRegisteringNewUser(Vertx vertx, VertxTestContext testContext){

      WebClient webClient = WebClient.create(vertx);

      webClient.post(8080, "localhost", "/api/users")
        .sendJsonObject(new JsonObject()
          .put("user", new JsonObject()
            .put("username", "User2")
            .put("email", "user2@user2.user2")
            .put("password", "user2user2")
          ), testContext.succeeding(response ->  testContext.verify(()->{
            assertEquals(201, response.statusCode());
            testContext.completeNow();
          })));


/*
      webClient.post(8080, "localhost", "/api/users")
        .sendJsonObject(new JsonObject()
          .put("user", new JsonObject()
            .put("username", "User2")
            .put("email", "user2@user2.user2")
            .put("password", "user2user2")
          ), testContext.succeeding(ar ->  {
          System.out.println("verifying registration");
          assertEquals(201, ar.statusCode());
          User returnedUser = new User(ar.bodyAsJsonObject().getJsonObject("user"));
          assertEquals("user2@user2.user2", returnedUser.getEmail());
          assertEquals("User2", returnedUser.getUsername());
          testContext.completeNow();
        }));
*/
  }

}
