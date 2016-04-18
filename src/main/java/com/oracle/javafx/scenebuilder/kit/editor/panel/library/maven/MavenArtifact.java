package com.oracle.javafx.scenebuilder.kit.editor.panel.library.maven;

import java.util.Objects;

public class MavenArtifact {
    
    private String coordinates;
    private String path;
    private String dependencies;
    private String filter;

    public MavenArtifact(String coordinates) {
        this.coordinates = coordinates;
    }

    public MavenArtifact(String coordinates, String path, String dependencies, String filter) {
        this.coordinates = coordinates;
        this.path = path;
        this.dependencies = dependencies;
        this.filter = filter;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.coordinates);
        hash = 97 * hash + Objects.hashCode(this.path);
        hash = 97 * hash + Objects.hashCode(this.dependencies);
        hash = 97 * hash + Objects.hashCode(this.filter);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MavenArtifact other = (MavenArtifact) obj;
        if (!Objects.equals(this.coordinates, other.coordinates)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        if (!Objects.equals(this.dependencies, other.dependencies)) {
            return false;
        }
        return Objects.equals(this.filter, other.filter);
    }

    
    @Override
    public String toString() {
        return "MavenArtifact{" + "coordinates=" + coordinates + ", path=" + path + ", dependencies=" + dependencies + ", filter=" + filter + '}';
    }

}
