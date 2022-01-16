package com.iad.tvshowsiad;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class MainRouteBuilder extends RouteBuilder {

    @Autowired
    private MainConfiguration configuration;

    @Override
    public void configure()  {
        fromF("jetty:http://0.0.0.0:%d/games/?matchOnUriPrefix=true", configuration.getPort())
                .setHeader("category").header(Exchange.HTTP_PATH)
                .choice()
                .when(exchange -> Paths.get(String.format("cache/%s.json", exchange.getIn().getHeader("category", String.class))).toFile().exists())
                .setBody(exchange -> Paths.get(String.format("cache/%s.json", exchange.getIn().getHeader("category", String.class))).toFile())
                .convertBodyTo(String.class)
                .to("direct:send-mail")
                .otherwise()
                .to("direct:send-mail");

        from("direct:send-mail")
                .removeHeaders("*", "category")
                .setHeader(Exchange.HTTP_URI).header("url")
                .setHeader(Exchange.HTTP_METHOD).constant("GET")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .setHeader(Exchange.HTTP_QUERY)
                .simple("category=${header.category}")
                .to("https://www.freetogame.com/api/games")
                .process(exchange -> Files.write(Paths.get(String.format("cache/%s.json", exchange.getIn().getHeader("category", String.class))),
                        exchange.getIn().getBody(String.class).getBytes())
                )
                .setBody().constant("")
                .setBody(exchange -> Paths.get(String.format("cache/%s.json", exchange.getIn().getHeader("category", String.class))).toFile())
                .convertBodyTo(String.class)
                .unmarshal().json(JsonLibrary.Jackson)
                .setHeader("id", () -> UUID.randomUUID().toString())
                .setHeader("size").simple("${body.size}")
                .split().body()
                .aggregate(Utilities::getGame)
                .header("id")
                .completionSize(header("size"))
                .marshal().json(JsonLibrary.Jackson)
                .process(exchange -> {
                    String body = exchange.getIn().getBody(String.class);
                    String parsedText = Utilities.replaceMessage(body);
                    exchange.getIn().setBody(parsedText);
                })
                .process(exchange ->
                        Files.write(Paths.get(String.format("message/%s.json", exchange.getIn().getHeader("category", String.class).toUpperCase())),
                                exchange.getIn().getBody(String.class).getBytes())
                )
                .end();

        from("file:message?delete=true").doTry()
                .setHeader("category").simple(Utilities.getCategory("${header.CamelFileName}"))
                .setHeader("subject").simple(Utilities.replaceSubject("${header.category}"))
                .setHeader("to", simple("javainuse@gmail.com"))
                .to("smtps://smtp.gmail.com:465?username=proiect.iad.games@gmail.com&password=proiectiad");

    }



}
