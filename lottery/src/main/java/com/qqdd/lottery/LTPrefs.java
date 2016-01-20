package com.qqdd.lottery;

import la.niub.util.utils.PrefsFile;

/**
 * Created by danliu on 1/20/16.
 */
public class LTPrefs extends PrefsFile {


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
