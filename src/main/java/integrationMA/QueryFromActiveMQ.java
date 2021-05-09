package integrationMA;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class QueryFromActiveMQ {
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				getContext().addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));
				// @formatter:off				
				from("activemq:noneTypeCode")
					.unmarshal().csv()
					.setHeader(Exchange.HTTP_METHOD).simple("GET")
					.setHeader(Exchange.HTTP_QUERY).simple("query=Modell&icao=${body[0][0]}")
					.setHeader(Exchange.HTTP_RAW_QUERY).simple("query=Modell&icao=${body[0][0]}")
					.setBody(simple(""))
					.to("netty-http://localhost:9092/gateway?bridgeEndpoint=true")
				.to("activemq:noneTypeCodeResult")
				;
				
				from("activemq:flightPos")
					.unmarshal().csv()
					.setHeader(Exchange.HTTP_METHOD).simple("GET")
					.setHeader(Exchange.HTTP_QUERY).simple("query=ModellBesitzer&icao=${body[0][0]}&FlightAlt=${body[0][2]}&twitter=true")
					.setHeader(Exchange.HTTP_RAW_QUERY).simple("query=ModellBesitzer&icao=${body[0][0]}&FlightAlt=${body[0][2]}&twitter=true")
					.setBody(simple(""))
					.to("netty-http://localhost:9092/gateway?bridgeEndpoint=true")
				.to("activemq:flightPosResult")
				;
					
				from("activemq:noFlightPos")
					.unmarshal().csv()
					.setHeader(Exchange.HTTP_METHOD).simple("GET")
					.setHeader(Exchange.HTTP_QUERY).simple("query=BesitzerRegAmRegBis&icao=${body[0][0]}")
					.setHeader(Exchange.HTTP_RAW_QUERY).simple("query=BesitzerRegAmRegBis&icao=${body[0][0]}")
					.setBody(simple(""))
					.to("netty-http://localhost:9092/gateway?bridgeEndpoint=true")
				.to("activemq:noFlightPosResult")
				;
				// @formatter:on
			}
		});

		while (true) {
			context.start();
		}
	}
}
