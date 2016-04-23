package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.search;

import java.util.Arrays;
import java.util.List;
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

public class SearchService extends Service<Void> {

    private final ExecutorService exec = Executors.newFixedThreadPool(5, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t ;
    });
    
    private String query;
    
    private final ObservableList<String> result;
    private final BooleanProperty searching;
    
    private List<Task<ObservableList<String>>> tasks;

    public SearchService() {
        setExecutor(exec);
        result = FXCollections.observableArrayList();
        searching = new SimpleBooleanProperty();
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public ObservableList<String> getResult() {
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
                    createSearchTask(new MavenSearch()),
                    createSearchTask(new SonatypeSearch()),
                    createSearchTask(new JcenterSearch()),
                    createSearchTask(new NexusSearch()),
                    createSearchTask(new LocalSearch()));
                
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
                                        .filter(a -> !result.contains(a))
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
    
    private Task<ObservableList<String>> createSearchTask(Search search) {
        return new Task<ObservableList<String>>() {
            @Override
            protected ObservableList<String> call() throws Exception {
                return FXCollections.observableArrayList(search.getCoordinates(query));
            }
        };
    }
}
