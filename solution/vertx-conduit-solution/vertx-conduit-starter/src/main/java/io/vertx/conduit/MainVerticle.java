package io.vertx.conduit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {

    Router router = Router.router(vertx);
    router.route("/").handler(this::indexHandler);

    vertx.createHttpServer()
        .requestHandler(req -> req.response().end("Hello, Conduit!"))
        .listen(8080);
    startFuture.complete();
  }

  private void indexHandler(RoutingContext routingContext) {
    HttpServerResponse response = routingContext.response();
    response
      .putHeader("Content-Type", "text/html")
      .end("Hello Conduit!");
  }

}
