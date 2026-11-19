// [AI-GENERATED WEB GUI] Ez a fájl a grafikus megjelenítési réteg része.
package org.example.webui;

import java.awt.Desktop;
import java.net.URI;

public class WebMain {
    private static final int PORT = 8080;
    private static final String URL = "http://localhost:" + PORT;

    public static void main(String[] args) {
        WebServer server = new WebServer(PORT);
        server.start();

        System.out.println("==========================================");
        System.out.println("  MUTYI TYCOON - Web GUI elindult!");
        System.out.println("  Cím: " + URL);
        System.out.println("  Leállítás: Ctrl+C / Stop gomb az IDE-ben");
        System.out.println("==========================================");

        openBrowserSafely(URL);
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
