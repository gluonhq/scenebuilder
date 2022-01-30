package com.oracle.javafx.scenebuilder.kit.util;

import javafx.scene.paint.*;

import java.util.List;

/**
 * Convert Paint to java code or css code;
 */
public class PaintConvertUtil {

    private static final int ROUNDING_FACTOR = 10000;//Use for round to 4 decimal places

    public static String convertPaintToCss(Paint fxPaint) {
        if (fxPaint == null) {
            return "";
        }
        if (fxPaint instanceof LinearGradient) {
            LinearGradient paint = (LinearGradient) fxPaint;
            StringBuilder strBuilder = new StringBuilder("linear-gradient(from ")
                    .append(lenToStr(paint.getStartX(), paint.isProportional()))
                    .append(" ").append(lenToStr(paint.getStartY(), paint.isProportional()))
                    .append(" to ").append(lenToStr(paint.getEndX(), paint.isProportional()))
                    .append(" ").append(lenToStr(paint.getEndY(), paint.isProportional()))
                    .append(", ");
            connectCycleMethodAndStops(strBuilder, paint.getCycleMethod(), paint.getStops());
            return strBuilder.toString();
        } else if (fxPaint instanceof RadialGradient) {
            RadialGradient paint = (RadialGradient) fxPaint;
            StringBuilder strBuilder = new StringBuilder("radial-gradient(focus-angle ").append(round(paint.getFocusAngle()))
                    .append("deg, focus-distance ").append(round(paint.getFocusDistance() * 100))
                    .append("% , center ").append(lenToStr(paint.getCenterX(), paint.isProportional()))
                    .append(" ").append(lenToStr(paint.getCenterY(), paint.isProportional()))
                    .append(", radius ").append(lenToStr(paint.getRadius(), paint.isProportional()))
                    .append(", ");
            connectCycleMethodAndStops(strBuilder, paint.getCycleMethod(), paint.getStops());
            return strBuilder.toString();
        } else if (fxPaint instanceof Color) {
            return toHex((Color) fxPaint);
        }
        return "";
    }

    public static String convertPaintToJavaCode(Paint fxPaint) {
        if (fxPaint == null) {
            return "";
        }
        if (fxPaint instanceof LinearGradient) {
            LinearGradient paint = (LinearGradient) fxPaint;
            return "LinearGradient paint = new LinearGradient(" + System.lineSeparator() +
                    round(paint.getStartX()) + "," + round(paint.getStartY()) + "," +
                    round(paint.getEndX()) + "," + round(paint.getEndY()) + "," +
                    paint.isProportional() + "," +
                    cycleMethodToStr(paint.getCycleMethod()) + "," + System.lineSeparator() +
                    stopsToString(paint.getStops()) +
                    ");";
        } else if (fxPaint instanceof RadialGradient) {
            RadialGradient paint = (RadialGradient) fxPaint;
            return "RadialGradient paint = new RadialGradient(" + System.lineSeparator() +
                    round(paint.getFocusAngle()) + "," + round(paint.getFocusDistance()) + "," + round(paint.getCenterX()) + ","
                    + round(paint.getCenterY()) + "," + round(paint.getRadius()) + "," + paint.isProportional() + ","
                    + cycleMethodToStr(paint.getCycleMethod()) + System.lineSeparator() + ","
                    + stopsToString(paint.getStops()) + ");";
        } else if (fxPaint instanceof Color) {
            return "Color paint = " + colorToJavaStr((Color) fxPaint) + ";";
        }
        return "";
    }

    private static void connectCycleMethodAndStops(StringBuilder strBuilder, CycleMethod cycleMethod, List<Stop> stops) {
        switch (cycleMethod) {
            case REFLECT:
                strBuilder.append("reflect").append(", ");
                break;
            case REPEAT:
                strBuilder.append("repeat").append(", ");
                break;
            default:
                break;
        }
        int len = stops.size();
        for (int i = 0; i < len; i++) {
            Stop stop = stops.get(i);
            strBuilder.append(toHex(stop.getColor())).append(" ").append(round(stop.getOffset() * 100.0D)).append("%");
            if (i < len - 1) {
                strBuilder.append(", ");
            }
        }
        strBuilder.append(")");
    }

    private static String cycleMethodToStr(CycleMethod cycleMethod) {
        String cycleMethodStr;
        if (CycleMethod.REFLECT.equals(cycleMethod)) {
            cycleMethodStr = "CycleMethod.REFLECT";
        } else if (CycleMethod.REPEAT.equals(cycleMethod)) {
            cycleMethodStr = "CycleMethod.REPEAT";
        } else {
            cycleMethodStr = "CycleMethod.NO_CYCLE";
        }
        return cycleMethodStr;
    }

    private static String stopsToString(List<Stop> stops) {
        StringBuilder stopsBuilder = new StringBuilder(32);
        int len = stops.size();
        for (int i = 0; i < len; i++) {
            Stop stop = stops.get(i);
            Color color = stop.getColor();
            double offset = round(stop.getOffset());
            String strColor = colorToJavaStr(color);
            stopsBuilder.append("new Stop(").append(offset).append(",").append(strColor).append(")");
            if (i < len - 1) {
                stopsBuilder.append(",").append(System.lineSeparator());
            }
        }
        return stopsBuilder.toString();
    }

    private static String colorToJavaStr(Color color) {
        return String.format("new Color(%s, %s, %s, %s)", round(color.getRed()), round(color.getGreen()), round(color.getBlue()), round(color.getOpacity()));
    }

    private static String lenToStr(double num, boolean isProportional) {
        return isProportional ? round(num * 100.0D) + "%" : num + "px";
    }

    private static double round(double num) {
        double doubleRounded = Math.round(num * ROUNDING_FACTOR);
        return doubleRounded / ROUNDING_FACTOR;
    }

    private static String toHex(Color color) {
        int red = (int) Math.round(color.getRed() * 255.0D);
        int green = (int) Math.round(color.getGreen() * 255.0D);
        int blue = (int) Math.round(color.getBlue() * 255.0D);
        int alpha = (int) Math.round(color.getOpacity() * 255.0D);
        if (alpha == 255) {
            return String.format("#%02x%02x%02x", red, green, blue);
        } else {
            return String.format("#%02x%02x%02x%02x", red, green, blue, alpha);
        }
    }

}
