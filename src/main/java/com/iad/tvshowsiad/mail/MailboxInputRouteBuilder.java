package com.iad.tvshowsiad.mail;

import org.apache.camel.builder.RouteBuilder;

public class MailboxInputRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("file:message").doTry().setHeader("subject", simple("JavaInUse Invitation111"))
				.setHeader("to", simple("javainuse@gmail.com,testouthworking@gmail.com"))
				.to("smtps://smtp.gmail.com:465?username=proiect.iad.games@gmail.com&password=proiectiad");
	}

}
