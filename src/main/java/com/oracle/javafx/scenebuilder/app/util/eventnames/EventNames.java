package com.oracle.javafx.scenebuilder.app.util.eventnames;

/**
 * Collects all the event names possible to be set in the Code section.
 */
public class EventNames {

    // Main
    public static final String ACTION_EVENT = "ActionEvent";

    // DragDrop
    public static final String MOUSE_EVENT = "MouseEvent";
    public static final String DRAG_EVENT = "DragEvent";
    public static final String MOUSE_DRAG_EVENT = "MouseDragEvent";

    // Keyboard
    public static final String INPUT_METHOD_EVENT = "InputMethodEvent";
    public static final String KEY_EVENT = "KeyEvent";

    // Mouse
    public static final String CONTEXT_MENU_EVENT = "ContextMenuEvent";
    public static final String SCROLL_EVENT = "ScrollEvent";

    // Rotation
    public static final String ROTATE_EVENT = "RotateEvent";

    // Swipe
    public static final String SWIPE_EVENT = "SwipeEvent";

    // Touch
    public static final String TOUCH_EVENT = "TouchEvent";

    // Zoom
    public static final String ZOOM_EVENT = "ZoomEvent";

    // should be used in a static way
    private EventNames() {
    }
}