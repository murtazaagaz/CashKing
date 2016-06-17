package com.hackerkernel.cashking.infrastructure;

import android.app.Application;
import android.content.Context;

import com.hackerkernel.cashking.util.FontsOverRide;


/**
 * Custom application class
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FontsOverRide.setDefaultFont(this,"SERIF","Exo-Regular.ttf");
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }


    }


