package com.oracle.javafx.scenebuilder.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/*
 * TODO: Replace this with one implementation per platform. 
 * With that, all the conditions/switches will vanish.
 */
/**
 * This is where all relevant application directories and paths are defined.
 * If a new operating system (OS) platform shall be supported, the OS specific
 * behavior is supposed to be implemented here in.
 */
public class PlatformSpecificDirectories implements AppPlatformDirectories {
    protected final OperatingSystem os;
    protected final String version;

    public PlatformSpecificDirectories(OperatingSystem os, String appVersion) {
        this.os = Objects.requireNonNull(os);
        this.version = appVersion;
    }

    /**
     * The application data folder is usually a child folder inside the application
     * data root.
     * 
     * @return the exact location, specifically for this version of Scene Builder,
     *         where settings and arbitrary files can be placed.
     */
    @Override
    public Path getApplicationDataFolder() {
        return getApplicationDataRoot().resolve(getApplicationDataSubFolder());
    }

    /**
     * @return the root location where application data shall be stored on the
     *         corresponding platform.
     */
    @Override
    public Path getApplicationDataRoot() {
        return switch (os) {
            case WINDOWS -> Paths.get(System.getenv("APPDATA"));
            case MACOS -> Paths.get(System.getProperty("user.home")).resolve("Library/Application Support/");
            case LINUX -> Paths.get(System.getProperty("user.home"));
            default -> Paths.get(System.getProperty("user.home"));
        };
    }

    @Override
    public Path getUserLibraryFolder() {
        return getApplicationDataFolder().resolve("Library");
    }

    /**
     * Provides the name of the application data sub directory (with version number). 
     * 
     * @return Provides the name of the by default version specific application data
     *         sub directory. This folder is will be located inside the application
     *         data root folder.
     */
    @Override
    public String getApplicationDataSubFolder() {
        return getApplicationDataSubFolder(true);
    }

    /**
     * Provides the name of the application data sub directory (with or without
     * version number). 
     * 
     * In previous versions, Scene Builder stored its files in a
     * directory without version number. Hence, in some cases it might be helpful to
     * control when the version number is used or not. To obtain this folder without
     * version number can be beneficial in cases, where one wants to search the
     * application data root folder for other existing settings of Scene Builder.
     * 
     * @param includeVersion If true, the version number might be a part of the sub
     *                       folder name.
     * @return Provides the name of the application data sub directory with or
     *         without version information.
     */
    @Override
    public String getApplicationDataSubFolder(boolean includeVersion) {
        final String appName = "Scene Builder"; //NOI18N
        final String appVersion = (version == null) ? "" : version;
        final String suffix = ("".equalsIgnoreCase(appVersion) || !includeVersion) ? "" : "-"+appVersion;
        return switch (os) {
            case WINDOWS -> appName+suffix;
            case MACOS -> appName+suffix;
            case LINUX -> ".scenebuilder"+suffix;
            default -> ".scenebuilder"+suffix;
        };
    }

    /**
     * Returns the directory path for logs. Default path is "${user.home}/.scenebuilder/logs/".
     * @return Directory path for Scene Builder logs
     */
    @Override
    public Path getLogFolder() {
        return Paths.get(System.getProperty("user.home"),".scenebuilder", "logs");
    }

    /**
     * Location of Scene Builders Message Box
     * @return The directory where the Message Box is saved.
     */
    protected Path getMessageBoxFolder() {
        return getApplicationDataFolder().resolve("MB");
    }
}