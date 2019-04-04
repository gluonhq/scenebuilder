package com.oracle.javafx.scenebuilder.kit.editor.panel.library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class LibraryUtil {
	
	public static final String FOLDERS_LIBRARY_FILENAME = "library.folders"; //NOI18N

	public static boolean isJarPath(Path path) {
        final String pathString = path.toString().toLowerCase(Locale.ROOT);
        return pathString.endsWith(".jar"); //NOI18N
    }

	public static boolean isFxmlPath(Path path) {
        final String pathString = path.toString().toLowerCase(Locale.ROOT);
        return pathString.endsWith(".fxml"); //NOI18N
    }
    
	public static boolean isFolderMarkerPath(Path path) {
    	final String pathString = path.toString().toLowerCase(Locale.ROOT);
    	return pathString.endsWith(".folders"); //NOI18N
    }
	
	public static List<Path> getFolderPaths(Path libraryFile) throws FileNotFoundException, IOException {
		return Files.readAllLines(libraryFile).stream()
				.map(line -> {
					File f = new File(line);
					if (f.exists() && f.isDirectory())
						return f.toPath();
					else
						return null;
				})
				.filter(p -> p != null)
				.collect(Collectors.toList());
	}
}
