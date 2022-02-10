package com.oracle.javafx.scenebuilder.app.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.control.ButtonType;

public class PreferencesImporterTest {

    private PreferencesImporter classUnderTest;

    @BeforeAll
    @AfterAll
    public static void cleanUpPrefs() throws Exception {
        Set<String> nodesToBeRemoved = Set.of("SOURCE_TO_IMPORT", 
                                              "SB_TEST_TARGET",
                                              "SB_TEST_TARGET2",
                                              "SB_OLD_VER");
        for (String nodeName : nodesToBeRemoved) {
            Preferences.userRoot().node(nodeName).removeNode();
        }
    }

    @Test
    public void that_settings_are_copied_between_nodes() throws Exception {
        Preferences mySourcePrefs = Preferences.userRoot().node("SOURCE_TO_IMPORT");
        mySourcePrefs.put("anykey", "anyvalue");
        mySourcePrefs.node("CHILD1").put("key1", "value1");
        mySourcePrefs.node("CHILD1").node("CHILD2").put("key2", "value2");
        mySourcePrefs.node("CHILD1").node("CHILD2").node("CHILD3").put("key3", "value3");
        mySourcePrefs.node("CHILD1").node("CHILD2").node("CHILD4");

        Preferences myTargetPrefs = Preferences.userRoot().node("SB_TEST_TARGET");

        classUnderTest = new PreferencesImporter(myTargetPrefs, Optional.empty());
        classUnderTest.importFrom(mySourcePrefs);

        assertEquals("anyvalue", myTargetPrefs.get("anykey", null));
        assertEquals("value1", myTargetPrefs.node("CHILD1").get("key1", null));
        assertEquals("value2", myTargetPrefs.node("CHILD1").node("CHILD2").get("key2", null));
        assertEquals("value3", myTargetPrefs.node("CHILD1").node("CHILD2").node("CHILD3").get("key3", null));
    }

    @Test
    public void that_user_will_be_only_asked_when_import_decision_was_not_made() throws Exception {
        Preferences appPrefs = Preferences.userRoot().node("SB_TEST_TARGET");
        appPrefs.clear();
        
        classUnderTest = new PreferencesImporter(appPrefs, Optional.empty());
        
        assertTrue(classUnderTest.askForImport());
        
        classUnderTest.documentThatNoImportIsDesired();
        assertFalse(classUnderTest.askForImport());
    }

    @Test
    public void that_user_will_be_only_asked_when_previous_version_settings_exist() throws Exception {
        AppVersion oldVersion = new AppVersion(0, 9);
        Preferences olderPrefs = Preferences.userRoot().node("SB_OLD_VER");
        Preferences appPrefs = Preferences.userRoot().node("SB_TEST_TARGET2");

        // ensure the PREF_PERFORM_IMPORT setting does not exist
        appPrefs.remove(PreferencesImporter.PREF_PERFORM_IMPORT);
        classUnderTest = new PreferencesImporter(appPrefs, Optional.of(new VersionedPreferences(oldVersion, olderPrefs)));
        assertTrue(classUnderTest.askForImportIfOlderSettingsExist());

        classUnderTest = new PreferencesImporter(appPrefs, Optional.empty());
        assertFalse(classUnderTest.askForImportIfOlderSettingsExist());

        classUnderTest = new PreferencesImporter(appPrefs, Optional.of(new VersionedPreferences(oldVersion, olderPrefs)));
        classUnderTest.documentThatNoImportIsDesired();
        assertFalse(classUnderTest.askForImportIfOlderSettingsExist());
    }

    @Test
    public void that_run_after_import_action_is_executed_in_tryImportingPreviousVersionSettings() {
        Set<String> responses = new HashSet<>();
        Runnable action = () -> responses.add("action performed");

        AppVersion oldVersion = new AppVersion(0, 9);
        Preferences olderPrefs = Preferences.userRoot().node("SB_OLD_VER");
        olderPrefs.put("somekey", "1234");

        Preferences appPrefs = Preferences.userRoot().node("SB_TEST_TARGET2");
        Optional<VersionedPreferences> previousVersionSettings = Optional.of(new VersionedPreferences(oldVersion, olderPrefs));

        classUnderTest = new PreferencesImporter(appPrefs, previousVersionSettings);
        classUnderTest.runAfterImport(action);
        classUnderTest.tryImportingPreviousVersionSettings();

        assertTrue(responses.contains("action performed"));
        assertEquals("1234", appPrefs.get("somekey", null));
        assertNotNull(appPrefs.get(PreferencesImporter.PREF_PERFORM_IMPORT, null));
    }
    
    @Test
    public void that_null_value_for_run_after_import_action_is_not_accepted() {
        Preferences appPrefs = Preferences.userRoot().node("SB_TEST_TARGET");
        classUnderTest = new PreferencesImporter(appPrefs, Optional.empty());
        assertThrows(NullPointerException.class,
                () -> classUnderTest.runAfterImport(null));
    }

    @Test
    public void that_user_interaction_is_executed_and_user_opt_out_decision_is_documented() throws Exception {
        Preferences appPrefs = Preferences.userRoot().node("SB_TEST_TARGET");
        appPrefs.clear();

        classUnderTest = new PreferencesImporter(appPrefs, Optional.empty());

        Set<String> interactionResponses = new HashSet<>();

        Supplier<Optional<ButtonType>> userInteraction = () -> {
            interactionResponses.add("alert-opened");
            return Optional.empty();
        };

        classUnderTest.executeInteractionAndImport(userInteraction);
        String documentedUserDecision = appPrefs.get(PreferencesImporter.PREF_PERFORM_IMPORT, "");
        assertEquals("false",documentedUserDecision);
    }
    
    @Test
    public void that_user_interaction_is_executed_and_user_opted_in() throws Exception {
        Preferences appPrefs = Preferences.userRoot().node("SB_TEST_TARGET2");
        appPrefs.clear();

        AppVersion oldVersion = new AppVersion(0, 9);
        Preferences olderPrefs = Preferences.userRoot().node("SB_OLD_VER");
        olderPrefs.put("somekey", "1234");

        Optional<VersionedPreferences> previousVersionSettings = Optional.of(new VersionedPreferences(oldVersion, olderPrefs));

        classUnderTest = new PreferencesImporter(appPrefs, previousVersionSettings);

        Set<String> interactionResponses = new HashSet<>();

        Supplier<Optional<ButtonType>> userInteraction = () -> {
            interactionResponses.add("alert-opened");
            return Optional.of(ButtonType.YES);
        };

        classUnderTest.askForActionAndRun(userInteraction);
        String documentedUserDecision = appPrefs.get(PreferencesImporter.PREF_PERFORM_IMPORT, "");

        assertTrue(interactionResponses.contains("alert-opened"));
        assertFalse(documentedUserDecision.contains("-no-import"));
        assertNotEquals("", documentedUserDecision);
    }

    /** 
     * This is only for debugging so that the import decision can be removed if needed 
     */
    public static void main(String[] args) {
        PreferencesController.getSingleton()
                             .getImporter()
                             .clearImportDecision();
    }
}
