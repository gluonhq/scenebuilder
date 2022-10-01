/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.app;

import com.oracle.javafx.scenebuilder.app.DocumentWindowController.ActionStatus;
import com.oracle.javafx.scenebuilder.app.about.AboutWindowController;
import com.oracle.javafx.scenebuilder.kit.preferences.MavenPreferences;
import com.oracle.javafx.scenebuilder.kit.ResourceUtils;
import com.oracle.javafx.scenebuilder.kit.ToolTheme;
import com.oracle.javafx.scenebuilder.kit.alert.ImportingGluonControlsAlert;
import com.oracle.javafx.scenebuilder.kit.alert.SBAlert;
import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import com.oracle.javafx.scenebuilder.app.menubar.MenuBarController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesWindowController;
import com.oracle.javafx.scenebuilder.app.registration.RegistrationWindowController;
import com.oracle.javafx.scenebuilder.kit.library.util.JarReport;
import com.oracle.javafx.scenebuilder.kit.template.Template;
import com.oracle.javafx.scenebuilder.kit.template.TemplatesWindowController;
import com.oracle.javafx.scenebuilder.kit.template.Type;
import com.oracle.javafx.scenebuilder.app.tracking.Tracking;
import com.oracle.javafx.scenebuilder.app.util.AppSettings;
import com.oracle.javafx.scenebuilder.app.welcomedialog.WelcomeDialogWindowController;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AlertDialog;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.ErrorDialog;
import com.oracle.javafx.scenebuilder.kit.library.BuiltinLibrary;
import com.oracle.javafx.scenebuilder.kit.library.user.UserLibrary;
import com.oracle.javafx.scenebuilder.kit.metadata.Metadata;
import com.oracle.javafx.scenebuilder.kit.util.control.effectpicker.EffectPicker;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * This is the SB main entry point.
 */
public class SceneBuilderApp extends Application implements AppPlatform.AppNotificationHandler {

    public enum ApplicationControlAction {
        ABOUT,
        CHECK_UPDATES,
        REGISTER,
        NEW_FILE,
        NEW_TEMPLATE,
        OPEN_FILE,
        CLOSE_FRONT_WINDOW,
        USE_DEFAULT_THEME,
        USE_DARK_THEME,
        SHOW_PREFERENCES,
        EXIT,
        SHOW_WELCOME
    }

    private static SceneBuilderApp singleton;

    private final ObservableList<DocumentWindowController> windowList = FXCollections.observableArrayList();
    private UserLibrary userLibrary;
    private ToolTheme toolTheme = ToolTheme.DEFAULT;

    private final ObservableList<Runnable> startupTasks = FXCollections.observableArrayList();
    private final BooleanBinding startupTasksFinished = Bindings.isEmpty(startupTasks);

    static {
        System.setProperty("java.util.logging.config.file", SceneBuilderApp.class.getResource("/logging.properties").getPath());
    }

    /*
     * Public
     */
    public static SceneBuilderApp getSingleton() {
        return singleton;
    }

