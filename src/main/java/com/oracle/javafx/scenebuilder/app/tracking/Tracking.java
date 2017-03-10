package com.oracle.javafx.scenebuilder.app.tracking;

import com.oracle.javafx.scenebuilder.app.SceneBuilderApp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Tracking {
    public static final String SCENEBUILDER_TYPE = "scenebuilder";
    public static final String SCENEBUILDER_USAGE_TYPE = "scenebuilder-usage";

    public static void sendTrackingInfo(String type, String hash, String email, boolean optIn, boolean update) {
        new Thread(() -> {
            try {
                String java = System.getProperty("java.version");
                String os = System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version");
                String urlParameters = "email=" + (email == null ? "" : URLEncoder.encode(email, "UTF-8"))
                        + "&subscribe=" + optIn
                        + "&os=" + URLEncoder.encode(os, "UTF-8")
                        + "&java=" + URLEncoder.encode(java, "UTF-8")
                        + "&type=" + type
                        + "&id=" + hash
                        + "&version=" + SceneBuilderApp.VERSION
                        + (update ? "&update=true" : "");

                URL url = new URL("http://usage.gluonhq.com/ul/log?" + urlParameters);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setUseCaches(false);
                conn.connect();
                try (DataInputStream in = new DataInputStream(conn.getInputStream())) {
                    while (in.read() > -1) {
                    }
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }, "UserRegistrationThread").start();
    }
}
