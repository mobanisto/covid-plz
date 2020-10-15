# Ein Webservice über das COVID-19 Infektionsgeschehen in Deutschland

Dieses Projekt ist ein Webservice, der die Informationen des
Robert-Koch-Instituts zum COVID-19 Infektionsgeschehen auf Postleitzahlen in
Deutschland abbildet und darüber abrufbar macht.

Datenquelle sind die vom Robert-Koch-Institut auf der Nationalen Plattform
für geographische Daten (NPGEO-DE) täglich aktualisert zur Verfügung gestellten
Daten sowie die Postleitzahlregionen aus dem OpenStreetMap-Projekt.

Eine öffentlich erreichbare Instanz dieses Webservice ist unter
https://covid-plz-check.de erreichbar.

Sie können selbst eine Instanz dieses Webserivce betreiben. Nähere Informationen
dazu finden sie [hier](project/README.md).
Sie können und dürfen den Dienst auch anpassen oder erweitern.
Der dazu benötigte Quellcode wird Ihnen in diesem Repository unter der
[MIT Lizenz](MIT.md) zur Verfügung gestellt.

## Datenquellen

Zur Zeit benutzen wir [diese auf Landkreisebene aggregierten
Daten](https://npgeo-corona-npgeo-de.hub.arcgis.com/datasets/917fc37a709542548cc3be077a786c17_0),
noch besser wäre es, die
[Rohdaten](https://npgeo-corona-npgeo-de.hub.arcgis.com/datasets/dd4580c810204019a7b8eb3e0b329dd6_0)
zu verarbeiten.

Leider stellt das RKI keine historischen Daten zur Verfügung, sondern
überschreibt seinen eigenen Datensatz täglich. Zum Glück haben andere schon vor
uns angefangen die Daten des RKI zu archivieren um historische Auswertungen
möglich zu machen. Uns sind zwei Quellen bekannt, von der ARD und dem NDR:

* [ard-data/2020-rki-archive](https://github.com/ard-data/2020-rki-archive)
* [NorddeutscherRundfunk/corona_daten](https://github.com/NorddeutscherRundfunk/corona_daten)
