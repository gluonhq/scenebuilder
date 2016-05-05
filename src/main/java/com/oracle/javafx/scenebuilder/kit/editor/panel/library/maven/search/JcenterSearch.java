package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset.MavenPresets;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.aether.artifact.DefaultArtifact;

public class JcenterSearch implements Search {

    // bintray
    private static final String URL_PREFIX = "https://api.bintray.com/search/packages?name=";
    private static final String URL_SUFFIX = "";
    
    private final HttpClient client;
    private final String username;
    private final String password;
            
    public JcenterSearch(String username, String password) {
        client = HttpClients.createDefault();
        this.username = username;
        this.password = password;
    }
    
    @Override
    public Map<String, List<DefaultArtifact>> getCoordinates(String query) {
        if (username.isEmpty() || password.isEmpty()) {
            return null;
        } 
        
        final Map<String, String> map = new HashMap<>();
        map.put("Repository", MavenPresets.JCENTER);
                        
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
                                    return ids.stream()
                                            .map(ga -> new DefaultArtifact(ga.toString()
                                                    .replaceAll("\"","") + ":" + o.getString("latest_version",""), map))
                                            .collect(Collectors.toList());
                                }   
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .flatMap(l -> l.stream())
                            .distinct()
                            .collect(Collectors.groupingBy(a -> a.getGroupId() + ":" + a.getArtifactId()));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JcenterSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
