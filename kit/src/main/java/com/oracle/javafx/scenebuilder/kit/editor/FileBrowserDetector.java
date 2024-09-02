package com.oracle.javafx.scenebuilder.kit.editor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


class FileBrowserDetector {

    private static final Logger LOGGER = Logger.getLogger(FileBrowserDetector.class.getName());
    
    private Optional<String> detectedExecutable = null;
    
    public Optional<String> getLinuxFileBrowser() {
        if (this.detectedExecutable == null) {
            this.detectedExecutable = detectLinuxFileManager();      
        }
        
        return this.detectedExecutable;
    }
    
    public void detect() {
        this.detectedExecutable = detectLinuxFileManager();
    }
    
    Optional<String> detectLinuxFileManager() {       
        var filemanager = detectApplication("nautilus");
        if (filemanager.isPresent()) {
            return filemanager;
        } 
        
        filemanager = detectApplication("dolphin");
        if (filemanager.isPresent()) {
            return filemanager;
        }
        
        filemanager = detectApplication("xdg-open");
        if (filemanager.isPresent()) {
            return filemanager;
        }

        return Optional.empty();
    }
    
    Optional<String> detectApplication(String applicationName) {
        var lookupCmd = EditorPlatform.IS_WINDOWS ? "where" : "which";
        List<String> cmd = List.of(lookupCmd, String.valueOf(applicationName).strip());
        return exec(cmd);
    }
    
    private Optional<String> exec(List<String> cmd) {
        LOGGER.log(Level.INFO, "Attempting to run: {0}", cmd.stream().collect(Collectors.joining(" ")));
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            Process proc = builder.start();
            proc.waitFor(100, TimeUnit.SECONDS);
            
            List<String> lines = new ArrayList<>();
            try (BufferedReader rdr = proc.inputReader()) {
                String line = null;
                while((line=rdr.readLine()) != null) {
                    lines.add(line);
                }
            }
            if (!lines.isEmpty()) {
                return Optional.of(lines.get(0).strip());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return Optional.empty();
    }

}
