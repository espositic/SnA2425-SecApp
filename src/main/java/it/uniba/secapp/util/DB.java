package it.uniba.secapp.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DB {
    private static Properties props;

    static {
        try {
            props = new Properties();
            try (InputStream is = DB.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (is == null) throw new RuntimeException("db.properties non trovato");
                props.load(is);
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Errore inizializzazione DB: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    props.getProperty("jdbc.url"),
                    props.getProperty("jdbc.user"),
                    props.getProperty("jdbc.password")
            );
        } catch (Exception e) {
            throw new RuntimeException("Connessione DB fallita: " + e.getMessage(), e);
        }
    }
}
