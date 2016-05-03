package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import org.eclipse.aether.artifact.DefaultArtifact;

public class NexusSearch implements Search {

    // nexus
    private static final String URL_PREFIX = "http://nexus.gluonhq.com/nexus/service/local/data_index?q=";
    private static final String URL_PREFIX_CLASS = "http://nexus.gluonhq.com/nexus/service/local/data_index?cn=";
    private static final String URL_SUFFIX = "&from=";
    
    private final HttpClient client;
    private final String username;
    private final String password;
            
    private static boolean first;
    private int totalCount = 0;
    private static final int MAX_ITEMS = 200;
    
    public NexusSearch() {
        client = HttpClients.createDefault();
        // TODO: Retrieve user/password from Preferences, and change the nexus repository URL
        username = "";
        password = "";
        
        first = true;
    }
    
    @Override
    public Map<String, List<DefaultArtifact>> getCoordinates(String query) {
        try {
            HttpGet request = new HttpGet(URL_PREFIX + query + (first ? "" : URL_SUFFIX + (totalCount - MAX_ITEMS)));
            if (!username.isEmpty() && !password.isEmpty()) {
                String authStringEnc = new String(encodeBase64((username + ":" + password).getBytes()));
                request.addHeader("Authorization", "Basic " + authStringEnc);
            }
            request.setHeader("Accept", "application/json");
            HttpResponse response = client.execute(request);
            try (JsonReader rdr = Json.createReader(response.getEntity().getContent())) {
                JsonObject obj = rdr.readObject();
                if (first && obj != null && !obj.isEmpty() && obj.containsKey("totalCount")) {
                    first = false;
                    totalCount = obj.getInt("totalCount", 0);
                    if (totalCount > MAX_ITEMS) {
                        return getCoordinates(query);
                    }
                }
                if (obj != null && !obj.isEmpty() && obj.containsKey("data")) {
                    JsonArray docResults = obj.getJsonArray("data");
                    return docResults.getValuesAs(JsonObject.class)
                            .stream()
                            .map(doc -> doc.getString("groupId", "") + ":" + doc.getString("artifactId", "") + ":" + doc.getString("version", ""))
                            .distinct()
                            .map(DefaultArtifact::new)
                            .collect(Collectors.groupingBy(a -> a.getGroupId() + ":" + a.getArtifactId()));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(NexusSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
