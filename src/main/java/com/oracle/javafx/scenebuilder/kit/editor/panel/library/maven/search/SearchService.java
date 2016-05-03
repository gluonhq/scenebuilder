package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
import org.eclipse.aether.artifact.Artifact;
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
    
    private final ObservableList<Artifact> result;
    private final BooleanProperty searching;
    
    private List<Task<ObservableList<Artifact>>> tasks;
    
    public SearchService() {
        setExecutor(exec);
        result = FXCollections.observableArrayList();
        searching = new SimpleBooleanProperty();
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public ObservableList<Artifact> getResult() {
        return result;
    }
    
    public BooleanProperty searchingProperty() {
        return searching;
    }

    public void cancelSearch() {
        tasks.forEach(Task::cancel);
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
                tasks = Arrays.asList(
                    createSearchTask("Maven Central", new MavenSearch()),
                    createSearchTask("Sonatype (releases)", new SonatypeSearch()),
                    createSearchTask("Jcenter", new JcenterSearch()),
                    createSearchTask("Gluon Nexus", new NexusSearch()),
                    createSearchTask("Local", new LocalSearch()));
                
                AtomicInteger count = new AtomicInteger();
                tasks.forEach(task -> 
                    task.stateProperty().addListener((obs, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED || newState == Worker.State.CANCELLED ||
                            newState == Worker.State.FAILED) {
                            if (count.getAndIncrement() == tasks.size() - 1) {
                                searching.set(false);
                            }
                            if (newState == Worker.State.SUCCEEDED && task.getValue() != null) {
                                result.addAll(task.getValue()
                                        .stream()
                                        .filter(a -> result
                                                .stream()
                                                .noneMatch(ar -> ar.getGroupId().equals(a.getGroupId()) &&
                                                        ar.getArtifactId().equals(a.getArtifactId()) &&
                                                        ar.getVersion().equals(a.getVersion())))
                                        .collect(Collectors.toList()));
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
    
    private Task<ObservableList<Artifact>> createSearchTask(String name, Search search) {
        return new Task<ObservableList<Artifact>>() {
            @Override
            protected ObservableList<Artifact> call() throws Exception {
                return FXCollections.observableArrayList(reduceMap(name, search.getCoordinates(query)));
            }
        };
    }
    
    private List<Artifact> reduceMap(String name, Map<String, List<DefaultArtifact>> mapArtifacts) {
        List<Artifact> list = new ArrayList<>();
        mapArtifacts.forEach((s, l) -> {
            Version v = l.stream()
//                .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("snapshot"))
                .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("javadoc"))
                .filter(a -> !a.getVersion().toLowerCase(Locale.ROOT).contains("source"))
                    
                .map(a -> {
                    try { 
                        return new GenericVersionScheme().parseVersion(a.getVersion()); 
                    } catch (InvalidVersionSpecificationException ivse) { 
                        return null; 
                    }
                })
                .filter(Objects::nonNull)
                .reduce((v1, v2) -> {
                    if (v1.compareTo(v2) > 0) {
                        return v1;
                    } else {
                        return v2;
                    }
                })
                .get();
            Map<String, String> map = new HashMap<>();
            map.put("Repository", name);
            list.add(new DefaultArtifact(s + ":" + v.toString(), map));
        });
        return list;
    }
}
