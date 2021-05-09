package integrationMA;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class APIGateway {
	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();

		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				// @formatter:off
				restConfiguration().component("netty-http").port(9092).clientRequestValidation(true);
				rest("/gateway")
			    	.get()
			    		.route().dynamicRouter(method(DynamicRouterBean.class, "route")).to("file:apiOut").endRest()
			    ;
				
				from("direct:queryAPI")
					.setHeader(Exchange.HTTP_METHOD, simple("GET"))
					.loadBalance().roundRobin()
						.to("netty-http://localhost:9093?bridgeEndpoint=true")
						.to("netty-http://localhost:9094?bridgeEndpoint=true")
				.end()
				;
				
				from("direct:twitterAPI")
					.setHeader(Exchange.HTTP_METHOD, simple("GET"))
					.loadBalance().roundRobin()
						.to("netty-http://localhost:9095?bridgeEndpoint=true")
						.to("netty-http://localhost:9096?bridgeEndpoint=true")
				.end()
				;
				
				from("direct:flightPosAPI")
					.setHeader(Exchange.HTTP_METHOD, simple("GET"))
					.to("netty-http://localhost:9099/?bridgeEndpoint=true")
				.end()
				;
				
				// @formatter:on
			}
		});

		while (true) {
			context.start();
		}
	}
}
