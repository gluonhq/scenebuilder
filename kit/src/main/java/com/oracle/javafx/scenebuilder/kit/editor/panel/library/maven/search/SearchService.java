/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;

public class SearchService extends Service<Void> {

    private final ExecutorService exec = Executors.newFixedThreadPool(5, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t ;
    });
    
    private String query;
    
    private final ObservableList<DefaultArtifact> result;
    private final BooleanProperty searching;
    
    private List<Task<ObservableList<DefaultArtifact>>> tasks;

    private final String userM2Repository;
    
    public SearchService(String userM2Repository) {
        setExecutor(exec);
        result = FXCollections.observableArrayList();
        searching = new SimpleBooleanProperty();
        this.userM2Repository = userM2Repository;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public ObservableList<DefaultArtifact> getResult() {
        return result;
    }
    
    public BooleanProperty searchingProperty() {
        return searching;
    }

    public void cancelSearch() {
        if (tasks != null) {
            tasks.forEach(Task::cancel);
        }
        searching.set(false);
    }
    
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (query == null || query.isEmpty()) {
                    return null;
                }
                // TODO: Manage other search engines
                // TODO: Retrieve user/password from Preferences
        
                tasks = Arrays.asList(
                    createSearchTask(new MavenSearch()),
                    createSearchTask(new NexusSearch(MavenPresets.SONATYPE, "http://oss.sonatype.org", "", "")),
                    createSearchTask(new NexusSearch(MavenPresets.GLUON_NEXUS, "https://nexus.gluonhq.com/nexus", "", "")),
                    createSearchTask(new LocalSearch(userM2Repository)));
                
                AtomicInteger count = new AtomicInteger();
                tasks.forEach(task -> 
                    task.stateProperty().addListener((obs, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED || newState == Worker.State.CANCELLED ||
                            newState == Worker.State.FAILED) {
                            if (count.getAndIncrement() == tasks.size() - 1) {
                                searching.set(false);
                            }
                            if (newState == Worker.State.SUCCEEDED && task.getValue() != null) {
                                List<DefaultArtifact> list = new ArrayList<>(result);
                                list.addAll(task.getValue());
                                
                                result.setAll(getLatestVersions(
                                            list.stream()
                                                .distinct()
                                                .collect(Collectors.groupingBy(a -> a.getGroupId() + ":" + a.getArtifactId()))));
                            }
                        }
                    }));
                
                Platform.runLater(() -> {
                    result.clear();
                    searching.set(true);
                });
                tasks.forEach(exec::execute);
                return null;
            }
        };
    }
    
    private Task<ObservableList<DefaultArtifact>> createSearchTask(Search search) {
        return new Task<ObservableList<DefaultArtifact>>() {
            @Override
            protected ObservableList<DefaultArtifact> call() throws Exception {
                return FXCollections.observableArrayList(search.getCoordinates(query));
            }
        };
    }
    
    private List<DefaultArtifact> getLatestVersions(Map<String, List<DefaultArtifact>> mapArtifacts) {
        List<DefaultArtifact> list = new ArrayList<>();
        mapArtifacts.forEach((s, l) -> {
            DefaultArtifact da = l.stream()
                    // TODO: Include snapshots
                    .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("snapshot"))
                    .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("javadoc"))
                    .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("source"))
                    .reduce((a1, a2) -> {
                        Version v1 = getVersion(a1.getVersion());
                        Version v2 = getVersion(a2.getVersion());
                        if (v1 != null && v2 != null && v1.compareTo(v2) > 0) {
                            return a1;
                        } else {
                            return a2;
                        }
                    })
                    .get();
            list.add(da);
        });
        return list;
    }
    
    // TODO: Return all versions, including snapshots
    private List<DefaultArtifact> getAllVersions(Map<String, List<DefaultArtifact>> mapArtifacts) {
        List<DefaultArtifact> list = new ArrayList<>();
        mapArtifacts.forEach((s, l) -> {
            l.stream()
                .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("javadoc"))
                .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("source"))
                .forEach(list::add);
        });
        return list;
    }
    
    private Version getVersion(String version) {
        Version v1 = null;
        try {
            v1 = new GenericVersionScheme().parseVersion(version);
        } catch (InvalidVersionSpecificationException ivse) { }
        return v1;
    }
    
}
