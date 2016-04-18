package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven;

public class Repository {
    
    private String id;
    private String type;
    private String URL;

    public Repository(String id, String type, String URL) {
        this.id = id;
        this.type = type;
        this.URL = URL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Override
    public String toString() {
        return "Repository{" + "id=" + id + ", type=" + type + ", URL=" + URL + '}';
    }
    
}
