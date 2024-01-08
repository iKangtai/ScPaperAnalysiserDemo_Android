package com.example.paperdemo;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

/**
 * desc
 *
 * @author xiongyl 2023/2/18 14:31
 */
public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