    public SceneBuilderApp() {
        assert singleton == null;
        singleton = this;

        // set design time flag
        java.beans.Beans.setDesignTime(true);
        
        // SB-270
        windowList.addListener((ListChangeListener.Change<? extends DocumentWindowController> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    final String toolStylesheet = getToolStylesheet();
                    for (DocumentWindowController dwc : c.getAddedSubList()) {
                        dwc.setToolStylesheet(toolStylesheet);
                    }
                }
            }
        });
    }

    public BooleanBinding startupTasksFinishedBinding() {
        return startupTasksFinished;
    }

    public void performControlAction(ApplicationControlAction a, DocumentWindowController source) {
        switch (a) {
            case ABOUT:
                AboutWindowController aboutWindowController = new AboutWindowController();
                aboutWindowController.setToolStylesheet(getToolStylesheet());
                aboutWindowController.openWindow();
                AppSettings.setWindowIcon(aboutWindowController.getStage());
                break;

            case REGISTER:
                final RegistrationWindowController registrationWindowController = new RegistrationWindowController(source.getStage());
                registrationWindowController.openWindow();
                break;

            case CHECK_UPDATES:
                checkUpdates(source);
                break;

            case NEW_FILE:
                final DocumentWindowController newWindow = makeNewWindow();
                newWindow.updateWithDefaultContent();
                newWindow.openWindow();
                break;

            case NEW_TEMPLATE:
                final TemplatesWindowController templatesWindowController = new TemplatesWindowController(source.getStage());
                templatesWindowController.setOnTemplateChosen(template -> {
                    templatesWindowController.getStage().hide();
                    performNewTemplateInNewWindow(template);
                });
                templatesWindowController.openWindow();
                break;

            case OPEN_FILE:
                performOpenFile();
                break;

            case CLOSE_FRONT_WINDOW:
                performCloseFrontWindow();
                break;

            case USE_DEFAULT_THEME:
                performUseToolTheme(ToolTheme.DEFAULT);
                break;

            case USE_DARK_THEME:
                performUseToolTheme(ToolTheme.DARK);
                break;

            case SHOW_PREFERENCES:
                PreferencesWindowController preferencesWindowController = new PreferencesWindowController(source.getStage());
                preferencesWindowController.setToolStylesheet(getToolStylesheet());
                preferencesWindowController.openWindow();
                break;

            case EXIT:
                performExit();
                break;

            case SHOW_WELCOME:
                WelcomeDialogWindowController.getInstance().getStage().show();
                break;
        }
    }

    public boolean canPerformControlAction(ApplicationControlAction a, DocumentWindowController source) {
        final boolean result;
        switch (a) {
            case ABOUT:
            case REGISTER:
            case CHECK_UPDATES:
            case NEW_FILE:
            case NEW_TEMPLATE:
            case OPEN_FILE:
            case SHOW_PREFERENCES:
            case EXIT:
            case SHOW_WELCOME:
                result = true;
                break;

            case CLOSE_FRONT_WINDOW:
                result = windowList.isEmpty() == false;
                break;

            case USE_DEFAULT_THEME:
                result = toolTheme != ToolTheme.DEFAULT;
                break;

            case USE_DARK_THEME:
                result = toolTheme != ToolTheme.DARK;
                break;

            default:
                result = false;
                assert false;
                break;
        }
        return result;
    }

    public void performOpenRecent(DocumentWindowController source, final File fxmlFile) {
        assert fxmlFile != null && fxmlFile.exists();

        final List<File> fxmlFiles = new ArrayList<>();
        fxmlFiles.add(fxmlFile);
        performOpenFiles(fxmlFiles);
    }

    public void documentWindowRequestClose(DocumentWindowController fromWindow) {
        closeWindow(fromWindow);
    }

    public UserLibrary getUserLibrary() {
        return userLibrary;
    }

    public List<DocumentWindowController> getDocumentWindowControllers() {
        return Collections.unmodifiableList(windowList);
    }

    public DocumentWindowController lookupDocumentWindowControllers(URL fxmlLocation) {
        assert fxmlLocation != null;

        DocumentWindowController result = null;
        try {
            final URI fxmlURI = fxmlLocation.toURI();
            for (DocumentWindowController dwc : windowList) {
                final URL docLocation = dwc.getEditorController().getFxmlLocation();
                if ((docLocation != null) && fxmlURI.equals(docLocation.toURI())) {
                    result = dwc;
                    break;
                }
            }
        } catch (URISyntaxException x) {
            // Should not happen
            throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); //NOI18N
        }

        return result;
    }

    public Optional<DocumentWindowController> findFirstUnusedDocumentWindowController() {
        return windowList.stream()
                .filter(DocumentWindowController::isUnused)
                .findFirst();
    }

    public void toggleDebugMenu() {
        final boolean visible;

        if (windowList.isEmpty()) {
            visible = false;
        } else {
            final DocumentWindowController dwc = windowList.get(0);
            visible = dwc.getMenuBarController().isDebugMenuVisible();
        }

        for (DocumentWindowController dwc : windowList) {
            dwc.getMenuBarController().setDebugMenuVisible(!visible);
        }

        if (EditorPlatform.IS_MAC) {
            MenuBarController.getSystemMenuBarController().setDebugMenuVisible(!visible);
        }
    }

    /*
     * Application
     */
    @Override
    public void start(Stage stage) throws Exception {
        setApplicationUncaughtExceptionHandler();

        try {
            if (AppPlatform.requestStart(this, getParameters()) == false) {
                // Start has been denied because another instance is running.
                Platform.exit();
            }
            // else {
            //      No other Scene Builder instance is already running.
            //      AppPlatform.requestStart() has/will invoke(d) handleLaunch().
            //      start() has now finished its job and should imply return.
            // }

        } catch (IOException x) {
            final ErrorDialog errorDialog = new ErrorDialog(null);
            errorDialog.setTitle(I18N.getString("alert.title.start"));
            errorDialog.setMessage(I18N.getString("alert.start.failure.message"));
            errorDialog.setDetails(I18N.getString("alert.start.failure.details"));
            errorDialog.setDebugInfoWithThrowable(x);
            errorDialog.showAndWait();
            Platform.exit();
        }

        logTimestamp(ACTION.START);
    }

    /*
     * AppPlatform.AppNotificationHandler
     */
    @Override
    public void handleLaunch(List<String> files) {
        var latch = new CountDownLatch(1);

        startupTasksFinished.addListener((observableValue, aBoolean, isFinished) -> {
            if (isFinished) {
                latch.countDown();
            }
        });

        boolean showWelcomeDialog = files.isEmpty();

        setApplicationUncaughtExceptionHandler();

        startInBackground("Set up Scene Builder", () -> {
            backgroundStartPhase0();
            backgroundStartPhase1();
            setUpUserLibrary(showWelcomeDialog);
            createEmptyDocumentWindow();
        });

        if (showWelcomeDialog) {
            // Unless we're on a Mac we're starting SB directly (fresh start)
            // so we're not opening any file and as such we should show the Welcome Dialog
            WelcomeDialogWindowController.getInstance().getStage().show();
        } else {
            // Open files passed as arguments by the platform

            // we need an extra thread so that synchronized AppPlatform.getUserLibraryFolder() can finish
            new Thread(
                    () -> {
                        if (!startupTasksFinished.get()) {
                            // no blocking threads, so block manually until startup tasks finish
                            try {
                                latch.await();
                            } catch (InterruptedException e) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An exception was thrown:", e);
                            }
                        }

                        Platform.runLater(() -> handleOpenFilesAction(files));
                    }
            ).start();
        }
    }

    /**
     * Creates and starts a new daemon thread with [taskName] to executive given [task].
     * The [task] is added to [startupTasks] to keep track of active background tasks.
     * When the [task] is finished, it is removed from [startupTasks].
     */
    private void startInBackground(String taskName, Runnable task) {
        var t = new Thread(() -> {
            task.run();

            startupTasks.remove(task);
        }, taskName + " Thread");
        t.setDaemon(true);
        t.start();

        startupTasks.add(task);
    }

    private void setUpUserLibrary(boolean showWelcomeDialog) {
        MavenPreferences mavenPreferences = PreferencesController.getSingleton().getMavenPreferences();
        // Creates the user library
        userLibrary = new UserLibrary(AppPlatform.getUserLibraryFolder(),
                () -> mavenPreferences.getArtifactsPathsWithDependencies(),
                () -> mavenPreferences.getArtifactsFilter());

        userLibrary.setOnUpdatedJarReports(jarReports -> {
            boolean shouldShowImportGluonJarAlert = false;
            for (JarReport jarReport : jarReports) {
                if (jarReport.hasGluonControls()) {
                    // We check if the jar has already been imported to avoid showing the import gluon jar
                    // alert every time Scene Builder starts for jars that have already been imported
                    if (!hasGluonJarBeenImported(jarReport.getJar().getFileName().toString())) {
                        shouldShowImportGluonJarAlert = true;
                    }
                }
            }
            if (shouldShowImportGluonJarAlert) {
                Platform.runLater(() -> {
                    var dwc = findFirstUnusedDocumentWindowController().orElse(makeNewWindow());
                    ImportingGluonControlsAlert alert = new ImportingGluonControlsAlert(dwc.getStage());
                    AppSettings.setWindowIcon(alert);
                    if (showWelcomeDialog) {
                        alert.initOwner(WelcomeDialogWindowController.getInstance().getStage());
                    }
                    alert.showAndWait();
                });
            }
            updateImportedGluonJars(jarReports);
        });

        userLibrary.explorationCountProperty().addListener((ChangeListener<Number>) (ov, t, t1) -> userLibraryExplorationCountDidChange());

        userLibrary.startWatching();

        sendTrackingStartupInfo();
    }

    private void sendTrackingStartupInfo() {
        PreferencesController pc = PreferencesController.getSingleton();
        PreferencesRecordGlobal recordGlobal = pc.getRecordGlobal();

        boolean sendTrackingInfo = shouldSendTrackingInfo(recordGlobal);

        if (sendTrackingInfo) {
            boolean update = false;
            String hash = recordGlobal.getRegistrationHash();
            String email = recordGlobal.getRegistrationEmail();
            boolean optIn = recordGlobal.isRegistrationOptIn();

            Tracking.sendTrackingInfo(Tracking.SCENEBUILDER_USAGE_TYPE, hash, email, optIn, update);
        }
    }

    private boolean shouldSendTrackingInfo(PreferencesRecordGlobal recordGlobal) {
        LocalDate date = recordGlobal.getLastSentTrackingInfoDate();
        boolean sendTrackingInfo = true;
        LocalDate now = LocalDate.now();

        if (date != null) {
            sendTrackingInfo = date.plusWeeks(1).isBefore(now);
            if (sendTrackingInfo) {
                recordGlobal.setLastSentTrackingInfoDate(now);
            }
        } else {
            recordGlobal.setLastSentTrackingInfoDate(now);
        }
        return sendTrackingInfo;
    }

    private void createEmptyDocumentWindow() {
        var newWindow = makeNewWindow();
        newWindow.updateWithDefaultContent();
    }

    @Override
    public void handleOpenFilesAction(List<String> files) {
        assert files != null;
        assert files.isEmpty() == false;

        final List<File> fileObjs = new ArrayList<>();
        for (String file : files) {
            fileObjs.add(new File(file));
        }

        EditorController.updateNextInitialDirectory(fileObjs.get(0));

        // Fix for #45
        if (userLibrary.isFirstExplorationCompleted()) {
            performOpenFiles(fileObjs);
        } else {
            // open files only after the first exploration has finished
            userLibrary.firstExplorationCompletedProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    if (userLibrary.isFirstExplorationCompleted()) {
                        performOpenFiles(fileObjs);
                        userLibrary.firstExplorationCompletedProperty().removeListener(this);
                    }
                }
            });
        }
    }

    @Override
    public void handleMessageBoxFailure(Exception x) {
        final ErrorDialog errorDialog = new ErrorDialog(null);
        errorDialog.setTitle(I18N.getString("alert.title.messagebox"));
        errorDialog.setMessage(I18N.getString("alert.messagebox.failure.message"));
        errorDialog.setDetails(I18N.getString("alert.messagebox.failure.details"));
        errorDialog.setDebugInfoWithThrowable(x);
        errorDialog.showAndWait();
    }

    @Override
    public void handleQuitAction() {
        /*
         * Note : this callback is called on Mac OS X only when the user
         * selects the 'Quit App' command in the Application menu.
         * 
         * Before calling this callback, FX automatically sends a close event
         * to each open window ie DocumentWindowController.performCloseAction()
         * is invoked for each open window.
         * 
         * When we arrive here, windowList is empty if the user has confirmed
         * the close operation for each window : thus exit operation can
         * be performed. If windowList is not empty,  this means the user has 
         * cancelled at least one close operation : in that case, exit operation
         * should be not be executed.
         */
        if (windowList.isEmpty()) {
            logTimestamp(ACTION.STOP);
            Platform.exit();
        }
    }

    /**
     * Normally ignored in correctly deployed JavaFX application.
     * But on Mac OS, this method seems to be called by the javafx launcher.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /*
     * Private
     */
    public DocumentWindowController makeNewWindow() {
        final DocumentWindowController result = new DocumentWindowController();

        if (Platform.isFxApplicationThread()) {
            AppSettings.setWindowIcon(result.getStage());
            windowList.add(result);
        } else {
            Platform.runLater(() -> {
                AppSettings.setWindowIcon(result.getStage());
                windowList.add(result);
            });
        }

        return result;
    }

    private void closeWindow(DocumentWindowController w) {
        assert windowList.contains(w);
        windowList.remove(w);
        w.closeWindow();
    }

    private static String displayName(String pathString) {
        return Paths.get(pathString).getFileName().toString();
    }

    /**
     * Opens a multiple-file dialog for loading projects.
     * If any files are selected, calls performOpenFiles() on them.
     */
    private void performOpenFile() {
        final FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(I18N.getString("file.filter.label.fxml"),
                "*.fxml")); //NOI18N
        fileChooser.setInitialDirectory(EditorController.getNextInitialDirectory());
        final List<File> fxmlFiles = fileChooser.showOpenMultipleDialog(null);
        if (fxmlFiles != null) {
            assert fxmlFiles.isEmpty() == false;
            EditorController.updateNextInitialDirectory(fxmlFiles.get(0));
            performOpenFiles(fxmlFiles);
        }
    }

    public void performNewTemplate(Template template) {
        var documentWC = findFirstUnusedDocumentWindowController().orElseGet(() -> {
            var w = makeNewWindow();
            w.updateWithDefaultContent();
            return w;
        });

        loadTemplateInWindow(template, documentWC);
    }

    private void performNewTemplateInNewWindow(Template template) {
        final DocumentWindowController newTemplateWindow = makeNewWindow();
        loadTemplateInWindow(template, newTemplateWindow);
    }

    private void loadTemplateInWindow(Template template, DocumentWindowController documentWindowController) {
        documentWindowController.loadFromURL(template.getFXMLURL(), template.getType() != Type.PHONE);

        if (template.getType() == Type.PHONE) {
            documentWindowController.getEditorController().performEditAction(EditorController.EditAction.SET_SIZE_335x600);
            documentWindowController.getEditorController().setTheme(EditorPlatform.Theme.GLUON_MOBILE_LIGHT);
        }

        documentWindowController.openWindow();
    }

    private void performCloseFrontWindow() {
        for (DocumentWindowController dwc : windowList) {
            if (dwc.isFrontDocumentWindow()) {
                dwc.performCloseFrontDocumentWindow();
                break;
            }
        }
    }

    public DocumentWindowController getFrontDocumentWindow() {
        for (DocumentWindowController dwc : windowList) {
            if (dwc.isFrontDocumentWindow()) {
                return dwc;
            }
        }
        return null;
    }

    private void performOpenFiles(List<File> fxmlFiles) {
        assert fxmlFiles != null;
        assert fxmlFiles.isEmpty() == false;

        final Map<File, Exception> exceptions = new HashMap<>();
        for (File fxmlFile : fxmlFiles) {
            try {
                final DocumentWindowController dwc
                        = lookupDocumentWindowControllers(fxmlFile.toURI().toURL());
                if (dwc != null) {
                    // fxmlFile is already opened
                    dwc.getStage().toFront();
                } else {
                    // Open fxmlFile
                    var hostWindow = findFirstUnusedDocumentWindowController().orElse(makeNewWindow());
                    hostWindow.loadFromFile(fxmlFile);
                    hostWindow.openWindow();
                }
            } catch (Exception xx) {
                exceptions.put(fxmlFile, xx);
            }
        }

        switch (exceptions.size()) {
            case 0: { // Good
                // Update recent items with opened files
                final PreferencesController pc = PreferencesController.getSingleton();
                pc.getRecordGlobal().addRecentItems(fxmlFiles);
                break;
            }
            case 1: {
                final File fxmlFile = exceptions.keySet().iterator().next();
                final Exception x = exceptions.get(fxmlFile);
                final ErrorDialog errorDialog = new ErrorDialog(null);
                errorDialog.setMessage(I18N.getString("alert.open.failure1.message", displayName(fxmlFile.getPath())));
                errorDialog.setDetails(I18N.getString("alert.open.failure1.details"));
                errorDialog.setDebugInfoWithThrowable(x);
                errorDialog.setTitle(I18N.getString("alert.title.open"));
                errorDialog.showAndWait();
                break;
            }
            default: {
                final ErrorDialog errorDialog = new ErrorDialog(null);
                if (exceptions.size() == fxmlFiles.size()) {
                    // Open operation has failed for all the files
                    errorDialog.setMessage(I18N.getString("alert.open.failureN.message"));
                    errorDialog.setDetails(I18N.getString("alert.open.failureN.details"));
                } else {
                    // Open operation has failed for some files
                    errorDialog.setMessage(I18N.getString("alert.open.failureMofN.message",
                            exceptions.size(), fxmlFiles.size()));
                    errorDialog.setDetails(I18N.getString("alert.open.failureMofN.details"));
                }
                errorDialog.setTitle(I18N.getString("alert.title.open"));
                errorDialog.showAndWait();
                break;
            }
        }
    }

    private void performExit() {

        // Check if an editing session is on going
        for (DocumentWindowController dwc : windowList) {
            if (dwc.getEditorController().isTextEditingSessionOnGoing()) {
                // Check if we can commit the editing session
                if (dwc.getEditorController().canGetFxmlText() == false) {
                    // Commit failed
                    return;
                }
            }
        }

        // Collects the documents with pending changes
        final List<DocumentWindowController> pendingDocs = new ArrayList<>();
        for (DocumentWindowController dwc : windowList) {
            if (dwc.isDocumentDirty()) {
                pendingDocs.add(dwc);
            }
        }

        // Notifies the user if some documents are dirty
        final boolean exitConfirmed;
        switch (pendingDocs.size()) {
            case 0: {
                exitConfirmed = true;
                break;
            }

            case 1: {
                final DocumentWindowController dwc0 = pendingDocs.get(0);
                exitConfirmed = dwc0.performCloseAction() == ActionStatus.DONE;
                break;
            }

            default: {
                assert pendingDocs.size() >= 2;

                final AlertDialog d = new AlertDialog(null);
                d.setMessage(I18N.getString("alert.review.question.message", pendingDocs.size()));
                d.setDetails(I18N.getString("alert.review.question.details"));
                d.setOKButtonTitle(I18N.getString("label.review.changes"));
                d.setActionButtonTitle(I18N.getString("label.discard.changes"));
                d.setActionButtonVisible(true);

                switch (d.showAndWait()) {
                    default:
                    case OK: { // Review
                        int i = 0;
                        ActionStatus status;
                        do {
                            status = pendingDocs.get(i++).performCloseAction();
                        } while ((status == ActionStatus.DONE) && (i < pendingDocs.size()));
                        exitConfirmed = (status == ActionStatus.DONE);
                        break;
                    }
                    case CANCEL: {
                        exitConfirmed = false;
                        break;
                    }
                    case ACTION: { // Do not review
                        exitConfirmed = true;
                        break;
                    }
                }
                break;
            }
        }

        // Exit if confirmed
        if (exitConfirmed) {
            for (DocumentWindowController dwc : new ArrayList<>(windowList)) {
                // Write to java preferences before closing
                dwc.updatePreferences();
                documentWindowRequestClose(dwc);
            }
            logTimestamp(ACTION.STOP);
            // TODO (elp): something else here ?
            Platform.exit();
        }
    }

    private enum ACTION {
        START("log.start"),
        STOP("log.stop");

        private final String logKey;

        ACTION(String logKey) {
            this.logKey = logKey;
        }
    }

    private void logTimestamp(ACTION type) {
        Logger.getLogger(this.getClass().getName()).info(I18N.getString(type.logKey));
    }

    private void setApplicationUncaughtExceptionHandler() {
        if (Thread.getDefaultUncaughtExceptionHandler() == null) {
            // Register a Default Uncaught Exception Handler for the application
            Thread.setDefaultUncaughtExceptionHandler(new SceneBuilderUncaughtExceptionHandler());
        }
    }

    private static class SceneBuilderUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            // Print the details of the exception in SceneBuilder log file
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "An exception was thrown:", e); //NOI18N
        }
    }

    private void performUseToolTheme(ToolTheme toolTheme) {
        this.toolTheme = toolTheme;

        final String toolStylesheet = getToolStylesheet();

        for (DocumentWindowController dwc : windowList) {
            dwc.setToolStylesheet(toolStylesheet);
        }
    }

    private String getToolStylesheet() {
        return ResourceUtils.getToolStylesheet(toolTheme);
    }
    
    /*
     * Background startup
     * 
     * To speed SB startup, we create two threads which anticipate some
     * initialization tasks and offload the JFX thread:
     *  - 'Phase 0' thread executes tasks that do not require JFX initialization
     *  - 'Phase 1' thread executes tasks that requires JFX initialization
     * 
     * Tasks executed here must be carefully chosen:
     * 1) they must be thread-safe
     * 2) they should be order-safe : whether they are executed in background
     *    or by the JFX thread should make no difference.
     * 
     * Currently we simply anticipate creation of big singleton instances
     * (like Metadata, Preferences...)
     */

    private void backgroundStartPhase0() {
        assert Platform.isFxApplicationThread() == false; // Warning 

        PreferencesController.getSingleton();
        Metadata.getMetadata();
    }

    private void backgroundStartPhase1() {
        assert Platform.isFxApplicationThread() == false; // Warning

        BuiltinLibrary.getLibrary();
        if (EditorPlatform.IS_MAC) {
            MenuBarController.getSystemMenuBarController();
        }
        EffectPicker.getEffectClasses();
    }

    private void userLibraryExplorationCountDidChange() {
        // We can have 0, 1 or N FXML file, same for JAR one.
        final int numOfFxmlFiles = userLibrary.getFxmlFileReports().size();
        final int numOfJarFiles = userLibrary.getJarReports().size();
        final int jarCount = userLibrary.getJarReports().size();
        final int fxmlCount = userLibrary.getFxmlFileReports().size();

        switch (numOfFxmlFiles + numOfJarFiles) {
            case 0: // Case 0-0
                final int previousNumOfJarFiles = userLibrary.getPreviousJarReports().size();
                final int previousNumOfFxmlFiles = userLibrary.getPreviousFxmlFileReports().size();
                if (previousNumOfFxmlFiles > 0 || previousNumOfJarFiles > 0) {
                    logInfoMessage("log.user.exploration.0");
                }
                break;
            case 1:
                Path path;
                if (numOfFxmlFiles == 1) { // Case 1-0
                    path = userLibrary.getFxmlFileReports().get(0);
                } else { // Case 0-1
                    path = userLibrary.getJarReports().get(0).getJar();
                }
                logInfoMessage("log.user.exploration.1", path.getFileName());
                break;
            default:
                switch (numOfFxmlFiles) {
                    case 0: // Case 0-N
                        logInfoMessage("log.user.jar.exploration.n", jarCount);
                        break;
                    case 1:
                        final Path fxmlName = userLibrary.getFxmlFileReports().get(0).getFileName();
                        if (numOfFxmlFiles == numOfJarFiles) { // Case 1-1
                            final Path jarName = userLibrary.getJarReports().get(0).getJar().getFileName();
                            logInfoMessage("log.user.fxml.jar.exploration.1.1", fxmlName, jarName);
                        } else { // Case 1-N
                            logInfoMessage("log.user.fxml.jar.exploration.1.n", fxmlName, jarCount);
                        }
                        break;
                    default:
                        switch (numOfJarFiles) {
                            case 0: // Case N-0
                                logInfoMessage("log.user.fxml.exploration.n", fxmlCount);
                                break;
                            case 1: // Case N-1
                                final Path jarName = userLibrary.getJarReports().get(0).getJar().getFileName();
                                logInfoMessage("log.user.fxml.jar.exploration.n.1", fxmlCount, jarName);
                                break;
                            default: // Case N-N
                                logInfoMessage("log.user.fxml.jar.exploration.n.n", fxmlCount, jarCount);
                                break;
                        }
                        break;
                }
                break;
        }
    }

    private void showUpdateDialogIfRequired(DocumentWindowController dwc) {
        AppSettings.getLatestVersion(latestVersion -> {
            if (latestVersion == null) {
                // This can be because the url was not reachable so we don't show the update dialog.
                return;
            }
            try {
                boolean showUpdateDialog = true;
                if (AppSettings.getSceneBuilderVersion().contains("SNAPSHOT")) {
                    showUpdateDialog = false;
                } else if (AppSettings.isCurrentVersionLowerThan(latestVersion)) {
                    PreferencesController pc = PreferencesController.getSingleton();
                    PreferencesRecordGlobal recordGlobal = pc.getRecordGlobal();

                    if (isVersionToBeIgnored(recordGlobal, latestVersion)) {
                        showUpdateDialog = false;
                    }

                    if (!isUpdateDialogDateReached(recordGlobal)) {
                        showUpdateDialog = false;
                    }
                } else {
                    showUpdateDialog = false;
                }

                if (showUpdateDialog) {
                    String latestVersionText = AppSettings.getLatestVersionText();
                    String latestVersionAnnouncementURL = AppSettings.getLatestVersionAnnouncementURL();
                    Platform.runLater(() -> {
                        UpdateSceneBuilderDialog dialog = new UpdateSceneBuilderDialog(latestVersion, latestVersionText,
                                latestVersionAnnouncementURL, dwc.getStage());
                        dialog.showAndWait();
                    });
                }
            } catch (NumberFormatException ex) {
                Platform.runLater(() -> showVersionNumberFormatError(dwc));
            }
        });
    }

    private void checkUpdates(DocumentWindowController source) {
        AppSettings.getLatestVersion(latestVersion -> {
            if (latestVersion == null) {
                Platform.runLater(() -> {
                    SBAlert alert = new SBAlert(Alert.AlertType.ERROR, getFrontDocumentWindow().getStage());
                    alert.setTitle(I18N.getString("check_for_updates.alert.error.title"));
                    alert.setHeaderText(I18N.getString("check_for_updates.alert.headertext"));
                    alert.setContentText(I18N.getString("check_for_updates.alert.error.message"));
                    alert.showAndWait();
                });
            } else {
                try {
                    if (AppSettings.isCurrentVersionLowerThan(latestVersion)) {
                        String latestVersionText = AppSettings.getLatestVersionText();
                        String latestVersionAnnouncementURL = AppSettings.getLatestVersionAnnouncementURL();
                        Platform.runLater(() -> {
                            UpdateSceneBuilderDialog dialog = new UpdateSceneBuilderDialog(latestVersion, latestVersionText,
                                    latestVersionAnnouncementURL, source.getStage());
                            dialog.showAndWait();
                        });
                    } else {
                        SBAlert alert = new SBAlert(Alert.AlertType.INFORMATION, getFrontDocumentWindow().getStage());
                        alert.setTitle(I18N.getString("check_for_updates.alert.up_to_date.title"));
                        alert.setHeaderText(I18N.getString("check_for_updates.alert.headertext"));
                        alert.setContentText(I18N.getString("check_for_updates.alert.up_to_date.message"));
                        alert.showAndWait();
                    }
                } catch (NumberFormatException ex) {
                    Platform.runLater(() -> showVersionNumberFormatError(source));
                }
            }
        });
    }

    private void showVersionNumberFormatError(DocumentWindowController dwc) {
        SBAlert alert = new SBAlert(Alert.AlertType.ERROR, dwc.getStage());
        // The version number format is not supported and this is most probably only happening
        // in development so we don't localize the strings
        alert.setTitle("Error");
        alert.setHeaderText(I18N.getString("check_for_updates.alert.headertext"));
        alert.setContentText("Update check is disabled in development environment.");
        alert.showAndWait();
    }

    private boolean isVersionToBeIgnored(PreferencesRecordGlobal recordGlobal, String latestVersion) {
        String ignoreVersion = recordGlobal.getIgnoreVersion();
        return latestVersion.equals(ignoreVersion);
    }

    private boolean isUpdateDialogDateReached(PreferencesRecordGlobal recordGlobal) {
        LocalDate dialogDate = recordGlobal.getShowUpdateDialogDate();
        if (dialogDate == null) {
            return true;
        } else if (dialogDate.isBefore(LocalDate.now())) {
            return true;
        } else {
            return false;
        }
    }

    private void logInfoMessage(String key) {
        for (DocumentWindowController dwc : windowList) {
            dwc.getEditorController().getMessageLog().logInfoMessage(key, I18N.getBundle());
        }
    }

    private void logInfoMessage(String key, Object... args) {
        for (DocumentWindowController dwc : windowList) {
            dwc.getEditorController().getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
        }
    }

    private static void updateImportedGluonJars(List<JarReport> jars) {
        PreferencesController pc = PreferencesController.getSingleton();
        PreferencesRecordGlobal recordGlobal = pc.getRecordGlobal();
        List<String> jarReportCollection = new ArrayList<>();
        for (JarReport jarReport : jars) {
            if (jarReport.hasGluonControls()) {
                jarReportCollection.add(jarReport.getJar().getFileName().toString());
            }
        }
        if (jarReportCollection.isEmpty()) {
            recordGlobal.setImportedGluonJars(new String[0]);
        } else {
            recordGlobal.setImportedGluonJars(jarReportCollection.toArray(new String[0]));
        }
    }

    private static boolean hasGluonJarBeenImported(String jar) {
        PreferencesController pc = PreferencesController.getSingleton();
        String[] importedJars = pc.getRecordGlobal().getImportedGluonJars();
        if (importedJars == null) {
            return false;
        }

        for (String importedJar : importedJars) {
            if (jar.equals(importedJar)) {
                return true;
            }
        }
        return false;
    }

    public static void applyToAllDocumentWindows(Consumer<DocumentWindowController> consumer) {
        for (DocumentWindowController dwc : getSingleton().getDocumentWindowControllers()) {
            consumer.accept(dwc);
        }
    }
}
