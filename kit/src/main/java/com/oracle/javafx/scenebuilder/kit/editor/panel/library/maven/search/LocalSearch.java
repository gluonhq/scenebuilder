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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.aether.artifact.DefaultArtifact;

public class LocalSearch implements Search {
            
    private final String m2;
        
    public LocalSearch(String userM2Repostory) {
        m2 = userM2Repostory + File.separator;
    }
    
    @Override
    public List<DefaultArtifact> getCoordinates(String query) {
        
        final Map<String, String> map = new HashMap<>();
        map.put("Repository", MavenPresets.LOCAL);
        
        try {
            return Files
                    .find(Paths.get(m2), 999, (p, bfa) -> bfa.isRegularFile())
                    .map(p -> p.toAbsolutePath().toString())
                    .filter(s -> s.endsWith(".jar"))
                    .map(s -> {
                        String d[] = s.substring(m2.length()).split("\\" + File.separator);
                        int length = d.length;
                        if (length > 3) {
                            String a = d[length - 3];
                            String g = Stream.of(d)
                                    .limit(length - 3)
                                    .collect(Collectors.joining("."));
                            return g + ":" + a + ":" + MIN_VERSION;
                        }
                        return null;
                    })
                    .filter(gav -> gav != null && gav.contains(query))
                    .distinct()
                    .map(gav -> new DefaultArtifact(gav, map))
                    .collect(Collectors.toList());
        } catch (IOException ex) { }
        return null;
    }
    
}
