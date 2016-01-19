/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    LibUtils
 *
 *    WechatUtil
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  2014-9-28
 *    @version: 1.0
 *
 ******************************************************************************/
package la.niub.util.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

/**
 * WechatUtil of LibUtils.
 * @author dhu
 *
 */
public class WechatUtil {

    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    private static final int WECHAT_5_0_VERSIONCODE = 350;
    private static final String WECHAT_5_0_VERSIONNAME = "5.0";
    private static final String TAEGET_CLASS = "com.tencent.mm.ui.LauncherUI";
    private static final String EXTRA_NAME = "LauncherUI_From_Biz_Shortcut";
    private static final String TAG = "FeedbackUtil";

    public static boolean feedbackWithWechat(String accountId) {

        Context context = AppContext.getInstance();
        PackageManager packageManager = context.getPackageManager();
        PackageInfo info = null;

        try {
            info = packageManager.getPackageInfo(WECHAT_PACKAGE_NAME, 0);
        } catch (NameNotFoundException e) {
            Log.d(TAG, "could not find wechat package: com.tencent.mm");
        }

        if (info == null) {
            return false;
        }

        Intent wxIntent = null;

        if (info.versionCode >= WECHAT_5_0_VERSIONCODE
                || TextUtils.equals(info.versionName, WECHAT_5_0_VERSIONNAME)) {
            wxIntent = new Intent(accountId);
            wxIntent.setClassName(WECHAT_PACKAGE_NAME, TAEGET_CLASS);
            wxIntent.putExtra(EXTRA_NAME, true);
            wxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            //Don't support version below 5.0
            return false;
        }

        try {
            AppContext.getInstance().startActivity(wxIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(e);
        } catch (SecurityException e) {
            Log.e(e);
        }

        return false;
    }

}
