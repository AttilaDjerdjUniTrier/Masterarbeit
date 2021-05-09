package integrationMA;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultRegistry;
import org.apache.commons.dbcp2.BasicDataSource;

public class DatabaseIntegrator {
	public static void main(String[] args) throws Exception {

		String url = "jdbc:mysql://localhost:3306?useTimezone=true&serverTimezone=UTC";

		BasicDataSource dataSource = setupBasicDataSource(url);

		DefaultRegistry reg = new DefaultRegistry();

		reg.bind("db", BasicDataSource.class, dataSource);

		CamelContext context = new DefaultCamelContext(reg);

		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				// @formatter:off	
				from("file:setupLocalAsViewDatabase?noop=true")
					.split(body().tokenize(";"))
				.to("jdbc:db")
				
				.log("done")
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
