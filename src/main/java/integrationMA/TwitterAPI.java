package integrationMA;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestParamType;
import org.apache.camel.test.AvailablePortFinder;

public class TwitterAPI {
	static int myPort = AvailablePortFinder.getNextAvailable(9095, 9096);

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		PropertiesComponent pc = (PropertiesComponent) context.getPropertiesComponent();

		pc.setLocation("classpath:myApplication.properties");

		context.addRoutes(new RouteBuilder() {
			public void configure() throws Exception {
				// @formatter:off				
				restConfiguration().component("netty-http").port(myPort).clientRequestValidation(true);
	
				rest("/")
					.get()
						.param().type(RestParamType.query).name("Twitter").allowableValues("true").required(true).endParam()
						.route().to("seda:postOnTwitterTimeline")
				.endRest()
			    ;
				
				from("seda:postOnTwitterTimeline")
				.log(("in Twit"))
					.to("twitter-timeline:USER?user={{twitter.user}}&consumerKey={{twitter.consumerKey}}&consumerSecret={{twitter.consumerSecret}}&accessToken={{twitter.accessToken}}&accessTokenSecret={{twitter.accessTokenSecret}}")
				;
				// @formatter:on
			}
		});

		while (true) {
			context.start();
		}
	}

}
