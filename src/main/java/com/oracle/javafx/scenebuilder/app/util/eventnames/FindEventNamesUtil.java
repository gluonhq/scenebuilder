package com.oracle.javafx.scenebuilder.app.util.eventnames;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Utility which offers a map with pairs of even type names and corresponding event names.
 * Event names can be searched for.
 */
public class FindEventNamesUtil {

    private static Map<String, String> events = new TreeMap<String, String>();

    // should be used in a static way
    private FindEventNamesUtil() {
    }

    /**
     * Initializes the internal map by loading it with data.
     */
    public static void initializeEventsMap() {
        loadEventsInMap();
    }

    /**
     * Gets the event name to the given event type name. If no event name was found, a default value (ActionEvent) is returned.
     *
     * @param eventType event type name
     * @return event name
     */
    public static String findEventName(String eventType) {
        return Optional.ofNullable(events.get(eventType)).orElse(EventNames.ACTION_EVENT);
    }

    /**
     * Loads the map with different kinds of event names and event type names.
     */
    private static void loadEventsInMap() {
        loadMainEvents();
        loadDragDropEvents();
        loadKeyboardEvents();
        loadMouseEvents();
        loadRotationEvents();
        loadSwipeEvents();
        loadTouchEvents();
        loadZoomEvents();
    }

    private static void loadMainEvents() {
        events.put(EventTypeNames.ON_ACTION, EventNames.ACTION_EVENT);
    }

    private static void loadDragDropEvents() {
        events.put(EventTypeNames.ON_DRAG_DETECTED, EventNames.MOUSE_EVENT);
        events.put(EventTypeNames.ON_DRAG_DONE, EventNames.DRAG_EVENT);
        events.put(EventTypeNames.ON_DRAG_DROPPED, EventNames.DRAG_EVENT);
        events.put(EventTypeNames.ON_DRAG_ENTERED, EventNames.DRAG_EVENT);
        events.put(EventTypeNames.ON_DRAG_EXITED, EventNames.DRAG_EVENT);
        events.put(EventTypeNames.ON_DRAG_OVER, EventNames.DRAG_EVENT);
        events.put(EventTypeNames.ON_MOUSE_DRAG_ENTERED, EventNames.MOUSE_DRAG_EVENT);
        events.put(EventTypeNames.ON_MOUSE_DRAG_EXITED, EventNames.MOUSE_DRAG_EVENT);
        events.put(EventTypeNames.ON_MOUSE_DRAG_OVER, EventNames.MOUSE_DRAG_EVENT);
        events.put(EventTypeNames.ON_MOUSE_DRAG_RELEASED, EventNames.MOUSE_DRAG_EVENT);
    }

    private static void loadKeyboardEvents() {
        events.put(EventTypeNames.ON_INPUT_METHOD_TEXT_CHANGED, EventNames.INPUT_METHOD_EVENT);
        events.put(EventTypeNames.ON_KEY_PRESSED, EventNames.KEY_EVENT);
        events.put(EventTypeNames.ON_KEY_RELEASED, EventNames.KEY_EVENT);
        events.put(EventTypeNames.ON_KEY_TYPED, EventNames.KEY_EVENT);
    }

    private static void loadMouseEvents() {
        events.put(EventTypeNames.ON_CONTEXT_MENU_REQUESTED, EventNames.CONTEXT_MENU_EVENT);
        events.put(EventTypeNames.ON_MOUSE_CLICKED, EventNames.MOUSE_EVENT);
        events.put(EventTypeNames.ON_MOUSE_DRAGGED, EventNames.MOUSE_EVENT);
        events.put(EventTypeNames.ON_MOUSE_ENTERED, EventNames.MOUSE_EVENT);
        events.put(EventTypeNames.ON_MOUSE_EXITED, EventNames.MOUSE_EVENT);
        events.put(EventTypeNames.ON_MOUSE_MOVED, EventNames.MOUSE_EVENT);
        events.put(EventTypeNames.ON_MOUSE_PRESSED, EventNames.MOUSE_EVENT);
        events.put(EventTypeNames.ON_MOUSE_RELEASED, EventNames.MOUSE_EVENT);
        events.put(EventTypeNames.ON_SCROLL, EventNames.SCROLL_EVENT);
        events.put(EventTypeNames.ON_SCROLL_STARTED, EventNames.SCROLL_EVENT);
        events.put(EventTypeNames.ON_SCROLL_FINISHED, EventNames.SCROLL_EVENT);
    }

    private static void loadRotationEvents() {
        events.put(EventTypeNames.ON_ROTATE, EventNames.ROTATE_EVENT);
        events.put(EventTypeNames.ON_ROTATION_FINISHED, EventNames.ROTATE_EVENT);
        events.put(EventTypeNames.ON_ROTATION_STARTED, EventNames.ROTATE_EVENT);
    }

    private static void loadSwipeEvents() {
        events.put(EventTypeNames.ON_SWIPE_LEFT, EventNames.SWIPE_EVENT);
        events.put(EventTypeNames.ON_SWIPE_RIGHT, EventNames.SWIPE_EVENT);
        events.put(EventTypeNames.ON_SWIPE_UP, EventNames.SWIPE_EVENT);
        events.put(EventTypeNames.ON_SWIPE_DOWN, EventNames.SWIPE_EVENT);
    }

    private static void loadTouchEvents() {
        events.put(EventTypeNames.ON_TOUCH_MOVED, EventNames.TOUCH_EVENT);
        events.put(EventTypeNames.ON_TOUCH_PRESSED, EventNames.TOUCH_EVENT);
        events.put(EventTypeNames.ON_TOUCH_RELEASED, EventNames.TOUCH_EVENT);
        events.put(EventTypeNames.ON_TOUCH_STATIONARY, EventNames.TOUCH_EVENT);
    }

    private static void loadZoomEvents() {
        events.put(EventTypeNames.ON_ZOOM, EventNames.ZOOM_EVENT);
        events.put(EventTypeNames.ON_ZOOM_STARTED, EventNames.ZOOM_EVENT);
        events.put(EventTypeNames.ON_ZOOM_FINISHED, EventNames.ZOOM_EVENT);
    }
}
