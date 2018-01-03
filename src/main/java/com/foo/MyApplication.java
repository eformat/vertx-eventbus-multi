package com.foo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class MyApplication extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.deployVerticle(HttpVerticle.class.getName());
        vertx.deployVerticle(ProcessorVerticle.class.getName());
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(HttpVerticle.class.getName());
        vertx.deployVerticle(ProcessorVerticle.class.getName());
    }
}
