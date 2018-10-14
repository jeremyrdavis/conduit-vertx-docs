package io.vertx.conduit;

import io.vertx.conduit.model.User;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Registration Endpoint Tests")
@ExtendWith(VertxExtension.class)
public class RegistrationEndpointTest {

  @Test
  public void testRegisteringNewUser(Vertx vertx, VertxTestContext testContext){
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> {
      WebClient webClient = WebClient.create(vertx);
      webClient.post(8080, "localhost", "/api/users")
        .sendJsonObject(new JsonObject()
          .put("user", new JsonObject()
            .put("username", "Jacob")
            .put("email", "jake@jake.jake")
            .put("password", "jakejake")
          ), response -> testContext.verify(() -> {
          assertEquals(201, response.result().statusCode());
          User returnedUser = new User(response.result().bodyAsJsonObject().getJsonObject("user"));
          assertEquals("jake@jake.jake", returnedUser.getEmail());
          assertEquals("Jacob", returnedUser.getUsername());
          testContext.completeNow();
        }));
    }));
  }

}
