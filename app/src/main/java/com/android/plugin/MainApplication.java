package com.android.plugin;

import android.app.Application;
import android.content.Context;

import com.morgoo.droidplugin.PluginHelper;

/**
 * Description:
 * Author     : kevin.bai
 * Time       : 2017/1/22 17:24
 * QQ         : 904869397@qq.com
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
    }
}
