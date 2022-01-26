package com.oracle.javafx.scenebuilder.kit.util;

import javafx.scene.paint.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Convert Paint to java code or css code;
 */
public class PaintConvertUtil {

    /**
     * Round to 3 decimal places
     */
    private static final int DOUBLE_SCALE = 3;

    public static String convertPaintToCss(Paint fxPaint) {
        if (fxPaint == null) {
            return null;
        }
        if (fxPaint instanceof LinearGradient) {
            LinearGradient paint = (LinearGradient) fxPaint;
            StringBuilder strBuilder = new StringBuilder("linear-gradient(from ")
                    .append(lenToStr(paint.getStartX(), paint.isProportional()))
                    .append(" ").append(lenToStr(paint.getStartY(), paint.isProportional()))
                    .append(" to ").append(lenToStr(paint.getEndX(), paint.isProportional()))
                    .append(" ").append(lenToStr(paint.getEndY(), paint.isProportional()))
                    .append(", ");
            appendStr(strBuilder, paint.getCycleMethod(), paint.getStops());
            return strBuilder.toString();
        } else if (fxPaint instanceof RadialGradient) {
            RadialGradient paint = (RadialGradient) fxPaint;
            StringBuilder strBuilder = new StringBuilder("radial-gradient(focus-angle ").append(round(paint.getFocusAngle()))
                    .append("deg, focus-distance ").append(round(paint.getFocusDistance() * 100))
                    .append("% , center ").append(lenToStr(paint.getCenterX(), paint.isProportional()))
                    .append(" ").append(lenToStr(paint.getCenterY(), paint.isProportional()))
                    .append(", radius ").append(lenToStr(paint.getRadius(), paint.isProportional()))
                    .append(", ");
            appendStr(strBuilder, paint.getCycleMethod(), paint.getStops());
            return strBuilder.toString();
        } else if (fxPaint instanceof Color) {
            return toHex((Color) fxPaint);
        }
        return null;
    }

    public static String convertPaintToJavaCode(Paint fxPaint) {
        if (fxPaint == null) {
            return null;
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
            Color color = (Color) fxPaint;
            return String.format("Color paint = new Color( %s, %s, %s, %s);", round(color.getRed()), round(color.getGreen()), round(color.getBlue()), round(color.getOpacity()));
        }
        return null;
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
            String strColor = String.format("new Color( %s, %s, %s, %s)", round(color.getRed()), round(color.getGreen()), round(color.getBlue()), round(color.getOpacity()));
            stopsBuilder.append("new Stop(").append(offset).append(",").append(strColor).append(")");
            if (i != len - 1) {
                stopsBuilder.append(",").append(System.lineSeparator());
            }
        }
        return stopsBuilder.toString();
    }

    private static void appendStr(StringBuilder strBuilder, CycleMethod cycleMethod, List<Stop> stops) {
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
            if (i != len - 1) {
                strBuilder.append(", ");
            }
        }
        strBuilder.append(")");
    }

    private static String lenToStr(double num, boolean isProportional) {
        return isProportional ? round(num * 100.0D) + "%" : num + "px";
    }

    private static double round(double num) {
        return new BigDecimal(num).setScale(DOUBLE_SCALE, RoundingMode.HALF_UP).doubleValue();
    }

    public static String toHex(Color color) {
        int red = (int) Math.round(color.getRed() * 255.0D);
        int green = (int) Math.round(color.getGreen() * 255.0D);
        int blue = (int) Math.round(color.getBlue() * 255.0D);
        int alpha = (int) Math.round(color.getOpacity() * 255.0D);
        return String.format("#%02x%02x%02x%02x", red, green, blue, alpha);
    }

}
