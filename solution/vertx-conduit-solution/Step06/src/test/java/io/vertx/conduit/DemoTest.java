package io.vertx.conduit;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Persistence Event Bus Tests")
@ExtendWith(VertxExtension.class)
public class DemoTest {

  @BeforeEach
  public void setUp(Vertx vertx, VertxTestContext testContext) {
    System.out.println("setUp");
    testContext.completeNow();
  }

  @Test
  public void test(Vertx vertx, VertxTestContext testContext) {
    assertTrue(true);
    testContext.completeNow();
  }
}
