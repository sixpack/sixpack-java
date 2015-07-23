package com.seatgeek.sixpack.log;

import android.util.Log;

public class LogcatLogger implements Logger {

    @Override
    public void log(final String tag, final String message) {
        Log.d(tag, message);
    }

    @Override
    public void loge(final String tag, final String message, final Throwable e) {
        if (e != null) {
            Log.e(tag, message, e);
        } else {
            Log.e(tag, message);
        }
    }
}
