package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

public class JcenterSearch implements Search {

    // bintray
    private static final String URL_PREFIX = "https://api.bintray.com/search/packages?name=";
    private static final String URL_SUFFIX = "";
    
    private final HttpClient client;
    private final String username;
    private final String password;
            
    public JcenterSearch() {
        client = HttpClients.createDefault();
        // TODO: Retrieve user/password from Preferences
        username = "";
        password = "";
            
    }
    
    @Override
    public List<String> getCoordinates(String query) {
        if (username.isEmpty() || password.isEmpty()) {
            return null;
        } 
        
        try {
            HttpGet request = new HttpGet(URL_PREFIX + query + URL_SUFFIX);
            String authStringEnc = new String(encodeBase64((username + ":" + password).getBytes()));
            request.addHeader("Authorization", "Basic " + authStringEnc);
            request.setHeader("Accept", "application/json");
            HttpResponse response = client.execute(request);
            try (JsonReader rdr = Json.createReader(response.getEntity().getContent())) {
                JsonArray obj = rdr.readArray();
                if (obj != null && !obj.isEmpty()) {
                    return obj.getValuesAs(JsonObject.class)
                            .stream()
                            .map(o -> {
                                JsonArray ids = o.getJsonArray("system_ids");
                                if (ids != null && !ids.isEmpty()) {
                                    return ids.getJsonString(0).getString();
                                }   
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JcenterSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
