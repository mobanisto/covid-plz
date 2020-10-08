# Anleitung zum Projekt

Nach dem unten beschriebenen Verfahren können Sie selber eine
eigene Instanz des Dienstes aufsetzen und betreiben.

## Aufsetzen einer Instanz

Das Projekt wird mit Hilfe des Build-Tools Gradle zusammengebaut. Verwenden Sie
das Kommando

    ./gradlew war

um ein WAR-Archiv zu erzeugen. Dieses befindet sich nach Abschluss des Vorgangs
hier:

    web/build/libs/covid-plz-web-0.0.1-production.war

Dieses können Sie auf einem beliebigen Servlet-Container, wie bspw. Tomcat oder
Jetty ausliefern. Nähere Informationen entnehmen Sie den
Installationsanweisungen des jeweiligen Servlet-Containers.

## Weiterentwicklung und Wartung

Zur Entwicklung empfehlen wir Eclipse mit Web Tools Platform. Zum Importieren
des Projekts in Eclipse führen Sie zunächst folgendes Kommando aus:

    ./gradlew cleanEclipse eclipse

Im Anschluss fügen Sie das Git-Repository zu ihrem Eclipse-Workspace hinzu,
und importieren von dort aus das Projekt mit seinen Untermodulen. Nachdem
Sie einen Servlet-Container wie Tomcat installiert haben, können Sie die
Webanwendung wie gewohnt von Eclipse aus starten und mit der Weiterentwicklung
beginnen.