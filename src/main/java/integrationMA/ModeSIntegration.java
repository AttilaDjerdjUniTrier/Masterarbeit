package integrationMA;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class ModeSIntegration {
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		context.addRoutes(new RouteBuilder() {
			public void configure() throws Exception {
				getContext().addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));

				// @formatter:off
				from("file:ModeSIntegration_in?noop=true")
					.choice()
						.when(header("CamelFileName").endsWith(".txt"))
							.to("seda:txtSplitter")
						.when(header("CamelFileName").endsWith(".json"))
							.to("seda:jsonSplitterContentFilter")
						.when(header("CamelFileName").endsWith(".csv"))
							.to("seda:csvContentFilter")
				;
				
				
				
				from("seda:jsonSplitterContentFilter")
					.split(body().tokenize("\n"))
					.transform().jsonpath("$.rawMessage")
				.to("seda:translator?blockWhenFull=true")
				;
				
				
				from("seda:txtSplitter")
					.split(body().tokenize("\\*"))
					.process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							String transformed = exchange.getIn().getBody().toString();
							transformed = transformed.replace(";", "");
							transformed =  transformed.trim();
							exchange.getIn().setBody(transformed);
						}
					})
					.to("seda:translator?blockWhenFull=true")
				;
				
				
				from("seda:csvContentFilter")
					.unmarshal().csv()
					.process(new Processor() {						
						public void process(Exchange exchange) throws Exception {
							List<List<String>>  data = (List<List<String>>) exchange.getIn().getBody();
							String tmp;
							for (List<String> list : data) {
								tmp = list.get(2);
								list.clear();
								list.add(tmp);
							}
							exchange.getIn().setBody(data);
						}
					})
					.marshal().csv()
				.to("seda:csvTransform")
				;
				
				
				from("seda:translator")
					.process(Utilities.hexToBin())
				.to("seda:filterADSB?blockWhenFull=true")
				;
				
				
				from("seda:filterADSB")
					.process(Utilities.convertBinToDezimalAndSetHeader(0, 5, "DownlinkFormat"))
					.filter(simple("${header.DownlinkFormat} == '18' || ${header.DownlinkFormat} == '17'"))
				.to("seda:decoder?blockWhenFull=true")
				;
				
				
				from("seda:decoder")
					.process(Utilities.convertBinToDezimalAndSetHeader(32, 37, "TypeCode"))
					.setHeader("ADSB-Data", simple("${body.substring(32, 88)}"))
					.process(Utilities.binToHex())
					.setHeader("ICAO", simple("${body.substring(2, 8)}"))
					.process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
							data.put("Icao", exchange.getIn().getHeader("ICAO").toString());
							data.put("Type", exchange.getIn().getHeader("TypeCode").toString());
							data.put("Data", exchange.getIn().getHeader("ADSB-Data").toString());
							data.put("ADSB", exchange.getIn().getBody().toString());						
							exchange.getIn().setBody(data);
						}
					})
					.marshal().csv()
				.to("seda:aggregate?blockWhenFull=true")
				;
				
				
				from("seda:csvTransform")
					.unmarshal().csv()
					.process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							List<List<String>>  data = (List<List<String>>) exchange.getIn().getBody();
							for (List<String> list : data) {
								list.add("");
								list.add("");
								list.add("");
							}
							exchange.getIn().setBody(data);
						}
					})
					.marshal().csv()
				.to("seda:aggregate")
				;
				
				
				from("seda:aggregate")
					.convertBodyTo(String.class)
					.aggregate(constant(true), new AggregationStrategy() {
						
						public Exchange aggregate(Exchange oldEx, Exchange newEx) {
							if(oldEx == null) {
								oldEx = newEx;
							} else {
								String oldBody = oldEx.getIn().getBody().toString();
								String newBody = newEx.getIn().getBody().toString();
								String combBody = oldBody + newBody;
								oldEx.getIn().setBody(combBody);
							}
							return oldEx;
						}
					})
					.completionTimeout(1000)
					.log("done")
				.to("file:ModeSIntegration_out?fileName=Integrated_ModeSData.csv")
				;
				// @formatter:on
			}
		});

		while (true) {
			context.start();
		}
	}
}
