package com.seatgeek.sixpack.log;

import jdk.nashorn.internal.ir.IfNode;

public enum LogLevel {
    VERBOSE(3),
    DEBUG(2),
    NONE(1);

    public final int level;

    LogLevel(final int level) {
        this.level = level;
    }

    public boolean isVerbose() {
        return level == VERBOSE.level;
    }

    public boolean isDebug() {
        return level == DEBUG.level;
    }

    public boolean isNone() {
        return level == NONE.level;
    }

    public boolean isAtLeastVerbose() {
        return level >= VERBOSE.level;
    }

    public boolean isAtLeastDebug() {
        return level >= DEBUG.level;
    }
}
