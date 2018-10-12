package io.vertx.conduit;

import io.vertx.conduit.model.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {

    Router baseRouter = Router.router(vertx);
    baseRouter.route("/").handler(this::indexHandler);

    Router apiRouter = Router.router(vertx);
    apiRouter.route("/*").handler(BodyHandler.create());
    apiRouter.post("/users").handler(this::registrationHandler);

    baseRouter.mountSubRouter("/api", apiRouter);

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
  }

  private void registrationHandler(RoutingContext routingContext) {

    JsonObject user = routingContext.getBodyAsJson().getJsonObject("user");

    User returnValue = new User("jake@jake.jake", null, "Jacob", "I work at state farm", null);

    routingContext.response()
      .setStatusCode(201)
      .putHeader("Content-Type", "application/json; charset=utf-8")
      .end(String.valueOf(returnValue.toJsonObject()));
  }

  private void indexHandler(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    response
      .putHeader("Content-Type", "text/html")
      .end("Hello Conduit!");
  }

}
