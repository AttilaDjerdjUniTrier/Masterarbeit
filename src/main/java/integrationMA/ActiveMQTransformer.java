package integrationMA;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class ActiveMQTransformer {
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));

		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				// @formatter:off
				from("file:ModeSIntegration_out?noop=true")
					.split(body().tokenize("\n"))
					.unmarshal().csv()
					.setHeader("TypeCode", simple("${body[0].[1]}"))
					.marshal().csv()
					.convertBodyTo(String.class)
					.choice()
						.when(header("TypeCode").isEqualTo(""))
							.to("activemq:noneTypeCode")
						.when(simple("${header.TypeCode} range '9..18'"))
							.to("activemq:flightPos")
						.otherwise()
							.to("activemq:noFlightPos")
					.log("done")
				;
				// @formatter:on
			}
		});

		while (true) {
			context.start();
		}
	}
}
