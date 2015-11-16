package com.oracle.javafx.scenebuilder.app.util.eventnames;

/**
 * Collects all the event type names possible to be set in the Code section.
 */
public class EventTypeNames {

    // Main
    public static final String ON_ACTION = "onAction";

    // DragDrop
    public static final String ON_DRAG_DETECTED = "onDragDetected";
    public static final String ON_DRAG_DONE = "onDragDone";
    public static final String ON_DRAG_DROPPED = "onDragDropped";
    public static final String ON_DRAG_ENTERED = "onDragEntered";
    public static final String ON_DRAG_EXITED = "onDragExited";
    public static final String ON_DRAG_OVER = "onDragOver";
    public static final String ON_MOUSE_DRAG_ENTERED = "onMouseDragEntered";
    public static final String ON_MOUSE_DRAG_EXITED = "onMouseDragExited";
    public static final String ON_MOUSE_DRAG_OVER = "onMouseDragOver";
    public static final String ON_MOUSE_DRAG_RELEASED = "onMouseDragReleased";

    // Keyboard
    public static final String ON_INPUT_METHOD_TEXT_CHANGED = "onInputMethodTextChanged";
    public static final String ON_KEY_PRESSED = "onKeyPressed";
    public static final String ON_KEY_RELEASED = "onKeyReleased";
    public static final String ON_KEY_TYPED = "onKeyTyped";

    // Mouse
    public static final String ON_CONTEXT_MENU_REQUESTED = "onContextMenuRequested";
    public static final String ON_MOUSE_CLICKED = "onMouseClicked";
    public static final String ON_MOUSE_DRAGGED = "onMouseDragged";
    public static final String ON_MOUSE_ENTERED = "onMouseEntered";
    public static final String ON_MOUSE_EXITED = "onMouseExited";
    public static final String ON_MOUSE_MOVED = "onMouseMoved";
    public static final String ON_MOUSE_PRESSED = "onMousePressed";
    public static final String ON_MOUSE_RELEASED = "onMouseReleased";
    public static final String ON_SCROLL = "onScroll";
    public static final String ON_SCROLL_STARTED = "onScrollStarted";
    public static final String ON_SCROLL_FINISHED = "onScrollFinished";

    // Rotation
    public static final String ON_ROTATE = "onRotate";
    public static final String ON_ROTATION_FINISHED = "onRotationFinished";
    public static final String ON_ROTATION_STARTED = "onRotationStarted";

    // Swipe
    public static final String ON_SWIPE_LEFT = "onSwipeLeft";
    public static final String ON_SWIPE_RIGHT = "onSwipeRight";
    public static final String ON_SWIPE_UP = "onSwipeUp";
    public static final String ON_SWIPE_DOWN = "onSwipeDown";

    // Touch
    public static final String ON_TOUCH_MOVED = "onTouchMoved";
    public static final String ON_TOUCH_PRESSED = "onTouchPressed";
    public static final String ON_TOUCH_RELEASED = "onTouchReleased";
    public static final String ON_TOUCH_STATIONARY = "onTouchStationary";

    // Zoom
    public static final String ON_ZOOM = "onZoom";
    public static final String ON_ZOOM_STARTED = "onZoomStarted";
    public static final String ON_ZOOM_FINISHED = "onZoomFinished";

    // should be used in a static way
    private EventTypeNames() {
    }
}