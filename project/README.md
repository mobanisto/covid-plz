# Anleitung zum Projekt

Nach dem unten beschriebenen Verfahren können Sie selber eine
eigene Instanz des Dienstes aufsetzen und betreiben.

## Aufsetzen einer Instanz

Zunächst müssen Sie eine Konfiguration vornehmen. In der Datei
`web/config/production/config.properties`
müssen Sie das Verzeichnis konfigurieren, in das die Daten des RKI
heruntergeladen werden, z.B:

    data=/tmp/covid-plz

Das Projekt wird mit Hilfe des Build-Tools Gradle zusammengebaut. Verwenden Sie
das Kommando

    $ ./gradlew war

um ein WAR-Archiv zu erzeugen. Dieses befindet sich nach Abschluss des Vorgangs
hier:

    web/build/libs/covid-plz-web-0.0.1-production.war

Dieses können Sie auf einem beliebigen Servlet-Container, wie bspw. Tomcat oder
Jetty ausliefern. Nähere Informationen entnehmen Sie den
Installationsanweisungen des jeweiligen Servlet-Containers.

Zu Testzwecken können Sie z.B. den Jetty-Runner verwenden:

    $ wget https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-runner/9.4.32.v20200930/jetty-runner-9.4.32.v20200930.jar

Dann starten Sie die Anwendung folgendermaßen:

    $ java -jar jetty-runner-9.4.32.v20200930.jar --port 9090 web/build/libs/covid-plz-web-0.0.1-production.war

Im Anschluss können Sie in Ihrem Browser die URL `http://localhost:9090`
aufrufen um die Anwendung zu benutzen.

## Weiterentwicklung und Wartung

Zur Entwicklung empfehlen wir Eclipse mit Web Tools Platform. Zum Importieren
des Projekts in Eclipse führen Sie zunächst folgendes Kommando aus:

    $ ./gradlew cleanEclipse eclipse

Im Anschluss fügen Sie das Git-Repository zu ihrem Eclipse-Workspace hinzu,
und importieren von dort aus das Projekt mit seinen Untermodulen. Nachdem
Sie einen Servlet-Container wie Tomcat installiert und in Eclipse hinzugefügt haben,
können Sie die Webanwendung wie gewohnt von Eclipse aus starten und mit der
Weiterentwicklung beginnen.

Wie im Produktivmodus müssen Sie auch für den Entwicklungsmodus eine
Konfiguration vornehmen. Die Konfiguration für den Entwicklungsmodus befindet
sich in der Datei `web/config/devel/config.properties`.
