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
package com.oracle.javafx.scenebuilder.app.registration;

import com.oracle.javafx.scenebuilder.app.i18n.I18N;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesController;
import com.oracle.javafx.scenebuilder.app.preferences.PreferencesRecordGlobal;
import com.oracle.javafx.scenebuilder.app.tracking.Tracking;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 *
 */
public class RegistrationWindowController extends AbstractFxmlWindowController {

    private static final Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\\-_\"]]+\\.[a-zA-Z0-9[!#$%&'()*+,/\\-_\"\\.]]+"); //NOI18N

    @FXML
    private Label lbAlert;
    @FXML
    private TextField tfEmail;
    @FXML
    private CheckBox cbOptIn;

    final private Window owner;

    public RegistrationWindowController(Stage owner) {
        super(RegistrationWindowController.class.getResource("Registration.fxml"), //NOI18N
                I18N.getBundle(), owner);
        this.owner = owner;
    }

    @Override
    public void onCloseRequest(WindowEvent event) {
        cancelUserRegistration();

        event.consume();
    }

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() != null;

        getStage().setTitle(I18N.getString("registration.title"));

        if (this.owner == null) {
            // Window will be application modal
            getStage().initModality(Modality.APPLICATION_MODAL);
        } else {
            // Window will be window modal
            getStage().initOwner(this.owner);
            getStage().initModality(Modality.WINDOW_MODAL);
        }
    }

    @Override
    protected void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert lbAlert != null;
        assert tfEmail != null;
        assert cbOptIn != null;
    }

    private boolean isEmailAddressValid() {
        String email = tfEmail.getText();
        return email != null && !email.isEmpty() && emailPattern.matcher(email).matches();
    }

    @FXML
    public void cancelUserRegistration() {
        PreferencesController pc = PreferencesController.getSingleton();
        PreferencesRecordGlobal recordGlobal = pc.getRecordGlobal();
        if (recordGlobal.getRegistrationHash() == null) {
            String hash = getUniqueId();
            recordGlobal.updateRegistrationFields(hash, null, null);
            Tracking.sendTrackingInfo(Tracking.SCENEBUILDER_TYPE, hash, "", false, false);
        }

        closeWindow();
    }
    
    @FXML
    public void trackUserRegistration() {
        if (!isEmailAddressValid()) {
            lbAlert.setVisible(true);
            return;
        }

        PreferencesController pc = PreferencesController.getSingleton();
        PreferencesRecordGlobal recordGlobal = pc.getRecordGlobal();
        
        boolean update = recordGlobal.getRegistrationHash() != null;
        String hash = update ? recordGlobal.getRegistrationHash() : getUniqueId();
        String email = tfEmail.getText();
        boolean optIn = cbOptIn.isSelected();
                
        // Update preferences
        recordGlobal.updateRegistrationFields(hash, email, optIn);

        Tracking.sendTrackingInfo(Tracking.SCENEBUILDER_TYPE, hash, email, optIn, update);

        closeWindow();
    }

    private String getUniqueId(){
        String uniqueId = "";
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            if (ni != null) {
                byte[] macAddress = ni.getHardwareAddress();
                if (macAddress != null) {
                    uniqueId = computeHash(macAddress);
                }
            }
        } catch (UnknownHostException | SocketException e) {
            // Intentionally blank catch
        }

        if (uniqueId.isEmpty()) {
            uniqueId = UUID.randomUUID().toString();
        }

        return uniqueId;
    }

    private String computeHash(byte[] buffer) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

            messageDigest.reset();
            messageDigest.update(buffer);
            byte[] digest = messageDigest.digest();

            // Convert the byte to hex format
            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
            }

            return hexStr;
        } catch (NoSuchAlgorithmException e) {
            // Intentionally blank catch
        }

        return "";
    }
}
