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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.aether.artifact.DefaultArtifact;

public class SonatypeSearch implements Search {

    // sonatype
    private static final String URL_PREFIX = "http://oss.sonatype.org/service/local/data_index?q=";
    private static final String URL_SUFFIX = "";
    
    private final HttpClient client;
            
    public SonatypeSearch() {
        client = HttpClients.createDefault();
    }
    
    @Override
    public Map<String, List<DefaultArtifact>> getCoordinates(String query) {
        try {
            HttpGet request = new HttpGet(URL_PREFIX + query + URL_SUFFIX);
            request.setHeader("Accept", "application/json");
            HttpResponse response = client.execute(request);
            try (JsonReader rdr = Json.createReader(response.getEntity().getContent())) {
                JsonObject obj = rdr.readObject();
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
            Logger.getLogger(SonatypeSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
