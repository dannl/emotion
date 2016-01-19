/*******************************************************************************
 *
 *    Copyright (c) Niub Info Tech Co. Ltd
 *
 *    WeFamily
 *
 *    NetworkUtil
 *    TODO File description or class description.
 *
 *    @author: dhu
 *    @since:  2014-5-14
 *    @version: 1.0
 *
 ******************************************************************************/
package la.niub.util.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.HashMap;
import java.util.Map;

/**
 * NetworkUtil of WeFamily.
 * @author dhu
 * @modifier ympeng add some network method
 *
 */
public class NetworkUtil {

    private static final Map<NetworkType, String> sNetworkTypeMap = new HashMap<NetworkType, String>();

    static {

        sNetworkTypeMap.put(NetworkType.TYPE_DISCONNECTED, "disconnected");
        sNetworkTypeMap.put(NetworkType.TYPE_WIFI, "wifi");
        sNetworkTypeMap.put(NetworkType.TYPE_MOBILE, "mobile");
        sNetworkTypeMap.put(NetworkType.TYPE_2G, "2G");
        sNetworkTypeMap.put(NetworkType.TYPE_3G, "3G");
        sNetworkTypeMap.put(NetworkType.TYPE_4G, "4G");
        sNetworkTypeMap.put(NetworkType.TYPE_SPECIAL, "special");
        sNetworkTypeMap.put(NetworkType.TYPE_BLUETOOTH, "bluetooth");
        sNetworkTypeMap.put(NetworkType.TYPE_UNKNOWN, "unknown");
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMgr.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connMgr.getActiveNetworkInfo();
        if (null != ni) {
            return ni.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    public static NetworkType getNetworkType(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {

            return NetworkType.TYPE_DISCONNECTED;
        }

        int activeNetworkType = networkInfo.getType();
        switch (activeNetworkType) {

        case ConnectivityManager.TYPE_WIFI:
            return NetworkType.TYPE_WIFI;
        case ConnectivityManager.TYPE_MOBILE:
            return getMobileType(context);
        case ConnectivityManager.TYPE_MOBILE_DUN:
        case ConnectivityManager.TYPE_MOBILE_HIPRI:
        case ConnectivityManager.TYPE_MOBILE_MMS:
        case ConnectivityManager.TYPE_MOBILE_SUPL:
            return NetworkType.TYPE_SPECIAL;
        case ConnectivityManager.TYPE_BLUETOOTH:
            return NetworkType.TYPE_BLUETOOTH;
        default:
            return NetworkType.TYPE_UNKNOWN;
        }
    }
    public static NetworkType getMobileType(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Integer currentNetworkClassInteger = (Integer) ReflectionUtils.invokeStaticMethod("android.telephony.TelephonyManager", "getNetworkClass",
                                        new Class<?>[] {Integer.class}, new Object[] {telephonyManager.getNetworkType()});
        // if "getNetworkClass" return null, means current SDK delete this method
        if (currentNetworkClassInteger == null) {

            return NetworkType.TYPE_MOBILE;
        }

        int currentNetworkClass = currentNetworkClassInteger;
        int networkClass2G = (Integer) ReflectionUtils.getStaticFieldValue("android.telephony.TelephonyManager", "NETWORK_CLASS_2_G");
        int networkClass3G = (Integer) ReflectionUtils.getStaticFieldValue("android.telephony.TelephonyManager", "NETWORK_CLASS_3_G");
        int networkClass4G = (Integer) ReflectionUtils.getStaticFieldValue("android.telephony.TelephonyManager", "NETWORK_CLASS_4_G");

        if (currentNetworkClass == networkClass2G) {

            return NetworkType.TYPE_2G;
        } else if (currentNetworkClass == networkClass3G) {

            return NetworkType.TYPE_3G;
        } else if (currentNetworkClass == networkClass4G) {

            return NetworkType.TYPE_4G;
        } else {

            return NetworkType.TYPE_MOBILE;
        }
    }

    public static String getNetworkTypeString(Context context) {

        return sNetworkTypeMap.get(getNetworkType(context));
    }

    /**
     * @description
     * TYPE_DISCONNECTED not connected yet
     * TYPE_UNKNOWN      unknown
     * TYPE_WIFI         wifi
     * TYPE_2G           2g
     * TYPE_3G           3g
     * TYPE_4G           4g
     *
     */
    public static enum NetworkType {

        TYPE_DISCONNECTED, TYPE_WIFI, TYPE_MOBILE, TYPE_2G, TYPE_3G, TYPE_4G, TYPE_SPECIAL, TYPE_BLUETOOTH, TYPE_UNKNOWN
    }
}
