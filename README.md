Die Anwendung ist mit SpringBoot 3 und Web MVC + Spring Data JPA implementiert. 
Die API-Schnittstelle und die Modell-Beans werden aus der 
API-Definition mit openapi-generator-maven-plugin generiert. 
Die Packages sind nach der Schichten aufgebaut: Controller bzw. API, Service- und
Persistenz-Schicht. Zum Persistieren wird die in-memory Datenbank H2 benutzt.
Produktions-Features wie Monitoring Schnittstelle (Actuator), wurden nicht umgesetzt,
Logging ist auch nicht durchgehend. Um die Anwendung in einer IDE zu Starten muss sie zuerst
mit Maven gebaut werden, sonst fehlen die generierten Klassen.


Zum Bauen und Starten der Anwendung ist Java 17 erforderlich

* Bauen:
  Im root-Verzeichnis des Quellcodes ausführen:

      ./mvnw clean package
* Starten:

      java -jar target/pets-0.0.1-SNAPSHOT.jar
Die Anwendung läuft auf dem port 8080, der Port kann beim Starten geändert werden:

    java -jar -Dserver.port=8082 target/pets-0.0.1-SNAPSHOT.jar
 