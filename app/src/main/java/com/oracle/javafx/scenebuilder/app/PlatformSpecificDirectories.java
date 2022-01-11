package com.oracle.javafx.scenebuilder.app;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * This is where all relevant application directories and paths are defined.
 * If a new operating system (OS) platform shall be supported, the OS specific
 * behavior is supposed to be implemented here in.
 */
public class PlatformSpecificDirectories implements ApplicationDirectories {
    protected final OperatingSystem os;
    protected final String version;
    
    public PlatformSpecificDirectories(OperatingSystem os, String appVersion) {
        this.os = Objects.requireNonNull(os);
        this.version = appVersion;
    }
    
    @Override
    public String getApplicationDataFolder() {
        String appDataRoot = getApplicationDataRoot();
        String appDataSubFolder = getApplicationDataSubFolder();
        return appDataRoot+"/"+appDataSubFolder;
    }
    
    @Override
    public String getApplicationDataRoot() {
        switch (os) {
            case WINDOWS: return System.getenv("APPDATA") + "\\";
            case MACOS: return System.getProperty("user.home") + "/Library/Application Support/";
            case LINUX: return System.getProperty("user.home") + "/";
            default: return null;
        }
    }

    @Override
    public String getUserLibraryFolder() {
        return getApplicationDataFolder() + "/Library";
    }

    @Override
    public String getApplicationDataSubFolder() {
        return getApplicationDataSubFolder(true);
    }
    
    @Override
    public String getApplicationDataSubFolder(boolean includeVersion) {
        final String appName = "Scene Builder"; //NOI18N
        final String appVersion = (version == null) ? "" : version;
        final String suffix = ("".equalsIgnoreCase(appVersion) || !includeVersion) ? "" : "-"+appVersion;
        return switch (os) {
            case WINDOWS -> appName+suffix;
            case MACOS -> appName+suffix;
            case LINUX -> ".scenebuilder"+suffix;
            default -> null;
        };
    }

    @Override
    public String getLogFolder() {
        return Paths.get(System.getProperty("user.home"), 
                         ".scenebuilder-"+version, "logs").toString();
    }

    @Override
    public String getMessageBoxFolder() {
        return getApplicationDataFolder() + "/MB";
    }
}
