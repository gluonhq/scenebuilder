/*
 * Copyright (c) 2022, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.app.preferences;

import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

/**
 * A record type to work with Scene Builder versions following the semantic
 * version schema. Patch versions can be null and will be ignored then.
 * 
 * Version numbers can be sorted, where major beats minor beats patch versions.
 * Version numbers with patch versions will be considered as higher (or newer)
 * than version numbers without patch numbers. This is regardless if there is no
 * patch number or patch version is 0. Patch version 0 is higher than no patch
 * version at all. If there is any kind of extension such as {@code -SNAPSHOT},
 * than the same version with an extension is considered as the older version.
 * 
 * As of now, Scene Builder release versions follow the schema {@code 17.0.0}
 * which is {@code major.minor.patch}. Snapshot versions declared such as
 * {@code 17.0.0-SNAPSHOT}.
 */
public record AppVersion(int major, int minor, Integer patch, String extension) implements Comparable<AppVersion> {
    
    /**
     * @return {@link Comparator} sorting {@link AppVersion} instances in descending order.
     */
    public static Comparator<AppVersion> descending() {
        return (a,b) -> b.compareTo(a);
    }

    public AppVersion(int major, int minor, int patch) {
        this(major, minor, patch, null);
    }

    public AppVersion(int major, int minor) {
        this(major, minor, null, null);
    }

    @Override
    public int compareTo(AppVersion o) {
        int majorDiff = major - o.major;
        int minorDiff = minor - o.minor;
        int patchDiff = calcPatchDiff(o);
        int extensionDiff = calcExtensionDiff(o);
        if (majorDiff == 0) {
            if (minorDiff == 0) {
                if (patchDiff == 0) {
                    return extensionDiff;
                }
                return patchDiff;
            }
            return minorDiff;
        }
        return majorDiff;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(major);
        builder.append(".");
        builder.append(minor);
        if (patch != null) {
            builder.append(".");
            builder.append(patch);
        }
        if (extension != null) {
            builder.append(extension);
        }
        return builder.toString();
    }
    
    /**
     * Creates a version specific Scene Builder Preferences node name using the given prefix.
     * @param prefix {@link String} used as prefix in Preferences node naming.
     * @return
     */
    public String nodeNameWithPrefix(String prefix) {
        StringBuilder builder = new StringBuilder();
        if (prefix != null && !prefix.isBlank()) {
            builder.append(prefix);
        }
        builder.append(major);
        builder.append(".");
        builder.append(minor);
        if (patch != null) {
            builder.append(".");
            builder.append(patch);
        }
        return builder.toString();
    }

    protected int calcPatchDiff(AppVersion o) {
        if (patch == null && o.patch == null) {
            return 0;
        } else if (patch == null && o.patch != null) {
            return -1;
        } else if (patch != null && o.patch == null) {
            return 1;
        }
        return patch - o.patch;
    }

    protected int calcExtensionDiff(AppVersion o) {
        String thisExt = (extension == null) ? "" : extension;
        String otherExt = (o.extension == null) ? "" : o.extension;
        return -thisExt.compareToIgnoreCase(otherExt);
    }

    /**
     * Parses an optional AppVersion from a given String. The supported version
     * schema must follow: {@code major.minor.patch-extension} whereas major, minor
     * and patch are supposed to be positive integers and the extension can be an
     * arbitrary string.
     * 
     * @param validVersion String representing a Scene Builder version.
     * @return Empty optional when the String does not represent a valid version. If
     *         valid, then an optional AppVersion is returned.
     */
    public static Optional<AppVersion> fromString(String validVersion) {
        String versionToParse = validVersion.toUpperCase(Locale.ROOT);
        String extension = null;
        int lastDot = validVersion.lastIndexOf('.');
        int startOfSnapshot = validVersion.indexOf("-");
        if (startOfSnapshot > lastDot+1) {
            extension = validVersion.substring(startOfSnapshot, validVersion.length());
            versionToParse = validVersion.substring(0, startOfSnapshot);
        }
        String[] elements = versionToParse.strip().split("[.]");
        return switch (elements.length) {
            case 2 -> parseMajorMinor(elements, extension);
            case 3 -> parseMajorMinorPatch(elements, extension);
            default -> Optional.empty();
        };
    }

    private static Optional<AppVersion> parseMajorMinor(String[] elements, String extension) {
        try {
            int major = Integer.parseInt(elements[0]);
            int minor = Integer.parseInt(elements[1]);
            if (major < 0 || minor < 0) {
                return Optional.empty();
            }
            return Optional.of(new AppVersion(major, minor, null, extension));
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }

    private static Optional<AppVersion> parseMajorMinorPatch(String[] elements, String extension) {
        try {
            int major = Integer.parseInt(elements[0]);
            int minor = Integer.parseInt(elements[1]);
            int patch = Integer.parseInt(elements[2]);
            if (major < 0 || minor < 0 || patch < 0) {
                return Optional.empty();
            }
            return Optional.of(new AppVersion(major, minor, patch, extension));
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }
}
