/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.editors.util;

import javafx.beans.NamedArg;
import javafx.util.Duration;

public class SBDuration extends Duration {

    /**
     * Creates a new SBDuration with potentially fractional millisecond resolution.
     *
     * @param millis The number of milliseconds
     */
    public SBDuration(@NamedArg("millis") double millis) {
        super(millis);
    }

    /**
     * Creates a new SBDuration instance from a Duration object.
     * @param duration
     */
    public SBDuration(Duration duration){
        super(duration.toMillis());
    }

    @Override
    public String toString() {
        return isIndefinite() ? "INDEFINITE" : (isUnknown() ? "UNKNOWN" : this.toMillis() + "ms");
    }

    /**
     * Factory method that returns a Duration instance for a specified
     * amount of time. The syntax is "[number][ms|s|m|h]".
     *
     * @param time A non-null string properly formatted. Leading or trailing
     * spaces will not parse correctly. Throws a NullPointerException if
     * time is null.
     * @return a Duration which is represented by the <code>time</code>
     */
    public static SBDuration valueOf(String time) {
        int index = -1;
        for (int i=0; i<time.length(); i++) {
            char c = time.charAt(i);
            if (!Character.isDigit(c) && c != '.' && c != '-' && c!='E') {
                index = i;
                break;
            }
        }

        String suffix;
        double value;
        if (index == -1) {
            value = Double.parseDouble(time);
            // Never found the suffix!
            suffix = "ms";
        } else {
            value = Double.parseDouble(time.substring(0, index));
            suffix = time.substring(index);
        }
        if ("ms".equals(suffix)) {
            return new SBDuration(millis(value));
        } else if ("s".equals(suffix)) {
            return new SBDuration(seconds(value));
        } else if ("m".equals(suffix)) {
            return new SBDuration(minutes(value));
        } else if ("h".equals(suffix)) {
            return new SBDuration(hours(value));
        } else {
            // Malformed suffix
            throw new IllegalArgumentException("The time parameter must have a suffix of [ms|s|m|h]");
        }

    }
}
