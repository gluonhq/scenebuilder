package com.oracle.javafx.scenebuilder.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;

public class OperatingSystemTest {

    private OperatingSystem classUnderTest;
    
    @Test
    public void that_MacOS_is_properly_detected() {
        assumeTrue("MacOS", System.getProperty("os.name").toLowerCase().contains("mac"));
        classUnderTest = OperatingSystem.get();
        assertEquals(OperatingSystem.MACOS, classUnderTest);
    }

    @Test
    public void that_Windows_is_properly_detected() {
        assumeTrue("Windows", System.getProperty("os.name").toLowerCase().contains("windows"));
        classUnderTest = OperatingSystem.get();
        assertEquals(OperatingSystem.WINDOWS, classUnderTest);
    }

    @Test
    public void that_Linux_is_properly_detected() {
        assumeTrue("Linux", System.getProperty("os.name").toLowerCase().contains("linux"));
        classUnderTest = OperatingSystem.get();
        assertEquals(OperatingSystem.LINUX, classUnderTest);
    }

    @Test
    public void that_other_is_returned_for_unknown_OS() {
        classUnderTest = OperatingSystem.get("BeOS");
        assertEquals(OperatingSystem.OTHER, classUnderTest);
    }
}
