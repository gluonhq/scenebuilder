package com.oracle.javafx.scenebuilder.app;

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
public class PlatformSpecificDirectories implements ApplicationDirectories {
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
     * @throws UnsupportedOperationException in case of the operating system is unknown
     */
    @Override
    public String getApplicationDataFolder() {
        String appDataRoot = getApplicationDataRoot();
        String appDataSubFolder = getApplicationDataSubFolder();
        return appDataRoot+"/"+appDataSubFolder;
    }

    /**
     * @return the root location where application data shall be stored on the
     *         corresponding platform.
     * @throws UnsupportedOperationException in case of the operating system is unknown
     */
    @Override
    public String getApplicationDataRoot() {
        return switch (os) {
            case WINDOWS -> System.getenv("APPDATA") + "\\";
            case MACOS -> System.getProperty("user.home") + "/Library/Application Support/";
            case LINUX -> System.getProperty("user.home") + "/";
            default -> throw new UnsupportedOperationException("Unknown operating system platform!");
        };
    }

    @Override
    public String getUserLibraryFolder() {
        return getApplicationDataFolder() + "/Library";
    }

    /**
     * @return Provides the name of the by default version specific application data
     *         sub directory. This folder is will be located inside the application
     *         data root folder.
     * @throws UnsupportedOperationException in case of the operating system is unknown
     */
    @Override
    public String getApplicationDataSubFolder() {
        return getApplicationDataSubFolder(true);
    }

    /**
     * In previous versions, Scene Builder stored its files in a directory without
     * version number. Hence, in some cases it might be helpful to control when the
     * version number is used or not. To obtain this folder without version number
     * can be beneficial in cases, where one wants to search the application data root
     * folder for other existing settings of Scene Builder.
     * 
     * @param includeVersion If true, the version number might be a part of the sub
     *                       folder name.
     * @return Provides the name of the application data sub directory with or
     *         without version information.
     * @throws UnsupportedOperationException in case of the operating system is unknown
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
            default -> throw new UnsupportedOperationException("Unknown operating system platform!");
        };
    }

    /**
     * Returns the directory path for logs. Default path is "${user.home}/.scenebuilder/logs/".
     * @return Directory path for Scene Builder logs
     */
    @Override
    public String getLogFolder() {
        return Paths.get(System.getProperty("user.home"), 
                         ".scenebuilder", "logs").toString();
    }

    @Override
    public String getMessageBoxFolder() {
        return getApplicationDataFolder() + "/MB";
    }
}
