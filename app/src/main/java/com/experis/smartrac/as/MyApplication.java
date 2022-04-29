package com.experis.smartrac.as;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {

    public MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try{
            MultiDex.install(MyApplication.this);
        }
        catch (Exception e){e.printStackTrace();}
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

}
