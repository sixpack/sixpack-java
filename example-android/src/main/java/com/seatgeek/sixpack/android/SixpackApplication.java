package com.seatgeek.sixpack.android;

import android.app.Application;

import dagger.ObjectGraph;

public class SixpackApplication extends Application {

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        buildObjectGraph();
    }

    public void buildObjectGraph() {
        objectGraph = ObjectGraph.create(
            new SixpackModule()
        );
    }

    public void inject(Object o) {
        objectGraph.inject(o);
    }
}
