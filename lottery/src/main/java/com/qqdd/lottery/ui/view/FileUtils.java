package com.qqdd.lottery.ui.view;

import java.io.File;

import la.niub.util.utils.StorageHelper;

/**
 * Created by danliu on 11/25/15.
 */
public class FileUtils {

    public static File getCacheDir() {
        return new File(StorageHelper.getExternalStorageDirectory(), "Ltt");
    }
}
