/*
 * Copyright (c) 2017, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package com.oracle.javafx.scenebuilder.kit.library.user;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.module.ModuleReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.kit.editor.images.ImageUtils;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryUtil;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.library.BuiltinLibrary;
import com.oracle.javafx.scenebuilder.kit.library.LibraryItem;
import com.oracle.javafx.scenebuilder.kit.library.util.FolderExplorer;
import com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer;
import com.oracle.javafx.scenebuilder.kit.library.util.JarReport;
import com.oracle.javafx.scenebuilder.kit.library.util.JarReportEntry;
import com.oracle.javafx.scenebuilder.kit.library.util.ModuleExplorer;

/**
 *
 * 
 */
class LibraryFolderWatcher implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(LibraryFolderWatcher.class.getSimpleName());

    private final UserLibrary library;

    private enum FILE_TYPE {FXML, JAR, FOLDER_MARKER}

    private static final List<String> JAVAFX_MODULES = Arrays.asList(
            "javafx-base", "javafx-graphics", "javafx-controls",
            "javafx-fxml", "javafx-media", "javafx-web", "javafx-swing");

    public LibraryFolderWatcher(UserLibrary library) {
        this.library = library;
    }

    /*
     * Runnable
     */
    
    @Override
    public void run() {
        
        try {
            library.updateExplorationCount(0);
            library.updateExplorationDate(new Date());
            runDiscovery();
            runWatching();
        } catch(InterruptedException x) {
            // Let's stop: Typically, when UserLibrary::stopWatching is invoked, an InterruptedException is triggered to stop this watch service
        }
    }
    
    
    /*
     * Private
     */
    private void runDiscovery() throws InterruptedException {
        // First put the builtin items in the library
        library.setItems(BuiltinLibrary.getLibrary().getItems());

        // Attempts to add the maven jars, including dependencies
        List<Path> additionalJars = library.getAdditionalJarPaths().get();
        
        final Set<Path> currentJarsOrFolders = new HashSet<>(additionalJars);
        final Set<Path> currentFxmls = new HashSet<>();
                
        // Now attempts to discover the user library folder
        final Path folder = Paths.get(library.getPath());
        if (folder != null && folder.toFile().exists()) {
            boolean retry;
            do {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
                    for (Path entry: stream) {
                        if (LibraryUtil.isJarPath(entry)) {
                            currentJarsOrFolders.add(entry);
                        } else if (LibraryUtil.isFxmlPath(entry)) {
                            currentFxmls.add(entry);
                        } else if (LibraryUtil.isFolderMarkerPath(entry)) {
                            // open folders marker file: every line should be a single folder entry
                            // we scan the file and add the path to currentJarsOrFolders
                            List<Path> folderPaths = LibraryUtil.getFolderPaths(entry);
                            for (Path f : folderPaths) {
                                currentJarsOrFolders.add(f);
                            }
                        }
                    }
                    retry = false;
                } catch(IOException x) {
                    Thread.sleep(2000 /* ms */);
                    retry = true;
                } finally {
                    library.updateExplorationCount(library.getExplorationCount()+1);
                }
            }
            while (retry && library.getExplorationCount() < 10);
        }
        try {
            library.setExploring(true);
            try {
                updateLibrary(currentFxmls);
                exploreAndUpdateLibrary(currentJarsOrFolders);
            }
            finally {
                library.setExploring(false);
            }
        } catch(IOException x) { }
    }
    
    private void runWatching() throws InterruptedException {
        WatchService watchService = null;
        try {
            while (true) {
                final Path folder = Paths.get(library.getPath());

                while (watchService == null) {
                    try {
                        watchService = folder.getFileSystem().newWatchService();
                    } catch(IOException x) {
                        System.out.println("FileSystem.newWatchService() failed"); //NOI18N
                        System.out.println("Sleeping..."); //NOI18N
                        Thread.sleep(1000 /* ms */);
                    }
                }

                WatchKey watchKey = null;
                while ((watchKey == null) || (watchKey.isValid() == false)) {
                    try {
                        watchKey = folder.register(watchService, 
                                StandardWatchEventKinds.ENTRY_CREATE, 
                                StandardWatchEventKinds.ENTRY_DELETE, 
                                StandardWatchEventKinds.ENTRY_MODIFY);

                        WatchKey wk;
                        do {
                            wk = watchService.take();
                            assert wk == watchKey;

                            boolean isDirty = false;
                            for (WatchEvent<?> e: wk.pollEvents()) {
                                final WatchEvent.Kind<?> kind = e.kind();
                                final Object context = e.context();

                                if (kind == StandardWatchEventKinds.ENTRY_CREATE
                                        || kind == StandardWatchEventKinds.ENTRY_DELETE
                                        || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                    assert context instanceof Path;
                                    if (LibraryUtil.isJarPath((Path) context)) {
                                        if (!hasJarBeenAdded((Path) context)) {
                                            isDirty = true;
                                        }
                                    } else if (LibraryUtil.isFxmlPath((Path)context)){
                                        isDirty = true;
                                    } else if (LibraryUtil.isFolderMarkerPath((Path)context)) {
                                        isDirty = true;
                                    }
                                } else {
                                    assert kind == StandardWatchEventKinds.OVERFLOW;
                                }
                            }

                            // We reconstruct a full set from scratch as soon as the
                            // dirty flag is set.
                            if (isDirty) {
                                // First put the builtin items in the library
                                library.setExploring(true);
                                try {
                                    library.setItems(BuiltinLibrary.getLibrary().getItems());

                                    // Now attempts to add the maven jars
                                    List<Path> currentMavenJars = library.getAdditionalJarPaths().get();

                                    final Set<Path> fxmls = new HashSet<>();
                                    fxmls.addAll(getAllFiles(FILE_TYPE.FXML));
                            	    updateLibrary(fxmls);

                                    final Set<Path> jarsAndFolders = new HashSet<>(currentMavenJars);
                                    jarsAndFolders.addAll(getAllFiles(FILE_TYPE.JAR));

                                    Set<Path> foldersMarkers = getAllFiles(FILE_TYPE.FOLDER_MARKER);
                                    for (Path path : foldersMarkers) {
                                        // open folders marker file: every line should be a single folder entry
                                        // we scan the file and add the path to currentJarsOrFolders
                                        List<Path> folderPaths = LibraryUtil.getFolderPaths(path);
                                        for (Path f : folderPaths) {
                                            jarsAndFolders.add(f);
                                        }
                                    }

                            	    exploreAndUpdateLibrary(jarsAndFolders);

                                    library.updateExplorationCount(library.getExplorationCount()+1);
                                }
                                finally {
                                    library.setExploring(false);
                                }
                            }
                        } while (wk.reset());
                    } catch(IOException x) {
                        Thread.sleep(1000 /* ms */);
                    }
                }

            }
        }
        finally {
        	// Typically, when UserLibrary::stopWatching is invoked, an InterruptedException is triggered to stop this watch service.
        	// The InterruptedException is handled outside; here we just make sure to close() the watcher service that polls the filesystem.
            if (watchService != null) {
                // we need to close the filesystem watcher here, otherwise it remains active and locking jars on library folder
                try {
                    watchService.close();
                } catch (IOException e) {
                	LOGGER.severe("Error closing FileSystemWatchService: " + e.getMessage());
                }
            }
        }
    }

    private Set<Path> getAllFiles(FILE_TYPE fileType) throws IOException {
        Set<Path> res = new HashSet<>();
        final Path folder = Paths.get(library.getPath());

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder)) {
            for (Path p : ds) {
                switch (fileType) {
                    case FXML:
                        if (LibraryUtil.isFxmlPath(p)) {
                            res.add(p);
                        }
                        break;
                    case JAR:
                        if (LibraryUtil.isJarPath(p)) {
                            res.add(p);
                        }
                        break;
                    case FOLDER_MARKER:
                        if (LibraryUtil.isFolderMarkerPath(p)) {
                            res.add(p);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return res;
    }
    
    
    private boolean hasJarBeenAdded(Path context) {
        boolean hasJarBeenAdded = false;
        for (JarReport report : library.getJarReports()) {
            if (report.getJar().getFileName().equals(context)) {
                hasJarBeenAdded = true;
                break;
            }
        }
        return hasJarBeenAdded;
    }


    private void updateLibrary(Collection<Path> paths) throws IOException {
        final List<LibraryItem> newItems = new ArrayList<>();
        
        for (Path path : paths) {
            newItems.add(makeLibraryItem(path));
        }

        library.addItems(newItems);
        library.updateFxmlFileReports(paths);
        library.updateExplorationDate(new Date());
    }
    
    
    private LibraryItem makeLibraryItem(Path path) throws IOException {
        final URL iconURL = ImageUtils.getNodeIconURL(null);
        String fileName = path.getFileName().toString();
        String itemName = fileName.substring(0, fileName.indexOf(".fxml")); //NOI18N
        String fxmlText = ""; //NOI18N
        StringBuilder buf = new StringBuilder();

        try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(path.toFile()), "UTF-8"))) { //NOI18N
            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line).append("\n"); //NOI18N
            }
            
            fxmlText = buf.toString();
        }

        final LibraryItem res = new LibraryItem(itemName, UserLibrary.TAG_USER_DEFINED, fxmlText, iconURL, library);
        return res;
    }
    
    
    private void exploreAndUpdateLibrary(Collection<Path> modulesOrJarsOrFolders) throws IOException {

        //  1) we create a classloader
        //  2) we explore all the modules, jars, and folders
        //  3) we construct a list of library items
        //  4) we update the user library with the class loader and items
        //  5) on startup only, we allow opening files that may/may not rely on the user library

        // 1)
        final ClassLoader classLoader;
        if (modulesOrJarsOrFolders.isEmpty()) {
            classLoader = null;
        } else {
            classLoader = new URLClassLoader(makeURLArrayFromPaths(modulesOrJarsOrFolders));
        }

        // 2)
        final List<JarReport> moduleOrJarOrFolderReports = new ArrayList<>();
        for (Path currentModuleOrJarOrFolder : modulesOrJarsOrFolders) {
            String jarName = currentModuleOrJarOrFolder.getName(currentModuleOrJarOrFolder.getNameCount() - 1).toString();
            if (JAVAFX_MODULES.stream().anyMatch(jarName::startsWith)) {
                continue;
            }

            JarReport jarReport;
            String resultText = "";
            Optional<ModuleReference> moduleReference = LibraryUtil.getModuleReference(currentModuleOrJarOrFolder);
            if (moduleReference.isPresent()) {
                LOGGER.info(I18N.getString("log.info.explore.module", moduleReference.get().descriptor()));
                final ModuleExplorer explorer = new ModuleExplorer(moduleReference.get());
                jarReport = explorer.explore();
                resultText = I18N.getString("log.info.explore.module.results", jarName);
            }
            else if (LibraryUtil.isJarPath(currentModuleOrJarOrFolder)) {
                LOGGER.info(I18N.getString("log.info.explore.jar", currentModuleOrJarOrFolder));
                final JarExplorer explorer = new JarExplorer(currentModuleOrJarOrFolder);
                jarReport = explorer.explore(classLoader);
                resultText = I18N.getString("log.info.explore.jar.results", jarName);
            }
            else if (Files.isDirectory(currentModuleOrJarOrFolder)) {
                LOGGER.info(I18N.getString("log.info.explore.folder", currentModuleOrJarOrFolder));
                final FolderExplorer explorer = new FolderExplorer(currentModuleOrJarOrFolder);
                jarReport = explorer.explore(classLoader);
                resultText = I18N.getString("log.info.explore.folder.results", jarName);
            } else {
                continue;
            }

            moduleOrJarOrFolderReports.add(jarReport);

            StringBuilder sb = new StringBuilder(resultText).append("\n");
            if (jarReport.getEntries().isEmpty()) {
                sb.append("> ").append(I18N.getString("log.info.explore.no.results"));
            } else {
                jarReport.getEntries().forEach(entry -> sb.append("> ").append(entry.toString()).append("\n"));
            }
            LOGGER.info(sb.toString());

            LOGGER.info(I18N.getString("log.info.explore.end", currentModuleOrJarOrFolder));
        }

        // 3)
        final List<LibraryItem> newItems = new ArrayList<>();
        for (JarReport moduleOrJarOrFolderReport : moduleOrJarOrFolderReports) {
            newItems.addAll(makeLibraryItems(moduleOrJarOrFolderReport));
        }

        // 4)
        library.updateClassLoader(classLoader);
        // Remove duplicated items
        library.addItems(newItems
                .stream()
                .distinct()
                .collect(Collectors.toList()));
        library.updateJarReports(new ArrayList<>(moduleOrJarOrFolderReports));
        library.getOnFinishedUpdatingJarReports().accept(moduleOrJarOrFolderReports);
        library.updateExplorationDate(new Date());
        
        // 5
        // Fix for #45: mark end of first exploration
        library.updateFirstExplorationCompleted();
    }
    
    
    private Collection<LibraryItem> makeLibraryItems(JarReport jarOrFolderReport) throws IOException {
        final List<LibraryItem> result = new ArrayList<>();
        final URL iconURL = ImageUtils.getNodeIconURL(null);
        final List<String> excludedItems = library.getFilter();
        final List<String> artifactsFilter = library.getAdditionalFilter().get();
                
        for (JarReportEntry e : jarOrFolderReport.getEntries()) {
            if ((e.getStatus() == JarReportEntry.Status.OK) && e.isNode()) {
                // We filter out items listed in the excluded list, based on canonical name of the class.
                final String canonicalName = e.getKlass().getCanonicalName();
                if (!excludedItems.contains(canonicalName) && 
                    !artifactsFilter.contains(canonicalName)) {
                    final String name = e.getKlass().getSimpleName();
                    final String fxmlText = BuiltinLibrary.makeFxmlText(e.getKlass());
                    result.add(new LibraryItem(name, UserLibrary.TAG_USER_DEFINED, fxmlText, iconURL, library));
                }
            }
        }
        
        return result;
    }
    
    
    private URL[] makeURLArrayFromPaths(Collection<Path> paths) {
        final URL[] result = new URL[paths.size()];
        int i = 0;
        for (Path p : paths) {
            try {
                URL url = p.toUri().toURL();
                if (url.toString().endsWith(".jar")) {
                    result[i++] = new URL("jar", "", url + "!/"); // <-- jar:file/path/to/jar!/
                } else {
                    result[i++] = url; // <-- file:/path/to/folder/ or jrt:/module.name
                }
            } catch (MalformedURLException x) {
                throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); //NOI18N
            }
        }
        
        return result;
    }
}
