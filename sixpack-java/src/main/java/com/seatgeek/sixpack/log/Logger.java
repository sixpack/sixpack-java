package com.seatgeek.sixpack.log;

/**
 * Simple logging interface used internally in {@code Sixpack}
 */
public interface Logger {

    /**
     * Sends a message to stdout; on Android this is logcat debug
     */
    void log(String tag, String message);

    /**
     * Sends a message to stderr and stdout; on Android this is logcat error
     */
    void loge(String tag, String message, Throwable e);
}
