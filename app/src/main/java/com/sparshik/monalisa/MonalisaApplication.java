package com.sparshik.monalisa;

import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Base Application Class
 */

public class MonalisaApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
