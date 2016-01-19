/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    NiubCoreLibrary
 *
 *    PreferenceHelper
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  2011-8-6
 *    @version: 1.0
 *
 ******************************************************************************/
package la.niub.util.utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import android.os.Build;

/**
 * PreferenceHelper of NiubCoreLibrary.
 * @author dhu
 *
 */
public abstract class PreferenceHelper {

    public abstract void save(Editor editor);

    private static PreferenceHelper sHelper;
    public static PreferenceHelper getInstance() {
        if (null == sHelper) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                sHelper = new NewPreferenceHelper();
            } else {
                sHelper = new OlderPreferenceHelper();
            }
        }
        return sHelper;
    }

    private static class OlderPreferenceHelper extends PreferenceHelper {

        @Override
        public void save(Editor editor) {
            editor.commit();
        }

    }

    private static class NewPreferenceHelper extends PreferenceHelper {

        @SuppressLint("NewApi")
        @Override
        public void save(Editor editor) {
            editor.apply();
        }

    }
}
