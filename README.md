# DocumentGateway

B2B-Document-Gateway (MVP Phase 1): nimmt XML-Nachrichten entgegen, prüft Auth und XSD, routet über internes Mapping an einen Receiver-Mock und schreibt Audit-Events nach MariaDB.

Details zum API-Vertrag: [api-contract.md](api-contract.md)  
Architektur: [../docs/architecture.md](../docs/architecture.md)

## Voraussetzungen

- Java 21
- Maven 3.9+
- Docker + Docker Compose (für lokale MariaDB)

## Deployment(Docker)
### .env anlegen (DB_PASSWORD etc.)
docker compose up -d --build

### UI öffnen
http://localhost:8082

### Stoppen
docker compose down

### Stoppen inkl. DB-Daten löschen
docker compose down -v

## Schnellstart (lokal)

### 1. Datenbank starten

Im Modulordner `DocumentGateway` eine `.env` anlegen (nicht committen). Vorlage: [.env.example](.env.example)

```env
DB_NAME=bewerbungsprojekt
DB_USER=appuser
DB_PASSWORD=geheim
```

DB hochfahren:

```bash
docker compose up -d
```

- MariaDB: `localhost:3306`
- Adminer (DB-UI): http://localhost:8081  
  (System: MariaDB, Server: `mariadb`, User/Passwort wie in `.env`)

### 2. Umgebungsvariablen für die App

Die App braucht mindestens `DB_PASSWORD`. Optional mit Defaults:

| Variable | Default | Beschreibung |
|----------|---------|--------------|
| `DB_HOST` | `localhost` | MariaDB-Host |
| `DB_PORT` | `3306` | MariaDB-Port |
| `DB_NAME` | `bewerbungsprojekt` | Datenbankname |
| `DB_USER` | `appuser` | DB-User |
| `DB_PASSWORD` | — | **Pflicht**, kein Default in `application.properties` |

PowerShell (Beispiel):

```powershell
$env:DB_PASSWORD = "geheim"
```

Bash:

```bash
export DB_PASSWORD=geheim
```

### 3. Anwendung starten

```bash
mvn spring-boot:run
```

Standard-Port: **8080**

Beim ersten Start legt Hibernate Tabellen an (`spring.jpa.hibernate.ddl-auto=update`).

## Beispiel-Request

Partner und API-Key stehen in `application.properties` (Dev):

```properties
gateway.api-keys.partner-a=test-api-key
```

```bash
curl -i -X POST http://localhost:8080/api/v1/messages \
  -H "Content-Type: application/xml" \
  -H "X-Partner-Id: partner-a" \
  -H "X-Message-Type: invoice" \
  -H "X-Correlation-Id: 7b8f0d6f-8d90-4f7a-8bf3-3bc4d8b0f6d9" \
  -H "X-API-KEY: test-api-key" \
  -d '<message xmlns="http://documentgateway.de/invoice/v1"><id>1</id></message>'
```

Erwartung bei Erfolg: **202 Accepted** mit JSON (`correlationId`, `status`, `message`).

Weitere Statuscodes und Fehlerformat: [api-contract.md](api-contract.md).

## Tests

```bash
mvn test
```

Integrationstests nutzen eine In-Memory-H2 — **keine laufende MariaDB nötig**.

## CI

GitHub Actions (`.github/workflows/ci.yaml`): bei Push/PR auf `main`/`develop` → `mvn -B test` mit Java 21.

## Konfiguration (Auszug)

| Thema | Wo |
|-------|-----|
| Partner-API-Keys | `gateway.api-keys.<partner-id>=...` |
| Routing | `routing.routes[partner-a.invoice].receiver-id=...` |
| Max. Request-Body | `server.tomcat.max-http-form-post-size=1MB` |

Routing-Ziele kommen **nur** aus der Config (keine URL aus dem Client) — SSRF-Schutz.

## Security-Hinweise (Dev)

- API-Keys nicht in Git committen; für Prod über Env/Secrets setzen.
- Auth-Fehler → **401**; Keys werden zeitkonstant verglichen (`MessageDigest.isEqual`).
- Fehlerantworten ohne Stacktraces; Details siehe `GlobalExceptionHandler` und [api-contract.md](api-contract.md).
- `X-Correlation-Id` für Logs und Audit mitgeben (wird bei Auth-Fehlern ggf. generiert).

