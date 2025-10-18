# Sicurezza nelle Applicazioni — SecApp

Applicazione Java (Servlet/JSP) con MySQL che implementa: registrazione con foto profilo, login sicuro (BCrypt), sessioni hardenizzate, “ricordami” con selector/validator, proposte condivise con allegato `.txt` (visualizzazione inline), filtro di sicurezza (headers, CSP), e UI stile Facebook (header blu).

---

## Requisiti

- **Java**: 17 (JDK 17)
- **Maven**: 3.9+  
- **Docker Engine**: 24+  
- **Docker Compose v2**: 2.20+  
- **MySQL**: fornito da Docker (immagine `mysql:8.0`)  
- **Tomcat**: locale (IntelliJ) oppure Docker (`tomcat:9.0-jdk17`)

Verifica versioni:
```bash
java -version
mvn -v
docker --version
docker compose version
```

---

## Struttura repository (essenziale)

```
.
├─ docker-compose.yml
├─ db/
│  └─ init/
│     └─ 01_secapp_init.sql        # init DB (crea DB/utente/tabelle)
├─ src/
│  ├─ main/java/it/uniba/secapp/... # servlet, filter, dao, model
│  └─ main/webapp/                  # JSP, assets, WEB-INF, jspf
└─ pom.xml
```

---

## Configurazione DB (JDBC)

**Se usi Tomcat in Docker (consigliato per replicabilità)**, nel tuo `db.properties` imposta:

```
jdbc.url=jdbc:mysql://mysql-secapp:3306/secapp?useSSL=false&serverTimezone=UTC
jdbc.user=secapp_user
jdbc.password=secapp_pass
```

**Se usi Tomcat locale (IntelliJ)** con MySQL nel Compose in ascolto su `3307`:

```
jdbc.url=jdbc:mysql://127.0.0.1:3307/secapp?useSSL=false&serverTimezone=UTC
jdbc.user=secapp_user
jdbc.password=secapp_pass
```

> Lo script `db/init/01_secapp_init.sql` crea DB, utente e tabelle alla **prima** accensione del container MySQL.

---

## Avvio con Docker Compose (MySQL + Tomcat)

1) **Build .war**
```bash
mvn clean package
```
> Alla fine devi avere `target/secapp.war`.

2) **Start stack**
```bash
docker compose up -d
```

3) **Apri l’app**
```
http://localhost:8080/secapp
```

4) **Verifica DB (opzionale)**
```bash
docker exec -it mysql-secapp mysql -usecapp_user -psecapp_pass -e "USE secapp; SHOW TABLES;"
```

5) **Aggiorna dopo modifiche**
```bash
mvn clean package
docker compose restart tomcat-secapp
```

---

## Solo Database con Docker (Tomcat locale)

Se preferisci sviluppare con l’integrazione Tomcat di IntelliJ, lancia solo MySQL:

```bash
docker compose up -d mysql-secapp
```

Poi usa la **config JDBC locale** (vedi sopra, porta 3307).  
Avvia Tomcat da IntelliJ con context `/secapp`.

---

## HTTPS (opzionale per locale)

Per sviluppo puoi restare in HTTP su `8080`.  
Se vuoi HTTPS su Tomcat **locale**:

```bash
keytool -genkeypair -alias secapp -keyalg RSA -keysize 2048 -validity 365   -keystore ~/secapp-keystore.jks -storepass changeit   -dname "CN=localhost, OU=Dev, O=SecApp, L=Bari, ST=BA, C=IT"
```

Aggiungi in `server.xml` il connector 8443 e usa `https://localhost:8443/secapp`.  
(I cookie `Secure` richiedono HTTPS.)

---

## URL principali

- Home: `http://localhost:8080/secapp/`
- Login: `/login.jsp`
- Registrazione: `/register.jsp`
- Lista proposte: `/proposals`
- Nuova proposta: `/proposal_new.jsp`

---

## Dati persistenti

- **MySQL**: volume Docker `mysql_data_secapp`
- **File caricati**: salvati su filesystem del server (impostazione `upload.dir` o path predefinito in tmp); in Docker Tomcat, questi vivono nel container — se vuoi persistenza anche per gli upload, monta una volume/host path.

Esempio `web.xml` (init param opzionale):
```xml
<context-param>
  <param-name>upload.dir</param-name>
  <param-value>/usr/local/tomcat/uploads</param-value>
</context-param>
```
E in `docker-compose.yml` aggiungi un volume al servizio tomcat:
```yaml
volumes:
  - ./uploads:/usr/local/tomcat/uploads
```

---

## Troubleshooting

**La pagina JSP stampa letteralmente il contenuto di `header.jspf`**  
Usa include **statico**:
```jsp
<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
```

**Errore JSTL “Illegal text inside c:choose”**  
Dentro `<c:choose>` non usare commenti HTML. Usa commenti JSP: `<%-- ... --%>`.

**ClassNotFoundException `org.apache.jsp.index_jsp`**  
Cache JSP rovinata. Stop Tomcat, cancella `work/` e `temp/` dell’istanza, redeploy.

**`Column 'file_path' not found` o `Field 'description' doesn't have a default value`**  
Allinea lo schema con lo script `01_secapp_init.sql` e ricrea/aggiorna le tabelle:
```bash
docker exec -it mysql-secapp mysql -usecapp_user -psecapp_pass -e "USE secapp; DESCRIBE proposals;"
```

**Conflitto porte 3307/8080**  
Cambia le porte in `docker-compose.yml` o libera le porte occupate.

**L’app in Docker non raggiunge MySQL**  
Controlla che l’URL JDBC usi l’host `mysql-secapp` (nome del servizio), non `localhost`.

---

## Reset completo ambiente

⚠️ Cancella anche i dati del DB (volume):

```bash
docker compose down -v
mvn clean package
docker compose up -d
```

---

## Note di sicurezza implementate

- Hash password con **BCrypt**.  
- Session fixation protection (`changeSessionId`) e timeout sessione 15’.  
- Remember-me con **selector/validator** e rotazione token; cookie `Secure`, `HttpOnly`, `SameSite=Strict`.  
- Validazione file con **Apache Tika** (immagini profilo `.png/.jpg` e proposte `.txt`).  
- Escaping sistematico in JSP (`<c:out>`) + CSP e header di sicurezza in filtro.  
- PreparedStatement su tutte le query.

---

## Build info

- `pom.xml` definisce Servlet API, JSTL, BCrypt (`org.mindrot:jbcrypt`), Apache Tika, driver MySQL (`mysql:mysql-connector-j`), e target `war`.  
- Compila con:
```bash
mvn clean package
```
