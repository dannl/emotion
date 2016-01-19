package la.niub.util.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PackageUtil {

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        if (TextUtils.isEmpty(packageName)) {
            throw new IllegalArgumentException("packageName may not be null or empty.");
        }
        final PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (final NameNotFoundException e) {
        }
        return packageInfo;
    }

    public static int getVersionCode(Context context, String packageName) {
        final PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    public static String getVersionName(Context context, String packageName) {
        final PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "";
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static Drawable getApkIcon(Context context, String apkPath){
        if (null == context) {
            throw new IllegalArgumentException("context may not be null.");
        }
        try {
            // get Package Parser
            Class<?> pkgParserClass = Class.forName("android.content.pm.PackageParser");
            Class<?>[] typeArgs = { String.class };
            Constructor<?> pkgParserConstructor = pkgParserClass.getConstructor(typeArgs);
            Object[] valueArgs = { apkPath };
            Object pkgParserObj = pkgParserConstructor.newInstance(valueArgs);

            //get package
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            typeArgs = new Class<?>[] { File.class, String.class,
                    DisplayMetrics.class, int.class };
            Method parsePackageMethod = pkgParserClass.getDeclaredMethod(
                    "parsePackage", typeArgs);
            valueArgs = new Object[] { new File(apkPath), apkPath, metrics, 0 };
            Object packageObj = parsePackageMethod.invoke(pkgParserObj, valueArgs);

            // get ApplicationInfo
            Field appInfoField = packageObj.getClass().getDeclaredField("applicationInfo");
            ApplicationInfo info = (ApplicationInfo) appInfoField.get(packageObj);


            // get Asset Manager
            Class<?> assetManagerClass = Class.forName("android.content.res.AssetManager");
            Object assetManagerObj = assetManagerClass.newInstance();

            typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Method addAssetPathMethod = assetManagerClass.getDeclaredMethod(
                    "addAssetPath", typeArgs);

            valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            addAssetPathMethod.invoke(assetManagerObj, valueArgs);

            // get Resources
            Resources res = context.getResources();

            typeArgs = new Class[3];
            typeArgs[0] = assetManagerObj.getClass();
            typeArgs[1] = res.getDisplayMetrics().getClass();
            typeArgs[2] = res.getConfiguration().getClass();
            Constructor<Resources> resConstructor = Resources.class.getConstructor(typeArgs);

            valueArgs = new Object[3];
            valueArgs[0] = assetManagerObj;
            valueArgs[1] = res.getDisplayMetrics();
            valueArgs[2] = res.getConfiguration();
            res = (Resources) resConstructor.newInstance(valueArgs);

            // get Icon
            if (info != null && info.icon != 0) {
                return res.getDrawable(info.icon);
            }
        } catch (Exception e) {
            Log.e(e);
        }
        return null;
    }

    public static String createPkgSig(PackageManager pckMan, PackageInfo pkgInfo) {
        if (null == pckMan || null == pkgInfo) {
            return null;
        }
        ByteArrayInputStream stream = null;
        final StringBuilder builder = new StringBuilder();
        try {
            stream = new ByteArrayInputStream(pckMan.getPackageInfo(pkgInfo.packageName,
                    PackageManager.GET_SIGNATURES).signatures[0].toByteArray());

            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final Collection<?> c = cf.generateCertificates(stream);

            byte[] bg = null;
            final Map<String, byte[]> map = new HashMap<String, byte[]>();
            int index = 0;
            final Iterator<?> iterator = c.iterator();
            while (iterator.hasNext()) {
                Certificate cert = (Certificate)iterator.next();
                bg = cert.getEncoded();
                map.put("" + index, bg);
                bg = null;
                index++;
            }

            java.security.MessageDigest digest = null;
            digest = java.security.MessageDigest.getInstance("sha-1");

            if (map.get("0") != null) {
                digest.update(map.get("0"));
                final byte[] digesta = digest.digest();
                for (int j = 0; j < digesta.length; j++) {
                    builder.append(digesta[j]);
                }
            }
        } catch (Exception ex) {
        } finally {
            IOUtilities.closeStream(stream);
        }
        return builder.toString();
    }

    public static void resolveLauncherApps(final Context context, HashSet<String> launcherAppSet) {
        if(null == launcherAppSet) {
            launcherAppSet = new HashSet<String>();
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pManager.queryIntentActivities(intent, 0);
        if(null != resolveInfos) {
            for(int i = 0; i < resolveInfos.size(); i++) {
                launcherAppSet.add(resolveInfos.get(i).activityInfo.packageName);
            }
        }
    }
}
