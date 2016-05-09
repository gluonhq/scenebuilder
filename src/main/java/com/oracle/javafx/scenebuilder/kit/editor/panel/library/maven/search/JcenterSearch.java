package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset.MavenPresets;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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

public class JcenterSearch implements Search {

    // bintray
    
    // This requires authentication:
//    private static final String URL_PREFIX = "https://api.bintray.com/search/packages?name=";
    // This doesn't require authentication, but it is very limited in terms of number of results:
    private static final String URL_PREFIX = "https://api.bintray.com/search/file?name=*";
    private static final String URL_SUFFIX = "*";
    
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
        final Map<String, String> map = new HashMap<>();
        map.put("Repository", MavenPresets.JCENTER);
                        
        try {
            HttpGet request = new HttpGet(URL_PREFIX + query + URL_SUFFIX);
            if (!username.isEmpty() && !password.isEmpty()) {
                String authStringEnc = new String(encodeBase64((username + ":" + password).getBytes()));
                request.addHeader("Authorization", "Basic " + authStringEnc);
            }
            request.setHeader("Accept", "application/json");
            HttpResponse response = client.execute(request);
            try (JsonReader rdr = Json.createReader(response.getEntity().getContent())) {
                JsonArray obj = rdr.readArray();
                if (obj != null && !obj.isEmpty()) {
                    return obj.getValuesAs(JsonObject.class)
                            .stream()
                            .map(o -> o.getString("path"))
                            .filter(Objects::nonNull)
                            .filter(s -> s.endsWith(".jar"))
                            .map(s -> {
                                String d[] = s.split("\\/");
                                int length = d.length;
                                if (length > 3) {
                                    String v = d[length - 2];
                                    String a = d[length - 3];
                                    String g = Stream.of(d)
                                            .limit(length - 3)
                                            .collect(Collectors.joining("."));
                                    return g + ":" + a + ":" + v;
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .distinct()
                            .map(gav -> new DefaultArtifact(gav, map))
                            .collect(Collectors.groupingBy(a -> a.getGroupId() + ":" + a.getArtifactId()));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JcenterSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
