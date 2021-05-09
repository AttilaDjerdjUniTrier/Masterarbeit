package integrationMA;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestParamType;
import org.apache.camel.support.DefaultRegistry;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.commons.dbcp2.BasicDataSource;

public class QueryAPI {
	static int myPort = AvailablePortFinder.getNextAvailable(9093, 9094);

	public static void main(String[] args) throws Exception {
		String url = "jdbc:mysql://localhost:3306?useTimezone=true&serverTimezone=UTC";

		BasicDataSource dataSource = setupBasicDataSource(url);

		DefaultRegistry reg = new DefaultRegistry();

		reg.bind("db", BasicDataSource.class, dataSource);

		CamelContext context = new DefaultCamelContext(reg);

		context.addRoutes(new RouteBuilder() {
			public void configure() throws Exception {
				// @formatter:off				
				restConfiguration().component("netty-http").port(myPort).clientRequestValidation(true);
	
				rest("/")
					.get()
						.param().type(RestParamType.query).name("query").allowableValues("true").required(true).endParam()
						.route().to("seda:answerQuery")
				.endRest()
			    ;
				
				from("seda:answerQuery")
					.transform().simple("resource:classpath:${header.query}.sql")
					.to("jdbc:db")
					.setBody(simple("Informationen zu Icao: ${header.icao}\n"
							+ "Modell: ${body[0].[Modell]}\n"
							+ "Besitzer: ${body[0].[Besitzer]}\n"
							+ "Registriert: ${body[0].[RegAm]}\n"
							+ "Registriert bis: ${body[0].[RegBis]}\n"
							+ "Stadt: ${body[0].[Stadt]}" + 
							myPort))
					.convertBodyTo(String.class)
				;
				// @formatter:on
			}
		});

		while (true) {
			context.start();
		}
	}

	private static BasicDataSource setupBasicDataSource(String connectURI) {
		BasicDataSource ds = new BasicDataSource();
		ds.setUsername("root");
		ds.setPassword(null);
		ds.setUrl(connectURI);
		return ds;
	}
}
