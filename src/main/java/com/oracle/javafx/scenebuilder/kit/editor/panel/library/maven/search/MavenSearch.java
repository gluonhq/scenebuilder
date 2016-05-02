package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import java.io.IOException;
import java.util.List;
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

public class MavenSearch implements Search {

    // maven
    private static final String URL_PREFIX = "http://search.maven.org/solrsearch/select?q=";
    private static final String URL_SUFFIX = "&rows=50&wt=json";
    
    private static final String URL_PREFIX_FULLCLASS = "http://search.maven.org/solrsearch/select?q=fc:%22";
    private static final String URL_SUFFIX_FULLCLASS = "%22&rows=50&wt=json";
    
    private final HttpClient client;
            
    public MavenSearch() {
        client = HttpClients.createDefault();
    }
    
    @Override
    public List<String> getCoordinates(String query) {
        try {
            HttpGet request = new HttpGet(URL_PREFIX + query + URL_SUFFIX);
            HttpResponse response = client.execute(request);
            try (JsonReader rdr = Json.createReader(response.getEntity().getContent())) {
                JsonObject obj = rdr.readObject();
                if (obj != null && !obj.isEmpty() && obj.containsKey("response")) {
                    JsonObject jsonResponse = obj.getJsonObject("response");
                    if (jsonResponse != null && !jsonResponse.isEmpty() && jsonResponse.containsKey("docs")) {
                        JsonArray docResults = jsonResponse.getJsonArray("docs");
                        return docResults.getValuesAs(JsonObject.class)
                                .stream()
                                .map(doc -> doc.getString("id", "")) // "latestVersion"
                                .distinct()
                                .collect(Collectors.toList());
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MavenSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
