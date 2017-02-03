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

import java.io.IOException;
import java.util.ArrayList;
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
    public List<DefaultArtifact> getCoordinates(String query) {
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
                        List<DefaultArtifact> coordinates = new ArrayList<>(processRequest(obj));
                        while (totalCount > ITEMS_ITERATION) {
                            iteration += 1;
                            coordinates.addAll(getCoordinates(query)
                                    .stream()
                                    .filter(ga -> coordinates.stream()
                                            .noneMatch(ar -> ar.getGroupId().equals(ga.getGroupId()) &&
                                                             ar.getArtifactId().equals(ga.getArtifactId())))
                                    .collect(Collectors.toList()));
                            
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
    
    private List<DefaultArtifact> processRequest(JsonObject obj) {
        if (obj != null && !obj.isEmpty() && obj.containsKey("data")) {
            JsonArray docResults = obj.getJsonArray("data");
            return docResults.getValuesAs(JsonObject.class)
                    .stream()
                    .map(doc -> {
                        final Map<String, String> map = new HashMap<>();
                        map.put("Repository", name + " (" + doc.getString("repoId", "") + ")");
                        return new DefaultArtifact(doc.getString("groupId", "") + ":" + 
                                doc.getString("artifactId", "") + ":" + MIN_VERSION, map);
                    })
                    .distinct()
                    .collect(Collectors.toList());
        }
        return null;
    }
}
