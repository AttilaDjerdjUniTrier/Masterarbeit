package integrationMA;

import org.apache.camel.Exchange;
import org.apache.camel.Header;

public class DynamicRouterBean {
	public String route(@Header(Exchange.SLIP_ENDPOINT) String previous, @Header("query") String query,
			@Header("FlightAlt") String flightPos, @Header("twitter") String twitter) {

		if (previous == null && query != null) {
			return "direct://queryAPI";

		} else if (previous == null || previous.equals("direct://queryAPI") && flightPos != null) {
			return "direct://flightPosAPI";

		} else if (previous.equals("direct://queryAPI") | previous.equals("direct://flightPosAPI") && twitter != null) {
			return "direct://twitterAPI";

		} else {
			return null;

		}
	}
}
