# Masterarbeit
 Anwendungs- und Datenintegration mit Apache Camel

Das Java Eclipse-Project nutzt Maven als Build-Management Tool. In der pom.xml Datei sind die verwendeten Packages zu finden. Alle Java Programme sind in src/main/java/integrationMA zu finden. In src/python ist das Python Progamm zur Berechnung der Flughöhe zu finden. Die Ordnerstruktur sollte so wie gegeben beibehalten werden.

**Schritt 1: Dateipfad anpassen und anschließend DatabaseIntegrator.java ausführen, abschließend das Programm terminieren**\
In setupLocalAsViewDatabase/ liegt eine setup.sql Datei. Diese wird von DatabaseIntegrator.java gelesen, um die lokalen Datenbanken zu erstellen und zu füllen. Das Datenbankmanagementsystem, mit dem sich DatabaseIntegrator.java verbindet, muss auf Port 3306 laufen. Die Datensätze zum Füllen der Datenbanken sind faaAircrafts.txt, openSkyAircrafts.csv und sunAircrafts.csv und sind in Datasets/ zu finden. Zusätzlichen werden die Local-As-View Views für die späteren Anfragen an die Datenbanken erstellt. Bis die Datensätze vollständig eingepfelgt sind, kann etwas Zeit vergehen. Sobald 13-Mal "done" in der Konsole ausgegeben wurde, ist die setup.sql abgeschlossen. **WICHTIG:** Bevor DatabaseIntegrator.java ausgeführt wird, muss der Dateipfad der setup.sql Datei an 3 Stellen angepasst werden: In Zeile 90 muss der Dateipfad der faaAircrafts.txt Datei angegeben werden; In Zeile 96 der Dateipfad der openSkyAircrafts.csv Datei; In Zeile 103 der Dateipfad der sunAircrafts.csv Datei.

**Schritt 2: ModeSIntegration.java ausführen, abschließend das Programm terminieren**\
ModeSIntegration.java liest Dateien aus /ModeSIntegration_in und integriert diese in eine gemeinsame CSV Datei. Apache Camel legt einen neuen Ordner /ModeSIntegration_out an, welchem die Integrated_ModeSData.csv hinterlegt wird. ModeSIntegration.java sollte, sobald "done" in der Konsole ausgegeben wird, manuell terminiert werden.

**Schritt 3: ActiveMQTransformer.java ausführen, abschließend das Programm terminieren**\
ActiveMQTransformer.java baut eine Verbindung zu einem ActiveMQ Message Broker auf. Der Active MQ Broker muss auf lokal und auf Standard-Port 61616 laufen. ActiveMQTransformer.java nimmt die Integrated_ModeSData.csv Datei aus /ModeSIntegration_out und sendet den Inhalt zeilenweise an ActiveMQ Queues. ActiveMQTransformer.java sollte, sobald "done" in der Konsole ausgegeben wird, manuell terminiert werden.

**Schritt 4: APIGateway.java ausführen**\
APIGateway.java muss 1-Mal ausgeführt werden und läuft so lange, bis das Prgramm beendet wird. APIGateway.java läuft lokal auf Port 9092. 

**Schritt 5: QueryAPI.java 2-Mal ausführen**\
Da APIGateway.java Loadbalancing für Anfragen an QueryAPI.java mit genau zwei Endpunkten betreibt, muss QueryAPI.java 2-Mal gestartet werden. QueryAPI.java läuft, bis das Programm beendet wird. QueryAPI.java läuft lokal auf Port 9093 bzw. 9094. In src/main/resources befinden sich Modell.sql, ModellBesitzer.sql und BesitzerRegAmRegBis.sql. Diese werden dynamisch von QueryAPI.java durchgeführt.

**Schritt 6: TwitterAPI.java 2-Mal ausführen**\
Da APIGateway.java Loadbalancing für Anfragen an TwitterAPI.java mit genau zwei Endpunkten betreibt, muss TwitterAPI.java 2-Mal gestartet werden. TwitterAPI.java läuft, bis das Programm beendet wird. QueryAPI.java läuft lokal auf Port 9095 bzw. 9096. In src/main/resources befindet sich die Datei myApplication.properties. In dieser sind die Daten wie twitter.user oder twitter.consumerKey hinterlegt. Diese können bei Bedarf mit eignen Daten ersetzt werden. Bleiben diese unverändert sind die Ausgaben der TwitterAPI.java auf https://twitter.com/AttiLa56720675 zu finden.

**Schritt 7: FlightHeight.py ausführen**\
FlightHeight.py muss 1-Mal gestartet werden. Es findet kein Loadbalancing statt. FlightHeight.py läuft, bis es beendet wird. FlightHeight.py läugt auf Port 9099 

**Schritt 8: QueryFromActiveMQ.java ausführen und abschließend terminieren, eventuell Tweets löschen**\
QueryFromActiveMQ.java liest Messages aus den ActiveMQ Queues, welche in Schritt 3 gefüllt wurden. Es werden sämtliche Messages in allen Queues gelesen und an APIGateway.java aus Schritt 4 geleitet. Die Ergebnisse der Anfragen an APIGateway.java werden in neue ActiveMQ Queues gesendet. Zum Teil werden einige auf Twitter gepostet (Siehe Schritt 6). **WICHTIG:** Wird QueryFromActiveMQ.java ein weiteres Mal ausgeführt, müssen vorher alle Tweets gelöscht werden, da es sonst zu Tweet-Duplikaten kommt und Twitter keine Tweet-Duplikate erlaubt. Alternativ kann in Zeile 30 und 31 der URL-Parameter twitter=true entfernt werden (also ("query=ModellBesitzer&icao=${body[0][0]}&FlightAlt=${body[0][2]}&twitter=true") zu ("query=ModellBesitzer&icao=${body[0][0]}&FlightAlt=${body[0][2]}") abwandeln). Dadurch werden keine Tweets erstellt und es kommt zu keinen Duplikaten.

**Schritt 9: Ergebnisse einsehen**\
In den ActiveMQ Queues noneTypeCodeResult, flightPosResult und noFlightPosResult können die Ergebnisse der Anfragen an APIGateway.java eingesehen werden.


**Bemerkung:**\
Da es sich bei den Flugfahrzeug- und ModeS-Signale-Ddatensätzen um Datein mit Datengrößen von teilweise mehreren hundert MB bzw. GB handelt, sind in diesem Repository stark reduzierte Datensätze hinterlegt. Bei Bedarf können die vollständigen Datensätze nachgereicht werden.\
Falls das Ablauf mehrmals getestet werden soll, muss **Schritt 1** übersprungen und direkt mit **Schritt 2** gestartet werden (außgenommen die Datenbank wird gelöscht, dann muss **Schritt 1** ebenfalls durchgeführt werden). Auch zu beachten ist der **WICHTIG** Hinweis in **Schritt 8** bezüglich der Tweet-Duplikate.
