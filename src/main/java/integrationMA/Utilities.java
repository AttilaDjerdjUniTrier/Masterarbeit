package integrationMA;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class Utilities {
	public static Processor formatFileName() {
		return new Processor() {

			public void process(Exchange exchange) throws Exception {
				String splitIndex = exchange.getProperty("CamelSplitIndex").toString();

				switch (splitIndex.length()) {
				case 1:
					splitIndex = "000" + splitIndex;
					break;
				case 2:
					splitIndex = "00" + splitIndex;
					break;
				case 3:
					splitIndex = "0" + splitIndex;
					break;
				default:
					break;
				}
				exchange.setProperty("CamelSplitIndex", splitIndex);
			}
		};
	}

	public static Processor hexToBin() {
		return new Processor() {

			public void process(Exchange exchange) throws Exception {
				String hex = exchange.getIn().getBody(String.class);
				hex = hex.replaceAll("0", "0000");
				hex = hex.replaceAll("1", "0001");
				hex = hex.replaceAll("2", "0010");
				hex = hex.replaceAll("3", "0011");
				hex = hex.replaceAll("4", "0100");
				hex = hex.replaceAll("5", "0101");
				hex = hex.replaceAll("6", "0110");
				hex = hex.replaceAll("7", "0111");
				hex = hex.replaceAll("8", "1000");
				hex = hex.replaceAll("9", "1001");
				hex = hex.replaceAll("A", "1010");
				hex = hex.replaceAll("a", "1010");
				hex = hex.replaceAll("B", "1011");
				hex = hex.replaceAll("b", "1011");
				hex = hex.replaceAll("C", "1100");
				hex = hex.replaceAll("c", "1100");
				hex = hex.replaceAll("D", "1101");
				hex = hex.replaceAll("d", "1101");
				hex = hex.replaceAll("E", "1110");
				hex = hex.replaceAll("e", "1110");
				hex = hex.replaceAll("F", "1111");
				hex = hex.replaceAll("f", "1111");
				exchange.getIn().setBody(hex);
			}
		};
	}

	public static Processor binToHex() {
		return new Processor() {

			public void process(Exchange exchange) throws Exception {
				String hex = exchange.getIn().getBody(String.class);
				String bin = "";
				String binTmp;
				for (int i = 0; i < hex.length(); i += 4) {
					binTmp = hex.substring(i, i + 4);
					binTmp = binTmp.replaceAll("0000", "0");
					binTmp = binTmp.replaceAll("0001", "1");
					binTmp = binTmp.replaceAll("0010", "2");
					binTmp = binTmp.replaceAll("0011", "3");
					binTmp = binTmp.replaceAll("0100", "4");
					binTmp = binTmp.replaceAll("0101", "5");
					binTmp = binTmp.replaceAll("0110", "6");
					binTmp = binTmp.replaceAll("0111", "7");
					binTmp = binTmp.replaceAll("1000", "8");
					binTmp = binTmp.replaceAll("1001", "9");
					binTmp = binTmp.replaceAll("1010", "A");
					binTmp = binTmp.replaceAll("1011", "B");
					binTmp = binTmp.replaceAll("1100", "C");
					binTmp = binTmp.replaceAll("1101", "D");
					binTmp = binTmp.replaceAll("1110", "E");
					binTmp = binTmp.replaceAll("1111", "F");
					bin = bin.concat(binTmp);
				}
				exchange.getIn().setBody(bin);
			}
		};
	}

	public static Processor convertBinToDezimalAndSetHeader(final int start, final int end, final String headerName) {
		return new Processor() {
			public void process(Exchange exchange) throws Exception {
				String dfBits = exchange.getIn().getBody(String.class);
				dfBits = dfBits.substring(start, end);
				int dfDez = 0;
				int runVar = 1;
				for (int i = dfBits.length() - 1; i >= 0; --i) {
					dfDez += (int) (dfBits.charAt(i) - 48) * runVar;
					runVar *= 2;
				}
				exchange.getIn().setHeader(headerName, Integer.toString(dfDez));
			}
		};
	}

	public static Processor trimToICAO() {
		return new Processor() {
			public void process(Exchange exchange) throws Exception {
				String body = exchange.getIn().getBody().toString();
				body = body.substring(2, 9);
				exchange.getIn().setBody(body);
			}
		};
	}
}
