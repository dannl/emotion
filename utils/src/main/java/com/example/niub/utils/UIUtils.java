package com.example.niub.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by danliu on 11/25/15.
 */
public class UIUtils {

    public static void toast(Context context, int res) {
        try {
            toast(context, context.getString(res));
        } catch (Exception e) {
            //ignore
        }
    }

    public static void toast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT)
                .show();
    }
}
