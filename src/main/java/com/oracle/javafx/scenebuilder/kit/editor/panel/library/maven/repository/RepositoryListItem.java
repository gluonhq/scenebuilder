package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository;

public class RepositoryListItem {

    private final RepositoryManagerController repositoryManagerController;
    private final Repository repository;

    public RepositoryListItem(RepositoryManagerController repositoryManagerController, Repository repository) {
        this.repositoryManagerController = repositoryManagerController;
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public RepositoryManagerController getRepositoryManagerController() {
        return repositoryManagerController;
    }
    
}
