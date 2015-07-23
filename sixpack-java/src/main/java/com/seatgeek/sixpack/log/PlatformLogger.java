package com.seatgeek.sixpack.log;

public enum PlatformLogger {
    INSTANCE;

    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    PlatformLogger() {
        boolean isAndroid;
        try {
            Class.forName("android.os.Build");
            isAndroid = true;
        } catch (ClassNotFoundException e) {
            isAndroid = false;
        }

        logger = isAndroid ? new LogcatLogger() : new SystemOutLogger();
    }
}
