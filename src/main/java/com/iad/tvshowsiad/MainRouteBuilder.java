package com.iad.tvshowsiad;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MainRouteBuilder extends RouteBuilder {

    @Autowired
    private MainConfiguration configuration;

    @Override
    public void configure() throws Exception {
        fromF("jetty:http://0.0.0.0:%d/games/?matchOnUriPrefix=true", configuration.getPort())
        .setHeader("category").header(Exchange.HTTP_PATH)
        .choice()
        .when(exchange ->
            Paths.get(String.format("cache/%s.json",
                                exchange.getIn().getHeader("category", String.class)))
                        .toFile().exists()
        )
        .setBody(exchange -> {
            return Paths.get(String.format("cache/%s.json", exchange.getIn().getHeader("category", String.class))).toFile();
        })
        .convertBodyTo(String.class)
        .otherwise()
            .process(x -> {
                System.out.println(x);
            })
            .removeHeaders("*", "category")
            .setHeader(Exchange.HTTP_URI).header("url")
            .setHeader(Exchange.HTTP_METHOD).constant("GET")
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
            .setHeader("Accept", constant("application/json"))
            .setHeader(Exchange.HTTP_QUERY)
            .simple("category=${header.category}")
            .to("https://www.freetogame.com/api/games")
            .convertBodyTo(String.class)
            .unmarshal().json(JsonLibrary.Jackson)
            .setHeader("id", () -> UUID.randomUUID().toString())
            .setHeader("size").simple("${body.size}")
//            .setBody(exchange -> {
//                List<Object> items = exchange.getIn().getBody(List.class);
//                int count = items.size();
//                return count;
//            })
            .marshal().json(JsonLibrary.Jackson)
            .process(exchange -> {
                Files.write(Paths.get(String.format("cache/%s.json", exchange.getIn().getHeader("category", String.class))),
                        exchange.getIn().getBody(String.class).getBytes());
            })
            .setBody().constant("")
            .setBody(exchange -> Paths.get(String.format("cache/%s.json", exchange.getIn().getHeader("category", String.class))).toFile())
            .convertBodyTo(String.class);
    }


}
