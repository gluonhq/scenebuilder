package com.oracle.javafx.scenebuilder.app;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextInputDialog;

public class RegistrationHelper {

    public static final String EMAIL = "email";
    public static final String VERSION = "version";
    public static final String NOTSET = "notset";
    public static final String SETTINGS_FILE = ".scenebuilder";

    public static boolean isRegistered() {
        Properties settings = getSettings();
        String email = settings.getProperty(EMAIL, "");
        String version = settings.getProperty(VERSION, "");
        if (email.isEmpty()) {
            return false;
        }
        return !(email.equals(NOTSET) && !SceneBuilderApp.VERSION.equals(version));
    }

    private static Properties getSettings() {
        File file = new File(System.getProperty("user.home"), SETTINGS_FILE);
        Properties settings = new Properties();
        if (!file.exists()) {
            return settings;
        }
        try {
            settings.load(new FileReader(file));
        } catch (IOException ex) {
            Logger.getLogger(RegistrationHelper.class.getName()).log(Level.SEVERE, "could not load settings properties file", ex);
        }
        return settings;
    }

    private static void writeSettings(Properties settings) {
        File file = new File(System.getProperty("user.home"), SETTINGS_FILE);
        try {
            settings.store(new FileWriter(file), "");
        } catch (IOException ex) {
            Logger.getLogger(RegistrationHelper.class.getName()).log(Level.SEVERE, "could not store settings properties file", ex);
        }
    }

    public static void showUserRegistration() {
        TextInputDialog dialog = new TextInputDialog("Email");
        dialog.setTitle("Registration (optional)");
        dialog.setHeaderText("Optional User Registration");
        dialog.setContentText("Please enter your email address if you want to be kept up-to-date about new SceneBuilder versions.");
        Optional<String> result = dialog.showAndWait();
        String email;
        boolean subscribe = false;
        if (result.isPresent()) {
            email = result.get();
            subscribe = true;
        } else {
            email = NOTSET;
        }
        Properties settings = getSettings();
        settings.setProperty(EMAIL, email);
        settings.setProperty(VERSION, SceneBuilderApp.VERSION);
        writeSettings(settings);
        trackUserRegistration(email, subscribe);
    }
    
    public static String getUniqueId(){
        String uniqueId = "";
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            if (ni != null) {
                uniqueId = computeHash(ni.getHardwareAddress());
            } else {
            }
        } catch (UnknownHostException | SocketException e) {
        }
        if (uniqueId.isEmpty()) {
            uniqueId = UUID.randomUUID().toString();
        }
        return uniqueId;
    }
    
    private static String computeHash(byte[] buffer) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            
            messageDigest.reset();
            messageDigest.update(buffer);
            byte[] digest = messageDigest.digest();
    
            // Convert the byte to hex format
            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
            }
    
            return hexStr;
        } catch (NoSuchAlgorithmException e) {
        }
        
        return "";
    }    

    public static void trackUserRegistration(String email, Boolean keepUpToDate) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String id = getUniqueId();
                    String java = System.getProperty("java.version");
                    String os = System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version");
                    String urlParameters = "email=" + URLEncoder.encode(email, "UTF-8")
                            + "&subscribe=" + keepUpToDate
                            + "&os=" + URLEncoder.encode(os, "UTF-8")
                            + "&java=" + URLEncoder.encode(java, "UTF-8")
                            + "&type=scenebuilder"
                            + "&id=" + id
                            + "&version=" + SceneBuilderApp.VERSION;

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
                } catch (IOException ex) {
                }
            }
        }.start();
    }

}
