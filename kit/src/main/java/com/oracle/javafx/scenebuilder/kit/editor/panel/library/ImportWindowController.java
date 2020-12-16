/*
 * Copyright (c) 2017, 2020, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.editor.panel.library;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.kit.alert.ImportingGluonControlsAlert;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.AbstractModalDialog;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.ErrorDialog;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.i18n.I18N;
import com.oracle.javafx.scenebuilder.kit.library.BuiltinLibrary;
import com.oracle.javafx.scenebuilder.kit.library.user.UserLibrary;
import com.oracle.javafx.scenebuilder.kit.library.util.FolderExplorer;
import com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer;
import com.oracle.javafx.scenebuilder.kit.library.util.JarReport;
import com.oracle.javafx.scenebuilder.kit.library.util.JarReportEntry;
import com.oracle.javafx.scenebuilder.kit.preferences.MavenPreferences;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 *
 */
public class ImportWindowController extends AbstractModalDialog {

    private static final Logger LOGGER = Logger.getLogger(ImportWindowController.class.getSimpleName());

    public enum PrefSize {

        DEFAULT, TWO_HUNDRED_BY_ONE_HUNDRED, TWO_HUNDRED_BY_TWO_HUNDRED
    }

    final List<File> importFiles;
    private final LibraryPanelController libPanelController;
    Task<List<JarReport>> exploringTask = null;
    URLClassLoader importClassLoader;
    Node zeNode = new Label(I18N.getString("import.preview.unable"));
    double builtinPrefWidth;
    double builtinPrefHeight;
    private int numOfImportedJar;
    private boolean copyFilesToUserLibraryDir;
    private Stage owner;
    
    // At first we put in this collection the items which are already excluded,
    // basically all which are listed in the filter file.
    // When constructing the list of items discovered in new jar file being imported
    // we uncheck already excluded items and remove them from the collection.
    // When the user clicks the Import button the collection might contain the
    // items we retain from older import actions.
    private List<String> alreadyExcludedItems = new ArrayList<>();
    private final List<String> artifactsFilter;

    private final MavenPreferences mavenPreferences;
    
    @FXML
    private VBox leftHandSidePart;

    @FXML
    private Label processingLabel;

    @FXML
    ProgressIndicator processingProgressIndicator;

    @FXML
    ListView<ImportRow> importList = new ListView<>();

    @FXML
    ChoiceBox<String> defSizeChoice;
    
    @FXML
    private Label sizeLabel;

    @FXML
    private SplitPane topSplitPane;
    
    @FXML
    Group previewGroup;
    
    @FXML
    Label numOfItemsLabel;
    
    @FXML
    Label classNameLabel;
    
    @FXML
    Label previewHintLabel;
    
    @FXML
    ToggleButton checkAllUncheckAllToggle;

    
    public ImportWindowController(LibraryPanelController lpc, List<File> files, MavenPreferences mavenPreferences, Stage owner) {
        this(lpc, files, mavenPreferences, owner, true, new ArrayList<>());
    }
    
    public ImportWindowController(LibraryPanelController lpc, List<File> files, MavenPreferences mavenPreferences, Stage owner,
            boolean copyFilesToUserLibraryDir, List<String> artifactsFilter) {
        super(ImportWindowController.class.getResource("ImportDialog.fxml"), I18N.getBundle(), owner); //NOI18N
        libPanelController = lpc;
        importFiles = new ArrayList<>(files);
        this.copyFilesToUserLibraryDir = copyFilesToUserLibraryDir;
        this.artifactsFilter = artifactsFilter;
        this.owner = owner;
        this.mavenPreferences = mavenPreferences;
    }

