package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    private static final String URL_PREFIX = "/service/local/data_index?q=";
    private static final String URL_PREFIX_CLASS = "http://nexus.gluonhq.com/nexus/service/local/data_index?cn=";
    private static final String URL_SUFFIX = "&from=";
    
    private final HttpClient client;
    private final String name;
    private final String domain;
    private final String username;
    private final String password;
            
    private static boolean first;
    private static int iteration;
    private int totalCount = 0;
    private static final int ITEMS_ITERATION = 200;
    private static final int MAX_RESULTS = 2000;
    
    public NexusSearch(String name, String domain, String username, String password) {
        client = HttpClients.createDefault();
        this.name = name;
        this.domain = domain;
        this.username = username;
        this.password = password;
        
        iteration = 0;
        first = true;
    }
    
    @Override
    public Map<String, List<DefaultArtifact>> getCoordinates(String query) {
        try {
            HttpGet request = new HttpGet(domain + URL_PREFIX + query + (first ? "" : URL_SUFFIX + iteration * ITEMS_ITERATION));
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
                    totalCount = Math.min(obj.getInt("totalCount", 0), MAX_RESULTS);
                    if (totalCount > ITEMS_ITERATION) {
                        Map<String, List<DefaultArtifact>> coordinates = new HashMap<>(processRequest(obj));
                        while (totalCount > ITEMS_ITERATION) {
                            iteration += 1;
                            getCoordinates(query).forEach((s, l) -> {
                                if (coordinates.get(s) != null) {
                                    List<DefaultArtifact> get = coordinates.get(s);
                                    coordinates.put(s, 
                                            Stream.concat(l.stream(), get.stream())
                                                    .distinct()
                                                    .collect(Collectors.toList()));
                                } else {
                                    coordinates.put(s, l);
                                }
                            });
                            
                            totalCount -= ITEMS_ITERATION;
                        }
                        return coordinates;
                    }
                }
                return processRequest(obj);
            }
        } catch (IOException ex) {
            Logger.getLogger(NexusSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private Map<String, List<DefaultArtifact>> processRequest(JsonObject obj) {
        if (obj != null && !obj.isEmpty() && obj.containsKey("data")) {
            JsonArray docResults = obj.getJsonArray("data");
            return docResults.getValuesAs(JsonObject.class)
                    .stream()
                    .map(doc -> {
                        final Map<String, String> map = new HashMap<>();
                        map.put("Repository", name + " (" + doc.getString("repoId", "") + ")");
                        return new DefaultArtifact(doc.getString("groupId", "") + ":" + 
                                doc.getString("artifactId", "") + ":" + doc.getString("version", ""), map);
                    })
                    .distinct()
                    .collect(Collectors.groupingBy(a -> a.getGroupId() + ":" + a.getArtifactId()));
        }
        return null;
    }
}
