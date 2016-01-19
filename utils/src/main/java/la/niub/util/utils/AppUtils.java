
package la.niub.util.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.io.File;
import java.util.List;

/**
 * 提供action
 */
public class AppUtils {

    private static final String TAG = "AppUtils";

    private static final String SMS_BODY_KEY = "sms_body";
    private static final String KEY_TREAT_UP_AS_BACK = "treat-up-as-back";

    public static boolean debuggable(Context context) {
        String className = context.getPackageName() + ".BuildConfig";
        Object value = ReflectionUtils.getStaticFieldValue(className, "DEBUG");
        if (value == null) {
            return false;
        }
        return (Boolean) value;
    }

    public static void killMyProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to start activity", e);
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to start activity", e);
        }
    }

    // 最小化，模拟按下home键
    public static void minimizeWindow(Context context) {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(context, i);
    }

    /**
     * 打开照相机拍照
     */
    public static void openCamera(Activity activity, String path, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
        activity.startActivityForResult(intent, requestCode);
    }

    public static void playVideo(Activity activity, String path, String title, int requestCode) {
        Uri uri = Uri.fromFile(new File(path));
        playVideo(activity, uri, title, requestCode);
    }

    public static void playVideo(Activity activity, Uri uri, String title, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "video/*")
                .putExtra(Intent.EXTRA_TITLE, title)
                .putExtra(KEY_TREAT_UP_AS_BACK, true);
        startActivityForResult(activity, intent, requestCode);
    }

    /**
     * 打开相册，准备挑选图片
     */
    public static void openGallery(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(activity, intent, requestCode);
    }

    /**
     * 照相后或者从相册挑选图片后，进行裁剪
     *
     * @param uri
     */
    public static void openCrop(Activity activity, Uri uri, Uri dst, int requestCode, int outputX, int outputY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", outputX);
        intent.putExtra("aspectY", outputY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, dst);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(activity, intent, requestCode);
    }

    public static void openAvatarCrop(Activity activity, Uri uri, Uri dst, int requestCode) {
        openCrop(activity, uri, dst, requestCode, 160, 160);
    }

    public static void openCoverCrop(Activity activity, Uri uri, Uri dst, int requestCode) {
        openCrop(activity, uri, dst, requestCode, 720, 260);
    }

    public static void openContact(Activity activity, int requestCode) {
        Intent openContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(activity, openContact, requestCode);
    }

    public static void openUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(context, intent);
    }

    public static void openMarket(Context context, String packageName) {
        Uri marketUri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, marketUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        startActivity(context, intent);
    }

    public static void sendSms(Context context, String phone, String smsBody) {
        Uri uri = Uri.parse("smsto:" + phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(SMS_BODY_KEY, smsBody);
        startActivity(context, intent);
    }

    public static void sendSms(Context context, List<String> phoneList, String smsBody) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("address", StringUtil.stringFromArray(phoneList, ";"));
        intent.putExtra(SMS_BODY_KEY, smsBody);
        intent.setType("vnd.android-dir/mms-sms");
        startActivity(context, intent);
    }

    public static void shareAttachment(Context context, File file) {
        if (null == file) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        final String name = file.getName();
        String type = null;
        if (name.endsWith(".gz")) {
            type = "application/x-gzip";
        } else if (name.endsWith(".txt")) {
            type = "text/plain";
        } else {
            type = "application/octet-stream";
        }
        intent.setType(type);
        startActivity(context, Intent.createChooser(intent, "Choose Share Client"));
    }

    /**
     * 调用系统share
     *
     * @param context
     * @return
     */
    public static void share(Context context, String subject, String body) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, body);
        i.setType("text/plain");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(context, i);
    }

    /**
     * 为程序创建桌面快捷方式
     */
    public static void createShortCut(Context context, Intent intent, String shortcurtName,
            int shortcutIconRes) {
        if (hasShortcut(context, shortcurtName)) {
            return;
        }
        intent.putExtra(Intent.EXTRA_TEXT, "short_cut");
        final Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        final ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(context,
                shortcutIconRes); // 获取快捷键的图标
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcurtName);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcut);
    }

    public static boolean hasShortcut(Context context, String shortcurtName) {
        boolean isInstallShortcut = false;
        String url = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            url = "content://com.android.launcher.settings/favorites?notify=true";
        } else {
            url = "content://com.android.launcher2.settings/favorites?notify=true";
        }
        Cursor c = context.getContentResolver().query(Uri.parse(url), new String[] {
                "title", "iconResource"
        }, "title=?", new String[] {
                shortcurtName,
        }, null);
        if (c != null) {
            isInstallShortcut = c.getCount() > 0;
            c.close();
        }
        return isInstallShortcut;
    }

    /**
     * 删除程序的快捷方式
     */
    public static void delShortcut(Context context, Intent intent, String shortcurtName) {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcurtName);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcut);
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return packageInfo != null;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 设置应用在launcher上图标右上角显示的数字, 仅支持三星手机默认的launcher
     * @param context
     * @param count 要显示的数字, 传0的话清除数字的显示
     */
    public static void setBadge(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String metaValue = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                metaValue = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return metaValue;
    }

}
