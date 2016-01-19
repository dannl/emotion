/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    LibUtils
 *
 *    DeviceHelper
 *    TODO File description or class description.
 *
 *    @author: danliu
 *    @since:  Sep 29, 2014
 *    @version: 1.0
 *
 ******************************************************************************/

package la.niub.util.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * DeviceHelper of LibUtils.
 *
 * @author danliu
 */
public class DeviceHelper {

    private static String mDeviceId;
    private static String mAndroidId;

    public static final String getDeviceId() {
        if (TextUtils.isEmpty(mDeviceId)) {
            mDeviceId = ((TelephonyManager) AppContext.getInstance().getSystemService(Context.TELEPHONY_SERVICE))
                    .getDeviceId();
            // some devices have no device id!!
            if (TextUtils.isEmpty(mDeviceId)) {
                mDeviceId = getAndroidId();
            }
        }
        return mDeviceId;
    }


    public static String getAndroidId() {
        if (TextUtils.isEmpty(mAndroidId)) {
            mAndroidId = Settings.Secure.getString(AppContext.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return mAndroidId;
    }

}
