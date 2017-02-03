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
    
    // This requires authentication:
//    private static final String URL_PREFIX = "https://api.bintray.com/search/packages?name=";
    // This doesn't require authentication, limited to 50 results:
    private static final String URL_PREFIX = "https://api.bintray.com/search/packages/maven?q=*";
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
    public List<DefaultArtifact> getCoordinates(String query) {
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
                            .map(o -> {
                                JsonArray ids = o.getJsonArray("system_ids");
                                if (ids != null && !ids.isEmpty()) {
                                    return ids.stream()
                                            .map(j -> j.toString().replaceAll("\"", "") + ":" + MIN_VERSION)
                                            .collect(Collectors.toList());
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .flatMap(l -> l.stream())
                            .distinct()
                            .map(gav -> new DefaultArtifact(gav, map))
                            .collect(Collectors.toList());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JcenterSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
