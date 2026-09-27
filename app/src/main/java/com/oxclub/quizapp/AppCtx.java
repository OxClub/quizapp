package com.oxclub.quizapp;

import android.app.Application;
import android.content.Context;

public class AppCtx extends Application {
    private static Context ctx;
    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();
    }
    public static Context get() { return ctx; }
}
