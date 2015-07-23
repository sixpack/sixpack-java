package com.seatgeek.sixpack.log;

public class SystemOutLogger implements Logger {

    @Override
    public void log(final String tag, final String message) {
        System.out.println(String.format("%s: %s", tag, message));
    }

    @Override
    public void loge(final String tag, final String message, final Throwable e) {
        String output = String.format("%s: %s\n\n%s", tag, message, e != null ? e.getMessage() : "");
        System.err.println(output);
        System.out.println(message);
    }
}