    /*
     * Event handlers
     */
    /* TO BE SOLVED
     We have an issue with the exploration of SOME jar files.
     If e.g. you use sa-jdi.jar (take it in the JRE or JDK tree) then a NPE as
     the one below will be printed but cannot be caught in the code of this class.
     And from there we won't be able to exit from SB, whatever the action we take
     on the import window (Cancel or Import).
     Yes the window goes away but some thread refuse to give up.
     I noticed two non daemon threads:
     AWT-EventQueue-0
     AWT-Shutdown
    
     java.lang.NullPointerException
     at java.util.StringTokenizer.<init>(StringTokenizer.java:199)
     at java.util.StringTokenizer.<init>(StringTokenizer.java:221)
     at sun.jvm.hotspot.tools.jcore.PackageNameFilter.<init>(PackageNameFilter.java:41)
     at sun.jvm.hotspot.tools.jcore.PackageNameFilter.<init>(PackageNameFilter.java:36)
     at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
     at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)
     at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
     at java.lang.reflect.Constructor.newInstance(Constructor.java:414)
     at java.lang.Class.newInstance(Class.java:444)
     at sun.reflect.misc.ReflectUtil.newInstance(ReflectUtil.java:47)
     at javafx.fxml.FXMLLoader$InstanceDeclarationElement.constructValue(FXMLLoader.java:883)
     at javafx.fxml.FXMLLoader$ValueElement.processStartElement(FXMLLoader.java:614)
     at javafx.fxml.FXMLLoader.processStartElement(FXMLLoader.java:2491)
     at javafx.fxml.FXMLLoader.load(FXMLLoader.java:2300)
     at com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer.instantiateWithFXMLLoader(JarExplorer.java:83)
     at com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer.exploreEntry(JarExplorer.java:117)
     at com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer.explore(JarExplorer.java:43)
     at com.oracle.javafx.scenebuilder.kit.editor.panel.library.ImportWindowController$2.call(ImportWindowController.java:155)
     at com.oracle.javafx.scenebuilder.kit.editor.panel.library.ImportWindowController$2.call(ImportWindowController.java:138)
     at javafx.concurrent.Task$TaskCallable.call(Task.java:1376)
     at java.util.concurrent.FutureTask.run(FutureTask.java:262)
     at java.lang.Thread.run(Thread.java:724)
     */
    @Override
    protected void cancelButtonPressed(ActionEvent e) {
        if (exploringTask != null && exploringTask.isRunning()) {
            exploringTask.setOnCancelled(t -> getStage().close());
            exploringTask.cancel(true);
        } else {
            getStage().close();
        }
        
        exploringTask = null;
        
        try {
            closeClassLoader();
        } catch (IOException ex) {
            showErrorDialog(ex);
        }
    }

