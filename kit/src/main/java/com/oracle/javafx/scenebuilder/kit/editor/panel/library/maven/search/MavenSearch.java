/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
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
import java.util.HashMap;
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

public class MavenSearch implements Search {

    // maven
    private static final String URL_PREFIX = "http://search.maven.org/solrsearch/select?q=";
    private static final String URL_SUFFIX = "&rows=200&wt=json";
    
    private static final String URL_PREFIX_FULLCLASS = "http://search.maven.org/solrsearch/select?q=fc:%22";
    private static final String URL_SUFFIX_FULLCLASS = "%22&rows=200&wt=json";
    
    private final HttpClient client;
            
    public MavenSearch() {
        client = HttpClients.createDefault();
    }
    
    @Override
    public List<DefaultArtifact> getCoordinates(String query) {
        
        final Map<String, String> map = new HashMap<>();
        map.put("Repository", MavenPresets.MAVEN);
    
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
                                .map(doc -> doc.getString("id", "") + ":" + MIN_VERSION)
                                .distinct()
                                .map(gav -> new DefaultArtifact(gav, map))
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
