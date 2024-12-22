/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.preset.MavenPresets;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.eclipse.aether.artifact.DefaultArtifact;

public class MavenSearch implements Search {

    // maven
    private static final String URL_PREFIX = "https://search.maven.org/solrsearch/select?q=";
    private static final String URL_SUFFIX = "&rows=200&wt=json";
    
    private static final String URL_PREFIX_FULLCLASS = "https://search.maven.org/solrsearch/select?q=fc:%22";
    private static final String URL_SUFFIX_FULLCLASS = "%22&rows=200&wt=json";
    
    private final HttpClient client;
            
    public MavenSearch() {
        client = HttpClient.newHttpClient();
    }
    
    @Override
    public List<DefaultArtifact> getCoordinates(String query) {
        
        final Map<String, String> map = new HashMap<>();
        map.put("Repository", MavenPresets.MAVEN);
    
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_PREFIX + query + URL_SUFFIX))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            try (JsonReader rdr = Json.createReader(new StringReader(response.body()))) {
                JsonObject obj = rdr.readObject();
                if (obj != null && !obj.isEmpty() && obj.containsKey("response")) {
                    JsonObject jsonResponse = obj.getJsonObject("response");
                    if (jsonResponse != null && !jsonResponse.isEmpty() && jsonResponse.containsKey("docs")) {
                        JsonArray docResults = jsonResponse.getJsonArray("docs");
                        return docResults.getValuesAs(JsonObject.class)
                                .stream()
                                .map(doc -> doc.getString("id", "") + ":" + MIN_VERSION)
                                .distinct()
                                .map(gav -> new DefaultArtifact(gav, map))
                                .collect(Collectors.toList());
                    }
                }
            }
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(MavenSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
