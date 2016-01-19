
package la.niub.util.utils;

public class SafeParser {

    private static final String TAG = SafeParser.class.getSimpleName();

    public static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            Log.e(TAG, "parseInt error, value:" + value);
        }
        return 0;
    }

    public static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            Log.e(TAG, "parseLong error, value:" + value);
        }
        return 0;
    }

    public static float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            Log.e(TAG, "parseFloat error, value:" + value);
        }
        return 0;
    }
}
