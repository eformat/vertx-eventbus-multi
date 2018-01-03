package com.foo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class HttpVerticle extends AbstractVerticle {

    private static final String template = "[%s][%s] Hello, %s!";
    private boolean online = false;

    @Override
    public void start(Future<Void> future) throws Exception {
        Router router = Router.router(vertx);

        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx)
            .register("server-online", fut -> fut.complete(online ? Status.OK() : Status.KO()));

        router.get("/api/greeting").handler(this::greeting);
        router.get("/api/stop").handler(this::stop);
        router.get("/api/health/readiness").handler(rc -> rc.response().end("OK"));
        router.get("/api/health/liveness").handler(healthCheckHandler);
        router.get("/").handler(StaticHandler.create());

        vertx
            .createHttpServer()
            .requestHandler(router::accept)
            .listen(
                config().getInteger("http.port", 8080), ar -> {
                    online = ar.succeeded();
                    future.handle(ar.mapEmpty());
                });

        // Ping
        final String name = InetAddress.getLocalHost().getHostAddress();

        vertx.setPeriodic(5000, id -> {
            vertx.eventBus().publish("ping", "ping from " + name + " " + new Date());
        });
    }

    private void stop(RoutingContext rc) {
        rc.response().end("Stopping HTTP server, Bye bye cruel world !");
        online = false;
    }

    private void greeting(RoutingContext rc) {
        if (!online) {
            rc.response().setStatusCode(400).putHeader(CONTENT_TYPE, "text/plain").end("Not online");
            return;
        }

        String name = rc.request().getParam("name");
        if (name == null) {
            name = "World";
        }

        vertx.eventBus()
            .<JsonObject>send("greetings", name, reply -> {
                if (reply.failed()) {
                    rc.response().setStatusCode(500).end(reply.cause().getMessage());
                } else {
                    rc.response().end(reply.result().body().encode());
                }
            });
    }

}
