package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import com.oracle.javafx.scenebuilder.app.AppPlatform;
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
        
    public LocalSearch() {
        m2 = AppPlatform.getUserM2Repository() + File.separator;
    }
    
    @Override
    public Map<String, List<DefaultArtifact>> getCoordinates(String query) {
        
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
                            String v = d[length - 2];
                            String a = d[length - 3];
                            String g = Stream.of(d)
                                    .limit(length - 3)
                                    .collect(Collectors.joining("."));
                            return g + ":" + a + ":" + v;
                        }
                        return null;
                    })
                    .filter(gav -> gav != null && gav.contains(query))
                    .distinct()
                    .map(gav -> new DefaultArtifact(gav, map))
                    .collect(Collectors.groupingBy(a -> a.getGroupId() + ":" + a.getArtifactId()));
        } catch (IOException ex) { }
        return null;
    }
    
}
