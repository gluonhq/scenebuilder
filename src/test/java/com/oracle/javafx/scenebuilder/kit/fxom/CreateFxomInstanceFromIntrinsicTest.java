package com.oracle.javafx.scenebuilder.kit.fxom;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link FXOMIntrinsic#createFxomInstanceFromIntrinsic()}
 */
public class CreateFxomInstanceFromIntrinsicTest {

    private static FXOMIntrinsic fxomIntrinsic;
    private static FXOMDocument fxomDocument;

    @BeforeClass
    public static void initialize() {
        prepareTestData();
    }

    private static void prepareTestData() {
        final String sourceFile = "test_include.fxml";
        fxomDocument = new FXOMDocument();
        fxomIntrinsic = new FXOMIntrinsic(fxomDocument, FXOMIntrinsic.Type.FX_INCLUDE, sourceFile);
    }

    private FXOMInstance callService() {
        return fxomIntrinsic.createFxomInstanceFromIntrinsic();
    }

    @Test
    public void testCreateFxomInstance() {
        FXOMInstance fxomInstance = callService();
        assertThat(fxomInstance).isNotNull();
        assertThat(fxomInstance.getFxomDocument()).isEqualTo(fxomIntrinsic.getFxomDocument());
        assertThat(fxomInstance.getGlueElement()).isEqualTo(fxomIntrinsic.getGlueElement());
        assertThat(fxomInstance.getSceneGraphObject()).isEqualTo(fxomIntrinsic.getSourceSceneGraphObject());
        assertThat(fxomInstance.getDeclaredClass()).isEqualTo(fxomIntrinsic.getClass());
    }

    @Test
    public void testCreateFxomInstanceWithProperties() {
        // add at least one property (source)
        fxomIntrinsic.addIntrinsicProperty(fxomDocument);
        FXOMInstance fxomInstance = callService();
        assertThat(fxomInstance).isNotNull();
        assertThat(fxomInstance.getProperties()).isNotEmpty();
    }
}