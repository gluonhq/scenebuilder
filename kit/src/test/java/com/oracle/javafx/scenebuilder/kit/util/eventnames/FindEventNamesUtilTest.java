/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.kit.util.eventnames;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit test for {@link FindEventNamesUtil#findEventName(String)}
 */
public class FindEventNamesUtilTest {

    @BeforeAll
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

        assertThat(callService(dummyEventTypeName)).isNotNull();
        assertThat(callService(dummyEventTypeName)).isEqualTo(EventNames.ACTION_EVENT);
        assertThat(callService(EventTypeNames.ON_ROTATION_FINISHED)).isNotEqualTo(dummyEventName);
    }
}
