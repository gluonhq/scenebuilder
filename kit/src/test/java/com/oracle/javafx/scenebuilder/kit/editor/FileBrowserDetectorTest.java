package com.oracle.javafx.scenebuilder.kit.editor;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class FileBrowserDetectorTest {

    @Test
    void test() {
        
       FileBrowserDetector classUnderTest = new FileBrowserDetector();
       
       Optional<String> x = classUnderTest.getLinuxFileBrowser();
        
        fail("Not yet implemented");
    }

}
