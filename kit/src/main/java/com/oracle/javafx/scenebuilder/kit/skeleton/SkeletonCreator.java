package com.oracle.javafx.scenebuilder.kit.skeleton;

class SkeletonCreator {

    /**
     * @return a code skeleton for the given context
     */
    static String createFrom(SkeletonContext context) {
        switch (context.getSettings().getLanguage()) {
            case JAVA:
                return SkeletonCreatorJava.createFrom(context);
            case KOTLIN:
                return SkeletonCreatorKotlin.createFrom(context);
            default:
                throw new IllegalArgumentException("Language not supported: " + context.getSettings().getLanguage());
        }
    }
}
