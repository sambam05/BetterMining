package com.sheath.bettermining.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTool {
    private final Logger logger;
    private static final String MOD_ID = ColorUtils.CYAN("[Better Mining]");

    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    // Constructor
    private LoggerTool(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }
    // Factory method to create a LoggerTool instance
    public static LoggerTool getLogger(Class<?> clazz) {
        return new LoggerTool(clazz);
    }

    // Logging methods with colors
    public void info(String message) {
        logger.info(colorize(message, GREEN));
    }

    public void success(String message) {
        logger.info(colorize( message, GREEN));
    }

    public void warn(String message) {
        logger.warn(colorize(message, YELLOW));
    }

    public void error(String message) {
        logger.error(colorize(message, RED));
    }

    public void debug(String message) {
        logger.debug(colorize(message, YELLOW));
    }

    public void custom(String message, String colour) {
        logger.info(colorize(message, colour));
    }

    // Private helper to apply color
    private String colorize(String message, String color) {
        return MOD_ID +" "+ color + message + RESET;
    }

}
