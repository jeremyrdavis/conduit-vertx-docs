package io.vertx.conduit;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.vertx.conduit.TestProps.DB_URL_TEST;
import static io.vertx.conduit.TestProps.DB_USER_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Authentication Endpoint Tests")
@ExtendWith(VertxExtension.class)
public class AuthenticationEndpointTest {

  void setUp(Vertx vertx, VertxTestContext testContext) {

    Flyway flyway = Flyway.configure().dataSource(DB_URL_TEST, DB_USER_TEST, null).load();
    flyway.migrate();

    testContext.completeNow();
  }

  public void testAuthentication(Vertx vertx, VertxTestContext testContext){
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> {
      WebClient webClient = WebClient.create(vertx);
      webClient.post(8080, "localhost", "/api/users/login")
        .sendJsonObject(new JsonObject()
          .put("user", new JsonObject()
            .put("email", "jake@jake.jake")
            .put("password", "jakejake")
          ), response -> testContext.verify(() -> {
          assertEquals(200, response.result().statusCode());
          JsonObject user = response.result().bodyAsJsonObject().getJsonObject("user");
          System.out.println(user.encodePrettily());
          assertEquals("jake@jake.jake", user.getString("email"));
          assertEquals("jakejake", user.getString("password"));
          assertNotNull( user.getString("token"));
          assertEquals("jake", user.getString("username"));
          assertEquals("I work at statefarm", user.getString("bio"));
          assertEquals("", user.getString("image"));
          testContext.completeNow();
        }));
    }));
  }

}
