package com.oracle.javafx.scenebuilder.kit.skeleton;

class SkeletonSettings {

    private LANGUAGE language = LANGUAGE.JAVA;
    private TEXT_TYPE textType = TEXT_TYPE.WITHOUT_COMMENTS;
    private FORMAT_TYPE textFormat = FORMAT_TYPE.COMPACT;

    enum LANGUAGE {
        JAVA("Java"), KOTLIN("Kotlin");

        private final String name;

        LANGUAGE(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
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
