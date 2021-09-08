/*
 * Copyright (c) 2021, Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
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
package com.oracle.javafx.scenebuilder.kit.skeleton;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

final class SkeletonFileWriter {

    private final Supplier<Stage> stageSupplier;

    private final Map<SkeletonSettings.LANGUAGE, File> savedFilePerLanguage;
    private final Map<SkeletonSettings.LANGUAGE, ExtensionFilter> extensionFilterByLanguage;

    private final BiFunction<FileChooser,Stage,File> saveDialogInteraction;

    private final Function<Supplier<Stage>,Consumer<File>> onSuccess;
    private final Function<Supplier<Stage>,BiConsumer<File, Exception>> onError;

    private SkeletonSettings.LANGUAGE language;
    private URL fxmlLocation;
    private String controllerName;
    private FileChooser saveFileChooser;
    private ReadOnlyStringProperty textProperty;

    /**
     * Provides the option to save a controller skeleton to a file considering
     * language specifics.
     * 
     * @param stageSupplier As a file chooser and alerts are used, the reference to
     *                      the parent stage is required. Must not be null.
     * @param textProperty  The contents of the file is read from this property.
     *                      Must not be null.
     * 
     */
    public SkeletonFileWriter(Supplier<Stage> stageSupplier, ReadOnlyStringProperty textProperty) {
        this(stageSupplier, textProperty, (fileChooser,stage) -> fileChooser.showSaveDialog(stage),
                SkeletonFileWriterSuccessAlert::new, SkeletonFileWriterErrorAlert::new);
    }

    /**
     * This constructor only exists for the purpose of testing. It allows to replace
     * the way how the file chooser interaction is handled. Also it allows to replace
     * the GUI interaction using alerts with custom logic for testing.
     * 
     * @param stageSupplier         As a file chooser and alerts are used, the
     *                              reference to the parent stage is required. Must
     *                              not be null.
     * @param textProperty          The contents of the file is read from this
     *                              property. Must not be null.
     * @param saveDialogInteraction Allows to replace the dialog interaction with
     *                              the file chooser with custom functionality (e.g.
     *                              for testing).
     * @param onSuccessNotify       This parameter provides the notification which
     *                              is raised in case of successfully writing the
     *                              skeleton file.
     * @param onErrorNotify         This parameter provides the notification which
     *                              is raised in case of an exception during an
     *                              attempt to write the skeleton file.
     */
    protected SkeletonFileWriter(Supplier<Stage> stageSupplier, ReadOnlyStringProperty textProperty,
                                 BiFunction<FileChooser,Stage,File> saveDialogInteraction,
                                 Function<Supplier<Stage>,Consumer<File>> onSuccessNotify,
                                 Function<Supplier<Stage>,BiConsumer<File, Exception>> onErrorNotify) {
        this.stageSupplier = Objects.requireNonNull(stageSupplier);
        this.textProperty = Objects.requireNonNull(textProperty);
        this.saveDialogInteraction = Objects.requireNonNull(saveDialogInteraction);
        this.onSuccess = Objects.requireNonNull(onSuccessNotify);
        this.onError   = Objects.requireNonNull(onErrorNotify);
        this.savedFilePerLanguage = new EnumMap<>(SkeletonSettings.LANGUAGE.class);
        this.extensionFilterByLanguage = new EnumMap<>(SkeletonSettings.LANGUAGE.class);
    }


    /**
     * Prepares a language specific file name proposal for a controller skeleton
     * file. Then, a file chooser is shown to allow adjustments to the proposed file
     * name. Finally, the contents of the provided textProperty is saved to the
     * file. In case of success, a message is shown - same applies for the error
     * case.
     * 
     * The file name proposal is based on values of fxmlLocation and fxControllerName.
     * Once a filename has been defined, either by accepting the proposal or by 
     * manually overriding it in the FileChooser, the filename will remain the one
     * provided by the FileChooser. The accepted file name is remembered for each
     * available language.
     * 
     * To create a new file name proposal, the FXML file must be closed and re-opened in
     * SceneBuilder.
     * 
     * Please refer to {@link SkeletonFileNameProposal} for details.
     * 
     * 
     * @param fxmlLocation     The location of the FXML file in works. In case the
     *                         file has not been saved, this location will be null.
     * @param fxControllerName This is usually the name of the controller as defined
     *                         in the FXML file. If no controller name has been
     *                         given, this value might be null
     * @param language         The language is required in order to adjust file naming
     *                         according to language specific rules. Must not be null.
     */
    public void run(URL fxmlLocation, String fxControllerName, SkeletonSettings.LANGUAGE language) {
        this.fxmlLocation = fxmlLocation;
        this.controllerName = fxControllerName;
        this.language = Objects.requireNonNull(language);
        createFileChooserWhenNeeded();
        /*
         * TODO: Ask user if a corresponding directory src/main/java or 
         * src/main/kotlin shall be created if it does not exist.
         */
        File fileToSave = determineSaveFileName();
        updateFileChooser(fileToSave);
        saveToFileWhenConfirmed();
    }

    /**
     * This method allows to verify which files have been previously saved per language.
     *  
     * @return The map of the previously saved file per language.
     */
    Map<SkeletonSettings.LANGUAGE,File> getLastSavedFilesPerLanguage() {
        return Collections.unmodifiableMap(savedFilePerLanguage);
    }

    /**
     * @return the defined notification function for the success case.
     */
    Function<Supplier<Stage>, Consumer<File>> getOnSuccess() {
        return onSuccess;
    }

    /**
     * @return the effectively defined error handling function. 
     */
    Function<Supplier<Stage>, BiConsumer<File, Exception>> getOnError() {
        return onError;
    }

    private void saveToFileWhenConfirmed() {
        File confirmedSavedFile = saveDialogInteraction.apply(saveFileChooser, stageSupplier.get());
        if (null != confirmedSavedFile) {
            updateFileChooserAndSave(confirmedSavedFile);
        }
    }

    private File determineSaveFileName() {
        File fileToSave = savedFilePerLanguage.get(language);
        if (fileToSave == null) {
            fileToSave = new SkeletonFileNameProposal(language).create(fxmlLocation, controllerName);
        }
        return fileToSave;
    }

    private void createFileChooserWhenNeeded() {
        if (saveFileChooser == null) {
            saveFileChooser = new FileChooser(); 
            createExtensionFilters();
        }
    }

    private void createExtensionFilters() {
        for (SkeletonSettings.LANGUAGE lang : SkeletonSettings.LANGUAGE.values()) {
            ExtensionFilter filter = new ExtensionFilter(lang.toString(), "*"+lang.getExtension());
            extensionFilterByLanguage.put(lang, filter);
            saveFileChooser.getExtensionFilters().add(filter);
        }
    }

    private void updateFileChooser(File fileToSave) {
        saveFileChooser.setInitialDirectory(fileToSave.getParentFile());
        saveFileChooser.setInitialFileName(fileToSave.getName());
        saveFileChooser.setSelectedExtensionFilter(extensionFilterByLanguage.get(language));
    }

    private void updateFileChooserAndSave(File savedFile) {
        updateFileChooser(savedFile);
        rememberLastSavedFilePerLanguage(savedFile);
        saveToFile(savedFile.toPath().toAbsolutePath());
    }

    private void rememberLastSavedFilePerLanguage(File savedFile) {
        savedFilePerLanguage.put(language, savedFile);
    }

    private void saveToFile(Path skeletonFile) {
        try {
            writeSkeletonFileAndNotifyUser(skeletonFile);
        } catch (IOException error) {
            logErrorAndNotifyUser(skeletonFile, error);
        }
    }

    private void writeSkeletonFileAndNotifyUser(Path skeletonFile) throws IOException {
        String skeleton = textProperty.getValueSafe();
        OpenOption openOption = Files.exists(skeletonFile) ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.CREATE_NEW;
        Files.write(skeletonFile, skeleton.getBytes(), openOption);
        onSuccess.apply(stageSupplier)
                 .accept(skeletonFile.toFile());
    }

    private void logErrorAndNotifyUser(Path skeletonFile, IOException error) {
        onError.apply(stageSupplier)
               .accept(skeletonFile.toFile(),error);
        Logger logger = Logger.getLogger(SkeletonFileWriter.class.getSimpleName());
        String template = "Could not write controller skeleton to file: %s .";
        logger.log(Level.SEVERE, String.format(template, skeletonFile.normalize().toAbsolutePath()), error);
    }
}
