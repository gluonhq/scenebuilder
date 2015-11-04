package com.oracle.javafx.scenebuilder.app.util.eventnames;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit test for {@link FindEventNamesUtil#findEventName(String)}
 */
public class FindEventNamesUtilTest {

    @BeforeClass
    public static void initialize() {
        FindEventNamesUtil.initializeEventsMap();
    }

    private String callService(String eventTypeName) {
        return FindEventNamesUtil.findEventName(eventTypeName);
    }

    @Test
    public void testEventNamesPositive() {
        assertThat(callService(EventTypeNames.ON_ACTION)).isEqualTo(EventNames.ACTION_EVENT);
        assertThat(callService(EventTypeNames.ON_DRAG_DETECTED)).isEqualTo(EventNames.MOUSE_EVENT);
        assertThat(callService(EventTypeNames.ON_DRAG_ENTERED)).isEqualTo(EventNames.DRAG_EVENT);
        assertThat(callService(EventTypeNames.ON_MOUSE_DRAG_ENTERED)).isEqualTo(EventNames.MOUSE_DRAG_EVENT);
        assertThat(callService(EventTypeNames.ON_INPUT_METHOD_TEXT_CHANGED)).isEqualTo(EventNames.INPUT_METHOD_EVENT);
        assertThat(callService(EventTypeNames.ON_KEY_PRESSED)).isEqualTo(EventNames.KEY_EVENT);
        assertThat(callService(EventTypeNames.ON_CONTEXT_MENU_REQUESTED)).isEqualTo(EventNames.CONTEXT_MENU_EVENT);
        assertThat(callService(EventTypeNames.ON_MOUSE_CLICKED)).isEqualTo(EventNames.MOUSE_EVENT);
        assertThat(callService(EventTypeNames.ON_SCROLL)).isEqualTo(EventNames.SCROLL_EVENT);
        assertThat(callService(EventTypeNames.ON_ROTATE)).isEqualTo(EventNames.ROTATE_EVENT);
        assertThat(callService(EventTypeNames.ON_SWIPE_UP)).isEqualTo(EventNames.SWIPE_EVENT);
        assertThat(callService(EventTypeNames.ON_TOUCH_MOVED)).isEqualTo(EventNames.TOUCH_EVENT);
        assertThat(callService(EventTypeNames.ON_ZOOM)).isEqualTo(EventNames.ZOOM_EVENT);
    }

    @Test
    public void testEventNamesNegative() {
        final String dummyEventTypeName = "onMyDummyEvent";
        final String dummyEventName = "myDummyEvent";

        assertThat(callService(EventTypeNames.ON_DRAG_DETECTED)).isNotEqualTo(EventNames.DRAG_EVENT);
        assertThat(callService(EventTypeNames.ON_DRAG_ENTERED)).isNotEqualTo(EventNames.MOUSE_DRAG_EVENT);
        assertThat(callService(EventTypeNames.ON_ZOOM)).isNotEqualTo(EventNames.SWIPE_EVENT);
        assertThat(callService(EventTypeNames.ON_ROTATE)).isNotEqualTo(EventNames.ACTION_EVENT);

        assertThat(callService(dummyEventTypeName)).isNotEqualTo(EventNames.ACTION_EVENT);
        assertThat(callService(dummyEventTypeName)).isNull();
        assertThat(callService(EventTypeNames.ON_ROTATION_FINISHED)).isNotEqualTo(dummyEventName);
    }
}
