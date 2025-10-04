package it.uniba.secapp.util;

import org.apache.tika.Tika;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * TikaUtil: helper per validare i file caricati.
 * - detectMime: verifica il MIME reale (no basarsi solo sull'estensione)
 * - readTextSample: legge un piccolo sample testuale per controlli base (XSS, HTML)
 */
public final class TikaUtil {
    private static final Tika TIKA = new Tika();

    private TikaUtil() {}

    /** Rileva il MIME reale del contenuto */
    public static String detectMime(InputStream in) throws IOException {
        return TIKA.detect(in);
    }

    /**
     * Legge fino a maxBytes dal flusso e li ritorna come stringa UTF-8.
     * (Per analisi semplice del contenuto senza caricare file enormi in memoria.)
     */
    public static String readTextSample(InputStream in, int maxBytes) throws IOException {
        byte[] buf = new byte[maxBytes];
        int read = in.read(buf);   // legge fino a maxBytes, oppure -1 se EOF
        if (read == -1) {
            return "";
        }
        return new String(buf, 0, read, StandardCharsets.UTF_8);
    }


    /** Controllo molto semplice contro HTML/script in un testo (Sprint 2) */
    public static boolean looksMaliciousText(String text) {
        if (text == null) return false;
        String low = text.toLowerCase();
        return low.contains("<script")
                || low.contains("javascript:")
                || low.contains("onerror=")
                || low.contains("onload=")
                || low.contains("</html>")
                || low.contains("<iframe");
    }
}
