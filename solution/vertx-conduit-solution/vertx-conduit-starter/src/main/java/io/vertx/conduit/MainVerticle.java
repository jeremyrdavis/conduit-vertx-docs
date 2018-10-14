package io.vertx.conduit;

import io.vertx.conduit.model.User;
import io.vertx.conduit.persistence.PersistenceVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import static io.vertx.conduit.ApplicationProps.*;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {

    Router baseRouter = Router.router(vertx);
    baseRouter.route("/").handler(this::indexHandler);

    Router apiRouter = Router.router(vertx);
    apiRouter.route("/*").handler(BodyHandler.create());
    apiRouter.post("/users").handler(this::registrationHandler);

    baseRouter.mountSubRouter("/api", apiRouter);

    vertx.deployVerticle(new PersistenceVerticle(), ar -> {
      if (ar.succeeded()) {
        vertx
          .createHttpServer()
          .requestHandler(baseRouter::accept)
          .listen(8080, result -> {
            if (result.succeeded()) {
              startFuture.complete();
            }else {
              startFuture.fail(result.cause());
            }
          });
      }else{
        startFuture.fail(ar.cause());
      }
    });
  }

  private void registrationHandler(RoutingContext routingContext) {

    User user = Json.decodeValue(routingContext.getBodyAsJson().getJsonObject("user").toString(), User.class);

    JsonObject message = new JsonObject()
      .put(PERSISTENCE_ACTION, PERSISTENCE_ACTION_REGISTER)
      .put("user", Json.encode(user));

    vertx.eventBus().send(PERSISTENCE_ADDRESS, message, ar -> {
      if (ar.succeeded()) {
        String msg = ((JsonObject) ar.result().body()).getString("outcome");
        if(PERSISTENCE_OUTCOME_SUCCESS.equalsIgnoreCase(msg)){
          routingContext.response()
            .setStatusCode(201)
            .putHeader("Content-Type", "application/json; charset=utf-8")
            .end(user.toJsonObject().toString());
        }
      } else {
        routingContext.response()
          .setStatusCode(201)
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(new JsonObject().put("message", "failed: " + ar.cause()).encode());
      }
    });
  }

  private void indexHandler(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    response
      .putHeader("Content-Type", "text/html")
      .end("Hello, Conduit!");
  }

}