    @Override
    protected void okButtonPressed(ActionEvent e) {
        exploringTask = null;
        getStage().close();
        
        try {
            closeClassLoader();
            
            UserLibrary userLib = ((UserLibrary) libPanelController.getEditorController().getLibrary());

            if (copyFilesToUserLibraryDir) {
                // collect directories from importFiles and add to library.folders file
                // for other filex (jar, fxml) copy them directly
                List<File> folders = new ArrayList<>(importFiles.size());
                List<File> files = new ArrayList<>(importFiles.size());

                for (File file : importFiles) {
                    if (file.isDirectory())
                        folders.add(file);
                    else
                        files.add(file);
                }

                if (!files.isEmpty())
                    libPanelController.copyFilesToUserLibraryDir(files);

                Path foldersMarkerPath = Paths.get(userLib.getPath().toString(), LibraryUtil.FOLDERS_LIBRARY_FILENAME);

                if (!Files.exists(foldersMarkerPath))
                    Files.createFile(foldersMarkerPath);

                Set<String> lines = new TreeSet<>(Files.readAllLines(foldersMarkerPath));
                lines.addAll(folders.stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()));

                Files.write(foldersMarkerPath, lines);
            }

            if (copyFilesToUserLibraryDir) {
                userLib.setFilter(getExcludedItems());
            }
        } catch (IOException ex) {
            showErrorDialog(ex);
        } finally {
            alreadyExcludedItems.clear();
        }
    }

    @Override
    protected void actionButtonPressed(ActionEvent e) {
        // NOTHING TO DO (no ACTION button)
    }

    /*
     * AbstractFxmlWindowController
     */
    @Override
    public void onCloseRequest(WindowEvent event) {
        cancelButtonPressed(null);
    }

    @Override
    public void controllerDidLoadContentFxml() {
        assert topSplitPane != null;
        // The SplitPane should not be visible from the beginning: only the progressing bar is initially visible.
        assert topSplitPane.isVisible() == false;
        assert processingLabel != null;
        assert processingProgressIndicator != null;
        assert sizeLabel != null;
        assert previewGroup != null;
        assert importList != null;
        assert defSizeChoice != null;
        assert numOfItemsLabel != null;
        assert leftHandSidePart != null;
        assert classNameLabel != null;
        assert previewHintLabel != null;
        assert checkAllUncheckAllToggle != null;
        
        // Setup dialog buttons
        setOKButtonVisible(true);
        setDefaultButtonID(ButtonID.OK);
        setShowDefaultButton(true);
        
        // Setup size choice box
        defSizeChoice.getItems().clear();
        // Care to have values in sync with definition of PrefSize
        defSizeChoice.getItems().addAll(I18N.getString("import.choice.builtin"),
                "200 x 100", "200 x 200"); //NOI18N
        defSizeChoice.getSelectionModel().selectFirst();
        defSizeChoice.getSelectionModel().selectedIndexProperty().addListener((ChangeListener<Number>) (ov, t, t1) -> {
            assert t1 instanceof Integer;
            updateSize((Integer)t1);
        });

        // Setup Select All / Unselect All toggle
        // Initially all items are Selected.
        checkAllUncheckAllToggle.selectedProperty().addListener((ChangeListener<Boolean>) (ov, t, t1) -> {
            if (t1) {
                for (ImportRow row1 : importList.getItems()) {
                    row1.setImportRequired(false);
                }
                checkAllUncheckAllToggle.setText(I18N.getString("import.toggle.checkall"));
            } else {
                for (ImportRow row2 : importList.getItems()) {
                    row2.setImportRequired(true);
                }
                checkAllUncheckAllToggle.setText(I18N.getString("import.toggle.uncheckall"));
            }
        });
                
        setProcessing();
        
        // We do not want the list becomes larger when the window is made larger.
        // The way to make the list larger is to use the splitter.
        SplitPane.setResizableWithParent(leftHandSidePart, false);
        
        work();
    }

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        super.controllerDidCreateStage();
        getStage().setTitle(I18N.getString("import.window.title"));
    }

    /*
     * Private
     */
    
    private void closeClassLoader() throws IOException {
        if (importClassLoader != null) {
            importClassLoader.close();
        }
    }

    // This method returns a new list of File made of the union of the provided
    // one and jar files found in the user library dir.
    List<File> buildListOfAllFiles(List<File> importFiles) throws IOException {
        final List<File> res = new ArrayList<>(importFiles);
        String userLibraryDir = ((UserLibrary) libPanelController.getEditorController().getLibrary()).getPath();
        if (new File(userLibraryDir).exists()) {
            Path userLibraryPath = new File(userLibraryDir).toPath();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(userLibraryPath)) {
                for (Path entry : stream) {
                    if (entry.toString().endsWith(".jar")) { //NOI18N
    //                    System.out.println("ImportWindowController::buildListOfAllFiles: Adding " + element); //NOI18N
                        res.add(entry.toFile());
                    }
                }
            }
        }
        // add artifacts jars (main and dependencies)
        res.addAll(mavenPreferences.getArtifactsFilesWithDependencies());
        
        return res;
    }

    private void work() {
        exploringTask = new Task<List<JarReport>>() {

            @Override
            protected List<JarReport> call() throws Exception {
                final List<JarReport> res = new ArrayList<>();
                numOfImportedJar = importFiles.size();
                // The classloader takes in addition all already existing
                // jar files stored in the user lib dir.
                final List<File> allFiles = buildListOfAllFiles(importFiles);
                final URLClassLoader classLoader = getClassLoaderForFiles(allFiles);
                int index = 1;
                for (File file : importFiles) {
                    if (isCancelled()) {
                        updateMessage(I18N.getString("import.work.cancelled"));
                        break;
                    }
                    updateMessage(I18N.getString("import.work.exploring", file.getName()));
//                    System.out.println("[" + index + "/" + max + "] Exploring file " + file.getName()); //NOI18N
                    if (file.isDirectory()) {
                        final FolderExplorer explorer = new FolderExplorer(file.toPath());
                        final JarReport jarReport = explorer.explore(classLoader);
                        res.add(jarReport);
                    }
                    else {
                        final JarExplorer explorer = new JarExplorer(Paths.get(file.getAbsolutePath()));
                        final JarReport jarReport = explorer.explore(classLoader);
                        res.add(jarReport);
                    }
                    updateProgress(index, numOfImportedJar);
                    index++;
                }

                updateProgress(numOfImportedJar, numOfImportedJar);
                updateImportClassLoader(classLoader);
                return res;
            }
        };

        Thread th = new Thread(exploringTask);
        th.setDaemon(true);
        processingProgressIndicator.progressProperty().bind(exploringTask.progressProperty());

        // We typically enter this handler when dropping jar files such as
        // rt.jar from Java Runtime.
        exploringTask.setOnFailed(t -> {
            // See in setOnSucceeded the explanation for the toFront below.
            getStage().toFront();
            updateNumOfItemsLabelAndSelectionToggleState();
        });
        
        // We construct the import list only if exploration of jar files does well.
        // If Cancel is called during the construction of the list then the import
        // window is closed but the construction itself will continue up to the
        // end. Do we want to make it interruptible ?
        exploringTask.setOnSucceeded(t -> {
            assert Platform.isFxApplicationThread();
            // This toFront() might not be necessary because import window is modal
            // and is chained to the document window. Anyway experience showed
            // we need it (FX 8 b106). This is suspicious, to be investigated ...
            // But more tricky is why toFront() is called here. Mind that when toFront()
            // is called while isShowing() returns false isn't effective: that's
            // why toFront called at the end of controllerDidCreateStage() or
            // controllerDidLoadContentFxml() wasn't an option. Below is the
            // earliest place it has been proven effective, at least on my machine.
            getStage().toFront();
            
            try {
                // We get the set of items which are already excluded prior to the current import.
                UserLibrary userLib = ((UserLibrary) libPanelController.getEditorController().getLibrary());
                alreadyExcludedItems = userLib.getFilter();
                
                List<JarReport> jarReportList = exploringTask.get(); // blocking call
                final Callback<ImportRow, ObservableValue<Boolean>> importRequired
                        = row -> row.importRequired();
                importList.setCellFactory(CheckBoxListCell.forListView(importRequired));

                boolean importingGluonControls = false;
                for (JarReport jarReport : jarReportList) {
                    Path file = jarReport.getJar();
                    String jarName = file.getName(file.getNameCount() - 1).toString();
                    StringBuilder sb = new StringBuilder(
                            I18N.getString("log.info.explore." + (Files.isDirectory(file) ? "folder" : "jar") + ".results", jarName))
                            .append("\n");
                    for (JarReportEntry e : jarReport.getEntries()) {
                        sb.append("> ").append(e.toString()).append("\n");
                        if ((e.getStatus() == JarReportEntry.Status.OK) && e.isNode()) {
                            boolean checked = true;
                            final String canonicalName = e.getKlass().getCanonicalName();
                            // If the class we import is already listed as an excluded one
                            // then it must appear unchecked in the list.
                            if (alreadyExcludedItems.contains(canonicalName) || 
                                    artifactsFilter.contains(canonicalName)) {
                                checked = false;
                                if (alreadyExcludedItems.contains(canonicalName)) {
                                    alreadyExcludedItems.remove(canonicalName);
                                }
                            }
                            final ImportRow importRow = new ImportRow(checked, e, null);
                            importList.getItems().add(importRow);
                            importRow.importRequired().addListener((ChangeListener<Boolean>) (ov, oldValue,
                                    newValue) -> {
                                        final int numOfComponentToImport = getNumOfComponentToImport(importList);
                                        updateOKButtonTitle(numOfComponentToImport);
                                        updateSelectionToggleText(numOfComponentToImport);
                                    });
                        } else {
                            if (e.getException() != null) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                e.getException().printStackTrace(pw);
                                sb.append(">> " + sw.toString());
                            }
                        }
                    }
                    LOGGER.info(sb.toString());

                    if (jarReport.hasGluonControls()) {
                        importingGluonControls = true;
                    }
                }

                if (importingGluonControls) {
                    ImportingGluonControlsAlert alert = new ImportingGluonControlsAlert(owner);
                    alert.showAndWait();
                }

                // Sort based on the simple class name.
                Collections.sort(importList.getItems(), new ImportRowComparator());

                final int numOfComponentToImport = getNumOfComponentToImport(importList);
                updateOKButtonTitle(numOfComponentToImport);
                updateOKCancelDefaultState(numOfComponentToImport);
                updateSelectionToggleText(numOfComponentToImport);
                updateNumOfItemsLabelAndSelectionToggleState();
            } catch (InterruptedException | ExecutionException | IOException ex) {
                getStage().close();
                showErrorDialog(ex);
            }

            unsetProcessing();
        });

        th.start();
    }
    
    private void showErrorDialog(Exception exception) {
        final ErrorDialog errorDialog = new ErrorDialog(null);
        errorDialog.setTitle(I18N.getString("import.error.title"));
        errorDialog.setMessage(I18N.getString("import.error.message"));
        errorDialog.setDetails(I18N.getString("import.error.details"));
        errorDialog.setDebugInfoWithThrowable(exception);
        errorDialog.showAndWait();
    }

    void updateImportClassLoader(URLClassLoader cl) {
        this.importClassLoader = cl;
    }

    void unsetProcessing() {
        processingProgressIndicator.setVisible(false);
        processingLabel.setVisible(false);
        topSplitPane.setVisible(true);

        importList.getSelectionModel().selectedItemProperty().addListener((ChangeListener<ImportRow>) (ov, t, t1) -> {
            previewGroup.getChildren().clear();
            final String fxmlText = BuiltinLibrary.makeFxmlText(t1.getJarReportEntry().getKlass());
            try {
                FXOMDocument fxomDoc = new FXOMDocument(fxmlText, null, importClassLoader, null);
                zeNode = (Node) fxomDoc.getSceneGraphRoot();
            } catch (IOException ioe) {
                showErrorDialog(ioe);
            }
            
            // In order to get valid bounds I need to put the node into a
            // scene and ask for full layout.
            try {
                final Group visualGroup = new Group(zeNode);
                final Scene hiddenScene = new Scene(visualGroup);
                Stage hiddenStage = new Stage();
                hiddenStage.setScene(hiddenScene);
                visualGroup.applyCss();
                visualGroup.layout();
                final Bounds zeBounds = zeNode.getLayoutBounds();
                builtinPrefWidth = zeBounds.getWidth();
                builtinPrefHeight = zeBounds.getHeight();
                // Detach the scene !
                hiddenScene.setRoot(new Group());
                hiddenStage.close();
            } catch (Error e) {
                // Experience shows that with rogue jar files (a jar file
                // unlikely to contain FX controls) we can enter here.
                // Anything better to do than setting pref size to 0 ?
                builtinPrefWidth = 0;
                builtinPrefHeight = 0;
            }
            
            if (builtinPrefWidth == 0 || builtinPrefHeight == 0) {
                if (zeNode instanceof Region) { // must check instanceof: custom components are not necessarily regions..
                    ((Region) zeNode).setPrefSize(200, 200);
                    setSizeLabel(PrefSize.TWO_HUNDRED_BY_TWO_HUNDRED);
                    defSizeChoice.getSelectionModel().select(2);
                }
            } else {
                setSizeLabel(PrefSize.DEFAULT);
                defSizeChoice.getSelectionModel().selectFirst();
            }
            previewGroup.getChildren().add(zeNode);
            defSizeChoice.setDisable(false);
            classNameLabel.setText(t1.getJarReportEntry().getKlass().getName());
        });

        // We avoid to get an empty Preview area at first.
        if (importList.getItems().size() > 0) {
            importList.getSelectionModel().selectFirst();
        }
    }

    private URLClassLoader getClassLoaderForFiles(List<File> files) {
        return new URLClassLoader(makeURLArrayFromFiles(files));
    }

    private URL[] makeURLArrayFromFiles(List<File> files) {
        final URL[] result = new URL[files.size()];
        try {
            int index = 0;
            for (File file : files) {
                URL url = file.toURI().toURL();
                if (url.toString().endsWith(".jar")) {
                    result[index] = new URL("jar", "", url + "!/"); // <-- jar:file/path/to/jar!/
                } else {
                    result[index] = url; // <-- file:/path/to/folder/
                }

                index++;
            }
        } catch (MalformedURLException x) {
            throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); //NOI18N
        }

        return result;
    }

    private void setProcessing() {
        cancelButton.setDefaultButton(true);
    }

    private int getNumOfComponentToImport(final ListView<ImportRow> list) {
        int res = 0;
        
        for (final ImportRow row : list.getItems()) {
            if (row.isImportRequired()) {
                res++;
            }
        }
        
        return res;
    }
    
    private List<String> getExcludedItems() {
        List<String> res = new ArrayList<>(alreadyExcludedItems);
        
        for (ImportRow row : importList.getItems()) {
            if (! row.isImportRequired()) {
                res.add(row.getCanonicalClassName());
            }
        }
        return res;
    }

    public String getNewExcludedItems() {
        return importList.getItems()
                .stream()
                .filter(r -> !r.isImportRequired())
                .map(ImportRow::getCanonicalClassName)
                .collect(Collectors.joining(File.pathSeparator));
    }
    
    // The title of the button is important in the sense it says to the user
    // what action will be taken.
    // In the most common case one or more component are selected in the list,
    // but it is also possible to get an empty list, in which case the user may
    // want to import the jar file anyway; it makes sense in ooder to resolve
    // dependencies other jars have onto it.
    // See DTL-6531 for details.
    private void updateOKButtonTitle(int numOfComponentToImport) {
        if (numOfComponentToImport == 0) {
            if (numOfImportedJar == 1) {
                setOKButtonTitle(I18N.getString("import.button.import.jar"));
            } else {
                setOKButtonTitle(I18N.getString("import.button.import.jars"));
            }
        } else if (numOfComponentToImport == 1) {
            setOKButtonTitle(I18N.getString("import.button.import.component"));
        } else {
            setOKButtonTitle(I18N.getString("import.button.import.components"));
        }
    }

    private void updateOKCancelDefaultState(int numOfComponentsToImport) {
        if (numOfComponentsToImport == 0) {
            cancelButton.setDefaultButton(true);
            cancelButton.requestFocus();
        } else {
            okButton.setDefaultButton(true);
            okButton.requestFocus();
        }
    }
    
    void updateNumOfItemsLabelAndSelectionToggleState() {
        final int num = importList.getItems().size();
        if (num == 0 || num == 1) {
            numOfItemsLabel.setText(num + " " //NOI18N
                    + I18N.getString("import.num.item"));
        } else {
            numOfItemsLabel.setText(num + " " //NOI18N
                    + I18N.getString("import.num.items"));
        }
        
        if (num >= 1) {
            checkAllUncheckAllToggle.setDisable(false);
        }
    }

    private void updateSelectionToggleText(int numOfComponentToImport) {
        if (numOfComponentToImport == 0) {
            checkAllUncheckAllToggle.setText(I18N.getString("import.toggle.checkall"));
        } else {
            checkAllUncheckAllToggle.setText(I18N.getString("import.toggle.uncheckall"));
        }
    }
        
    // NOTE At the end of the day some tooling in metadata will supersedes the
    // use of this method that is only able to deal with a Region, ignoring all
    // other cases.
    private void updateSize(Integer choice) {
        if (zeNode instanceof Region) {
            PrefSize prefSize = PrefSize.values()[choice];
            switch (prefSize) {
                case DEFAULT:
                    ((Region) zeNode).setPrefSize(builtinPrefWidth, builtinPrefHeight);
                    setSizeLabel(prefSize);
                    break;
                case TWO_HUNDRED_BY_ONE_HUNDRED:
                    ((Region) zeNode).setPrefSize(200, 100);
                    setSizeLabel(prefSize);
                    break;
                case TWO_HUNDRED_BY_TWO_HUNDRED:
                    ((Region) zeNode).setPrefSize(200, 200);
                    setSizeLabel(prefSize);
                    break;
                default:
                    break;
            }
            
            defSizeChoice.getSelectionModel().select(choice);
        }
    }
    
    private void setSizeLabel(PrefSize ps) {
        switch (ps) {
            case DEFAULT:
                sizeLabel.setText(builtinPrefWidth + " x " + builtinPrefHeight); //NOI18N
                break;
            case TWO_HUNDRED_BY_ONE_HUNDRED:
                sizeLabel.setText("200 x 100"); //NOI18N
                break;
            case TWO_HUNDRED_BY_TWO_HUNDRED:
                sizeLabel.setText("200 x 200"); //NOI18N
                break;
            default:
                break;
        }
    }
}
