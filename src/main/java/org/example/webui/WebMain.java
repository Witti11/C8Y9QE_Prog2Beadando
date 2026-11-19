// [AI-GENERATED WEB GUI] Ez a fájl a grafikus megjelenítési réteg része.
package org.example.webui;

import io.javalin.util.JavalinBindException;

import java.awt.Desktop;
import java.net.URI;

public class WebMain {
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_FALLBACK_PORT = 8090;

    public static void main(String[] args) {
        int activePort = startWithPortFallback();
        String url = "http://localhost:" + activePort;

        System.out.println("==========================================");
        System.out.println("  MUTYI TYCOON - Web GUI elindult!");
        System.out.println("  Cím: " + url);
        System.out.println("  Leállítás: Ctrl+C / Stop gomb az IDE-ben");
        System.out.println("==========================================");

        openBrowserSafely(url);
    }

    private static int startWithPortFallback() {
        for (int port = DEFAULT_PORT; port <= MAX_FALLBACK_PORT; port++) {
            WebServer server = new WebServer(port);
            try {
                server.start();
                if (port != DEFAULT_PORT) {
                    System.out.println("Figyelem: A " + DEFAULT_PORT + "-as port foglalt volt, a szerver a " + port + "-es porton indult el.");
                }
                return port;
            } catch (JavalinBindException e) {
                if (port == MAX_FALLBACK_PORT) {
                    throw e;
                }
            }
        }
        throw new IllegalStateException("Nem sikerült szabad portot találni (" + DEFAULT_PORT + "-" + MAX_FALLBACK_PORT + ").");
    }

    private static void openBrowserSafely(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            System.out.println("A böngésző automatikus megnyitása nem sikerült. Nyisd meg kézzel: " + url);
        }
    }
}
