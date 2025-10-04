# SecApp

Progetto di laboratorio per il corso di Sicurezza nelle Applicazioni.  
L’obiettivo è sviluppare una piccola webapp sicura con Java 8 e Tomcat 9.

---

## Come avviare l’ambiente

### Tomcat 9
Scaricato e scompattato in `~/servers/tomcat9`.

```bash
~/servers/tomcat9/bin/startup.sh   # avvio
~/servers/tomcat9/bin/shutdown.sh  # stop
```

Dopo l’avvio aprire [http://localhost:8080](http://localhost:8080) per vedere la pagina di Tomcat.

### MySQL 8 (via Docker)
```bash
docker run -d --name mysql8 -p 3306:3306   -e MYSQL_ROOT_PASSWORD=RootPass!   -e MYSQL_DATABASE=secapp   -e MYSQL_USER=secapp_user   -e MYSQL_PASSWORD=UserPass!   mysql:8
```

Connessione test:
```bash
mysql -h 127.0.0.1 -P 3306 -u secapp_user -p secapp
```

### IntelliJ IDEA
- SDK: Java 8
- Run → Edit Configurations → Tomcat Local
- Deployment: aggiungere `secapp:war exploded`
- Application context: `/secapp`

URL di test:
- [http://localhost:8080/secapp/](http://localhost:8080/secapp/) → index.jsp
- [http://localhost:8080/secapp/hello](http://localhost:8080/secapp/hello) → servlet di prova

---

## Struttura
```
src/main/java/...      # servlet e DAO
src/main/webapp/       # jsp e risorse statiche
src/main/webapp/WEB-INF/web.xml
pom.xml
```

---

## Roadmap
- Sprint 1: registrazione, login, upload base
- Sprint 2: gestione sicura cookie e sessioni, validazione file
- Sprint 3: hash sicuri per password, SQL injection, HTTPS
- Sprint 4: test di uso/abuso e documentazione