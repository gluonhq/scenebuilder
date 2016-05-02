package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven.repository;

public class Repository {
    
    private String id;
    private String type;
    private String URL;
    private String user;
    private String password;

    public Repository(String id, String type, String URL) {
        this.id = id;
        this.type = type;
        this.URL = URL;
        this.user = null;
        this.password = null;
    }

    public Repository(String id, String type, String URL, String user, String password) {
        this.id = id;
        this.type = type;
        this.URL = URL;
        this.user = user;
        this.password = password;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Repository{" + "id=" + id + ", type=" + type + ", URL=" + URL + ", user=" + user + ", password=" + password + '}';
    }
    
}
