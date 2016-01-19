
package la.niub.util.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.Toast;

public class UIUtil {
    private static final String TAG = "UIUtil";
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static interface Method<T> {
        T call();
    }
    private static class Result {
        Object obj;
        boolean complete;
    }
    private static final class SyncRunnable implements Runnable {
        private final Runnable mTarget;
        private boolean mComplete;

        public SyncRunnable(Runnable target) {
            mTarget = target;
        }

        public void run() {
            mTarget.run();
            synchronized (this) {
                mComplete = true;
                notifyAll();
            }
        }

        public void waitForComplete() {
            synchronized (this) {
                while (!mComplete) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
    /**
     * Retrieve the sHandler.
     * @return the sHandler
     */
    public static Handler getHandler() {
        return sHandler;
    }

    public static Thread getUIThread() {
        return Looper.getMainLooper().getThread();
    }

    public static boolean isOnUIThread() {
        return Thread.currentThread() == getUIThread();
    }

    public static void runOnUIThread(Runnable action) {
        if (!isOnUIThread()) {
            getHandler().post(action);
        } else {
            action.run();
        }
    }

    public static boolean isAfterJellyBeanMR2() {
        return Build.VERSION.SDK_INT > 18;//Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    //All WebView method must be called in UI Thread After SDK18
    public static void runOnUIThreadAfterSDK18(Runnable action) {
        if (!isOnUIThread() && isAfterJellyBeanMR2()) {
            getHandler().post(action);
        } else {
            action.run();
        }
    }

    public static <T> T runOnUIThreadAfterSDK18(final Method<T> method) {
        if (isAfterJellyBeanMR2()) {
            return runOnUIThread(method);
        } else {
            return method.call();
        }
    }

    public static void runOnUIThreadDelay(Runnable action, long delayMillis) {
        getHandler().postDelayed(action, delayMillis);
    }

    /**
     * Execute a call on the application's main thread, blocking until it is
     * complete.  Useful for doing things that are not thread-safe, such as
     * looking at or modifying the view hierarchy.
     *
     * @param runner The code to run on the main thread.
     */
    public static void runOnUIThreadSync(Runnable action) {
        if (!isOnUIThread()) {
            SyncRunnable sr = new SyncRunnable(action);
            getHandler().post(sr);
            sr.waitForComplete();
        } else {
            action.run();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T runOnUIThread(final Method<T> method) {
        if (!isOnUIThread()) {
            final Result result = new Result();
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    synchronized (result) {
                        try {
                            result.obj = method.call();
                        } catch (Exception e) {
                            Log.w(null, e);
                        }
                        result.complete = true;
                        result.notifyAll();
                    }
                }
            });
            synchronized (result) {
                while (!result.complete) {
                    try {
                        result.wait();
                    } catch (InterruptedException e) {
                    }
                }
                return (T) result.obj;
            }
        } else {
            return method.call();
        }
    }

    public static boolean showDialogSafe(Dialog dialog) {
        try {
            dialog.show();
            return true;
        } catch (Exception e) {
            //log detail informations
            Log.w(null, e);
            return false;
        }
    }

    public static boolean dismissDialogSafe(DialogInterface dialog) {
        if (dialog == null) {
            return false;
        }

        try {
            dialog.dismiss();
            return true;
        } catch (BadTokenException e) {
            Log.w(null, e.getMessage());
            return false;
        } catch (IllegalStateException e) {
            Log.w(null, e.getMessage());
            return false;
        } catch (Exception e) {
            Log.w(null, e.getMessage());
            return false;
        }
    }


    public static void showToastSafe(final Context context, final int msgId) {

        try {
            showToastSafe(context, context.getString(msgId));
        } catch (Exception e) {
            Log.e(null, e.getMessage());
        }
    }

    public static void showToastSafe(final Context context, final String msg) {
        showToastSafe(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToastSafe(final Context context, final String msg, final int durationFlag) {
        try {
            runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, durationFlag).show();
                }
            });
        } catch (Exception e) {
            Log.e(null, e.getMessage());
        }

    }

    public static void addView(View view, ViewGroup.LayoutParams params, WindowManager manager) {

        try {
            manager.addView(view, params);
        } catch (Exception e) {
            Log.e(TAG, e);
        }
    }

    public static void removeView(View view, WindowManager manager) {
        try {
            manager.removeView(view);
        } catch (Exception e) {
            Log.e(TAG, e);
        }
    }

    /*
     * Get child of the parent view, which is same type with given ViewClass.
     */
    public static View getChildByType(ViewGroup parent, Class<? extends View> viewClass) {
        if (parent != null) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child.getClass() == viewClass) {
                    return child;
                }
            }
        }

        return null;
    }

    public static void updatePaddingByBackground(View view) {
        if (view == null) {
            return;
        }
        Drawable background = view.getBackground();
        if (background == null) {
            return;
        }
        Rect rect = new Rect();
        background.getPadding(rect);
        view.setPadding(rect.left, rect.top, rect.right, rect.bottom);
    }
}
