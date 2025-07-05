/*
 * Copyright (c) 2020, Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.kit.fxom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;

import javafx.fxml.FXMLLoader;

/**
 *
 */
public class BindingExpressionDisabler {

    public static void disable(FXOMDocument fxomDocument) {
        assert fxomDocument != null;
        
        final List<FXOMObject> candidates = new ArrayList<>();
        if (fxomDocument.getFxomRoot() != null) {
            candidates.add(fxomDocument.getFxomRoot());
        }
        
        while (candidates.isEmpty() == false) {
            final FXOMObject candidate = candidates.get(0);
            candidates.remove(0);
            
            if (candidate instanceof FXOMInstance) {
            	final FXOMInstance inst = (FXOMInstance)candidate;
            	final Object sceneGraphObject = inst.getSceneGraphObject();

                for (Map.Entry<PropertyName, FXOMProperty> e:inst.getProperties().entrySet()) {
                	FXOMProperty property = e.getValue();
                	PropertyName propertyName = e.getKey();
                	
                	if (property instanceof FXOMPropertyT) {
                		FXOMPropertyT propertyT = (FXOMPropertyT)property;
                		if (propertyT.getValue().startsWith(FXMLLoader.BINDING_EXPRESSION_PREFIX)) {
                			try {
								propertyName.setValue(sceneGraphObject, propertyT.getValue());
							} catch (Exception ex) {
								// Let the exception be, the binding expression can't be escaped 
								// due to the property type not accepting string value
								// catching the exception allow the process to go on
							}
                		}
                	}
                }
            }
                        
            candidates.addAll(candidate.getChildObjects());
        }
    }
}
