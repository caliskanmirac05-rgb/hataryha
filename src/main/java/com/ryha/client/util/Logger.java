package com.ryha.client.util;

import org.slf4j.LoggerFactory;

public final class Logger {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger("RyHaClient");

    private Logger() {}

    public static void info(String msg)  { LOG.info(msg); }
    public static void warn(String msg)  { LOG.warn(msg); }
    public static void error(String msg) { LOG.error(msg); }
    public static void error(String msg, Throwable t) { LOG.error(msg, t); }
    public static void debug(String msg) { LOG.debug(msg); }
}
