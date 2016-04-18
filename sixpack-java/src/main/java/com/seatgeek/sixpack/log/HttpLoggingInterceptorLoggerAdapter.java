package com.seatgeek.sixpack.log;

import com.seatgeek.sixpack.Sixpack;

import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLoggingInterceptorLoggerAdapter implements HttpLoggingInterceptor.Logger {

    private final Logger logger;

    public HttpLoggingInterceptorLoggerAdapter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(String message) {
        logger.log(Sixpack.SIXPACK_LOG_TAG, message);
    }
}
