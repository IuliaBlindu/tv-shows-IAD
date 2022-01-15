package com.iad.tvshowsiad;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;


public class MainRouteBuilder extends RouteBuilder {

    @Autowired
    private MainConfiguration configuration;

    @Override
    public void configure() throws Exception {
        from("jetty:https://0.0.0.0:%d");
    }


}
