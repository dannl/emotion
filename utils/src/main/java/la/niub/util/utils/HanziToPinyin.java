/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    LibUtils
 *
 *    HanziToPinyin
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  Feb 3, 2015
 *    @version: 1.0
 *
 ******************************************************************************/
package la.niub.util.utils;

import android.text.TextUtils;
import java.util.Locale;

/**
 * HanziToPinyin of LibUtils.
 * @author dhu
 *
 */
public class HanziToPinyin {
    private static final String TAG = "HanziToPinyin";
    private static final String TRANSLITERATOR = "libcore.icu.Transliterator";

    private static HanziToPinyin sInstance;
    private Object mPinyinTransliterator;
    private HanziToPinyin() {
        try {
            mPinyinTransliterator = ReflectionUtils.newInstance(TRANSLITERATOR,
                    new Object[] {"Han-Latin/Names; Latin-Ascii; Any-Upper"});
        } catch (Exception e) {
            Log.w(TAG, "Han-Latin/Names transliterator data is missing,"
                  + " HanziToPinyin is disabled");
        }
    }

    public boolean hasChineseTransliterator() {
        return mPinyinTransliterator != null;
    }

    public static HanziToPinyin getInstance() {
        synchronized (HanziToPinyin.class) {
            if (sInstance == null) {
                sInstance = new HanziToPinyin();
                if (!"zhong wen".equals(sInstance.transliterate("中文"))) {
                    sInstance.mPinyinTransliterator = null;
                }
            }
            return sInstance;
        }
    }

    public String transliterate(final String input) {
        if (!hasChineseTransliterator() || TextUtils.isEmpty(input)) {
            return null;
        }
        try {
            String pinyin = (String) ReflectionUtils.invokeMethod(mPinyinTransliterator, "transliterate", new Class[] {String.class}, new String[]{input});
            if (pinyin != null) {
                return pinyin.toLowerCase(Locale.US);
            }
        } catch (Exception e) {
        }
        return null;
    }

}
