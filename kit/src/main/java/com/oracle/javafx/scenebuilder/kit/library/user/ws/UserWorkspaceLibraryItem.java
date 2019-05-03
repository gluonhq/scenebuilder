package com.oracle.javafx.scenebuilder.kit.library.user.ws;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * This class contains a path to a jar or an fxml or to a folder.
 */
public class UserWorkspaceLibraryItem {

    @XStreamAsAttribute private String path;

    public UserWorkspaceLibraryItem(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    
    @Override
    public String toString() {
        return path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserWorkspaceLibraryItem other = (UserWorkspaceLibraryItem) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return true;
    }
    
}
