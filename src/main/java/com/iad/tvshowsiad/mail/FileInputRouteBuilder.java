package com.iad.tvshowsiad.mail;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.YAMLLibrary;

import java.util.ResourceBundle;

public class FileInputRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		final ResourceBundle bundle = ResourceBundle.getBundle("application");
		from("file:data?delete=true")
		.convertBodyTo(String.class)
		.unmarshal().yaml(YAMLLibrary.SnakeYAML)
		.removeHeaders("*")
		.setHeader("subject").simple("${body[subject]}")
		.setHeader("message").simple("${body[message]}")
		.to("sql:select * from contacts")
		.split().body()
		.setHeader("from").constant(bundle.getString("from"))
		.setHeader("to").simple("${body[email]}")
		.bean(Utilities.class, "replaceFields")
		.removeHeader("message")
		.log("Sending '${body}' to ${header.to}")
		.toF("smtp://localhost:%d", Integer.parseInt(bundle.getString("smtp.port")));
	}


}
