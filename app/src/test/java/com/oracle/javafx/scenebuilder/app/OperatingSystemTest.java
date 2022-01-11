package com.oracle.javafx.scenebuilder.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;

public class OperatingSystemTest {

    @Test
    public void that_MacOS_is_properly_detected() {
        assumeTrue("MacOS", System.getProperty("os.name").toLowerCase().contains("mac"));
        OperatingSystem os = OperatingSystem.get();
        assertEquals(OperatingSystem.MACOS, os);
    }

    @Test
    public void that_Windows_is_properly_detected() {
        assumeTrue("Windows", System.getProperty("os.name").toLowerCase().contains("windows"));
        OperatingSystem os = OperatingSystem.get();
        assertEquals(OperatingSystem.WINDOWS, os);
    }

    @Test
    public void that_Linux_is_properly_detected() {
        assumeTrue("Linux", System.getProperty("os.name").toLowerCase().contains("linux"));
        OperatingSystem os = OperatingSystem.get();
        assertEquals(OperatingSystem.LINUX, os);
    }

}
