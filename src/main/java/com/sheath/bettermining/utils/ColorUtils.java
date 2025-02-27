package com.sheath.bettermining.utils;

import org.joml.Vector3i;

public class ColorUtils {
    public static final String RESET = "\u001B[0m";

    // Define ANSI color codes as static methods for direct use
    public static String RED(String text) {
        return "\u001B[31m" + text + "\u001B[0m";
    }

    public static String GREEN(String text) {
        return "\u001B[32m" + text + "\u001B[0m";
    }

    public static String YELLOW(String text) {
        return "\u001B[33m" + text + "\u001B[0m";
    }

    public static String BLUE(String text) {
        return "\u001B[34m" + text + "\u001B[0m";
    }

    public static String PURPLE(String text) {
        return "\u001B[35m" + text + "\u001B[0m";
    }

    public static String CYAN(String text) {
        return "\u001B[36m" + text + "\u001B[0m";
    }

    public static String WHITE(String text) {
        return "\u001B[37m" + text + "\u001B[0m";
    }

    public static int vector3fToInt(Vector3i color) {
        int r = color.x; // Scale red to [0, 255]
        int g = color.y; // Scale green to [0, 255]
        int b = color.z; // Scale blue to [0, 255]

        // Pack into an integer: 0xRRGGBB
        return (r << 16) | (g << 8) | b;
    }

    public static String color(String text, String color) {
        return color + text + RESET;
    }

}
