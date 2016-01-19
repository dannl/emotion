package com.qqdd.lottery;

import android.app.Application;

import la.niub.util.utils.AppContext;

/**
 * Created by danliu on 2016/1/19.
 */
public class LotteryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.init(this);
    }
}
