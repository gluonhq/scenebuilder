/*
 * Copyright (c) 2021, 2023, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.skeleton;

class SkeletonSettings {

    private LANGUAGE language = LANGUAGE.JAVA;
    private TEXT_TYPE textType = TEXT_TYPE.WITHOUT_COMMENTS;
    private FORMAT_TYPE textFormat = FORMAT_TYPE.COMPACT;

    enum LANGUAGE {
        JAVA("Java", ".java"), KOTLIN("Kotlin", ".kt"), JRUBY("JRuby", ".rb");

        private final String name;
        private final String ext;

        LANGUAGE(String name, String fileNameExt) {
            this.name = name;
            this.ext = fileNameExt;
        }

        @Override
        public String toString() {
            return name;
        }

        String getExtension() {
            return ext;
        }
    }

    enum TEXT_TYPE {
        WITH_COMMENTS, WITHOUT_COMMENTS
    }

    enum FORMAT_TYPE {
        COMPACT, FULL
    }

    void setLanguage(LANGUAGE language) {
        this.language = language;
    }

    public LANGUAGE getLanguage() {
        return language;
    }

    void setTextType(TEXT_TYPE type) {
        this.textType = type;
    }

    SkeletonSettings withTextType(TEXT_TYPE type) {
        this.textType = type;
        return this;
    }

    void setFormat(FORMAT_TYPE format) {
        this.textFormat = format;
    }

    SkeletonSettings withFormat(FORMAT_TYPE format) {
        this.textFormat = format;
        return this;
    }

    boolean isWithComments() {
        return textType == TEXT_TYPE.WITH_COMMENTS;
    }

    boolean isFull() {
        return textFormat == FORMAT_TYPE.FULL;
    }
}
