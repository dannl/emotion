
package la.niub.util.utils;

import android.text.TextUtils;
import java.util.HashMap;

public class TraceLog {
    private static final String TAG = "TraceLog";
    private final long mStart;
    private String mMessage;
    private boolean mForceLog;
    private Object mToken;
    private int mCount;

    private static final HashMap<String, TraceLog> sLogs = new HashMap<String, TraceLog>();
    private static final HashMap<Object, TraceLog> sTokens = new HashMap<Object, TraceLog>();

    TraceLog(String message) {
        this(message, false);
    }

    TraceLog(String message, boolean forceLog) {
        this(message, forceLog, null);
    }

    TraceLog(String message, boolean forceLog, Object token) {
        mForceLog = forceLog;
        mMessage = message;
        mToken = token;
        mStart = System.currentTimeMillis();
        if (mForceLog) {
            android.util.Log.d(TAG, mMessage + " started.");
        } else {
            Log.d(TAG, mMessage + " started.");
        }
    }

    public static TraceLog start(String tag, boolean cacheIt) {
        final TraceLog log = new TraceLog(tag);
        if (cacheIt) {
            sLogs.put(tag, log);
        }
        return log;
    }

    public static TraceLog start(String tag, boolean cacheIt, boolean forceLog) {
        final TraceLog log = new TraceLog(tag, forceLog);
        if (cacheIt) {
            sLogs.put(tag, log);
        }
        return log;
    }

    public static TraceLog start(String tag, boolean cacheIt, boolean forceLog, Object token) {
        final TraceLog log = new TraceLog(tag, forceLog, token);
        if (cacheIt) {
            sLogs.put(tag, log);
        }
        if (token != null) {
            sTokens.put(token, log);
        }
        return log;
    }

    public static long end(String tag) {
        return end(tag, null);
    }

    public static long end(String tag, String msg) {
        long duration = 0;
        final TraceLog log = sLogs.remove(tag);
        if (log != null) {
            if (!TextUtils.isEmpty(msg)) {
                log.mMessage = msg;
            }
            duration = log.end();
            if (log.mToken != null) {
                sTokens.remove(log.mToken);
            }
        }
        return duration;
    }

    public static void endToken(Object token) {
        final TraceLog log = sTokens.remove(token);
        if (log != null) {
            log.end();
            sLogs.remove(log.mMessage);
        }
    }

    public static TraceLog start(String message) {
        return start(message, false);
    }

    public long end() {

        final long end = System.currentTimeMillis();
        final long duration = end - mStart;
        printTimeConsuming(mMessage, duration);
        printFpsIfNeeded(mMessage, (float) duration / 1000f, mCount);
        return duration;
    }

    private void printTimeConsuming(String message, long duration) {
        if (mForceLog) {
            android.util.Log.d(TAG, message + " takes " + duration + "ms.");
        } else {
            Log.d(TAG, message + " takes " + duration + "ms.");
        }
    }

    private void printFpsIfNeeded(String message, float duration, int frames) {
        if (frames <= 0) {
            return;
        }
        if (mForceLog) {
            android.util.Log.d(TAG, message + " : " + (float) frames / duration);
        } else {
            Log.d(TAG, message + " : " + (float) frames / duration);
        }
    }

    public int getCount() {
        return mCount;
    }

    public static void increaseCount(String tag) {
        final TraceLog log = sLogs.get(tag);
        if (log != null) {
            log.mCount++;
        }
    }

}