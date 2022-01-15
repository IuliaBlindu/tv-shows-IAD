package com.iad.tvshowsiad.mail;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class MailboxInputApplication {

	public static void main(String[] args) {
		MailboxInputRouteBuilder routeBuilder = new MailboxInputRouteBuilder();
		CamelContext ctx = new DefaultCamelContext();
		try {
			ctx.addRoutes(routeBuilder);
			ctx.start();
			Thread.sleep(5 * 60 * 1000);
			ctx.stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
