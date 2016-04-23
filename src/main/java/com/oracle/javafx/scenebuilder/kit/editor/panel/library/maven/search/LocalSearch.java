package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import com.oracle.javafx.scenebuilder.app.AppPlatform;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalSearch implements Search {
            
    private final String m2;
        
    public LocalSearch() {
        m2 = AppPlatform.getUserM2Repository() + File.separator;
    }
    
    @Override
    public List<String> getCoordinates(String query) {
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
                            return g + ":" + a;
                        }
                        return null;
                    })
                    .filter(ga -> ga != null && ga.contains(query))
                    .distinct()
                    .collect(Collectors.toList());
        } catch (IOException ex) { }
        return null;
    }
    
}
