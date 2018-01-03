package com.foo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ProcessorVerticle extends AbstractVerticle {

    private static final String template = "[%s][%s] Hello, %s!";

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();
        final String hostname = InetAddress.getLocalHost().getHostName();

        // Register a consumer and call the `reply` method with a JSON object containing the greeting message. ~
        // parameter is passed in the incoming message body (a name). For example, if the incoming message is the
        // String "vert.x", the reply contains: `{"message" : "hello vert.x"}`.
        // Unlike the previous exercise, the incoming message has a `String` body.
        eventBus.<String>consumer("greetings", msg -> {
            JsonObject response = new JsonObject()
                .put("content", String.format(template, hostname, Thread.currentThread().getName(), msg.body()));
            msg.reply(response);
        });

        final String name = InetAddress.getLocalHost().getHostAddress();

        // Ping
        eventBus.consumer("ping", message -> {
            System.out.println("ping received by " + name + " " + message.body());
        });
    }

}
