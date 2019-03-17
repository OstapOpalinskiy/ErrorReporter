package com.opalynskyi.crashreportertestapp;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.opalynskyi.errorreporter.ErrorReporter;

public class App extends Application {
    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ErrorReporter.getInstance().initialize(this);
        Stetho.initializeWithDefaults(this);
    }
}
