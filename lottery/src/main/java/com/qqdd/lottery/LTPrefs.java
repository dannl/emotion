package com.qqdd.lottery;

import la.niub.util.utils.PrefsFile;

/**
 * Created by danliu on 1/20/16.
 */
public class LTPrefs extends PrefsFile {

    public void setShowTestEntrance(boolean showTestEntrance) {
        setBooleanValue("showTestEntrance", showTestEntrance);
    }

    public boolean showTestEntrance() {
        return getBooleanValue("showTestEntrance", false);
    }


    private static class SingletonHolder {
        private static LTPrefs INSTANCE = new LTPrefs();
    }

    public static LTPrefs getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private LTPrefs() {
        super("lt_prefs");
    }

}
