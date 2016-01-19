/*******************************************************************************
 *
 *    Copyright (c) Niub
 *
 *    NiubCoreLibrary
 *
 *    StorageHelper
 *
 *
 *    @author: derron
 *    @since:  Jun 11, 2010
 *    @version: 1.0
 *
 ******************************************************************************/

package la.niub.util.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * StorageHelper of TunnyBrowser.Can work on Incredible(Only have internal
 * storage)
 *
 * @author derron
 */

public class StorageHelper {
    private static final String LOG_TAG = "StorageHelper";
    private static final String MOUNT_LINE_VALID_REG =
            "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*r(o|w).*";
    private static final boolean HAS_PHONE_STARAGE;
    private static Method getPhoneStorageStateMethod;
    private static Method getPhoneStorageDirectoryMethod;
    static {
        boolean hasPhoneStorage = false;
        try {
            Class<Environment> cls = Environment.class;
            getPhoneStorageDirectoryMethod = cls.getDeclaredMethod("getPhoneStorageDirectory");
            getPhoneStorageDirectoryMethod.setAccessible(true);

            getPhoneStorageStateMethod = cls.getDeclaredMethod("getPhoneStorageState");
            getPhoneStorageStateMethod.setAccessible(true);
            hasPhoneStorage = true;
        } catch (Exception e) {
            //Not HTC Sense device, do nothing
        }
        HAS_PHONE_STARAGE = hasPhoneStorage;
    }

    /**
     * Gets the Android external storage directory.
     */

    public static File getExternalStorageDirectory() {
        File dir = null;
        if (HAS_PHONE_STARAGE) {
            if (Environment.MEDIA_REMOVED.equals(getExternalStorageStateInternal())) {
                dir = getPhoneStorageDirectory();
            } else {
                dir = Environment.getExternalStorageDirectory();
            }
        } else {
            dir = getAvaliedStorageDirectory();
        }
        return dir;
    }

    /**
     * Gets the current state of the external storage device.
     */

    public static String getExternalStorageState() {
        String state = Environment.MEDIA_REMOVED;
        if (HAS_PHONE_STARAGE) {
            if (Environment.MEDIA_REMOVED.equals(getExternalStorageStateInternal())) {
                state = getPhoneStorageStateInternal();
            } else {
                state = Environment.getExternalStorageState();
            }
        } else {
            state = getStorageState();
        }
        return state;
    }

    private static String getExternalStorageStateInternal() {
        return Environment.getExternalStorageState();
    }

    private static String getPhoneStorageStateInternal() {
        try {
            if (HAS_PHONE_STARAGE) {
                return (String) getPhoneStorageStateMethod.invoke(null);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e);
        }
        return Environment.MEDIA_REMOVED;
    }

    private static File getPhoneStorageDirectory() {
        try {
            if (HAS_PHONE_STARAGE) {
                return (File) getPhoneStorageDirectoryMethod.invoke(null);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e);
        }
        return null;
    }


    public static long getFileSize(File f) {
        long size = 0;
        File list[] = f.listFiles();
        if (list == null) {
            return 0;
        }
        for (int i = 0; i < list.length; i++) {
            File child = list[i];
            if (child.isDirectory()) {
                size += getFileSize(child);
            } else {
                size += child.length();
            }
        }
        return size;
    }


    public static boolean isDirSizeLargerThan(File f, long size) {
        long total = 0;
        File list[] = f.listFiles();
        if (null != list) {
            for (int i = 0; i < list.length; i++) {
                File child = list[i];
                total += child.length();
                if (total > size) {
                    break;
                }
            }
        }
        return total > size;
    }

    @SuppressWarnings("deprecation")
    static long getExternalStoreAvaliedSize(String path) {
        StatFs state = new StatFs(path);
        long cont = state.getAvailableBlocks();
        long blockSize = state.getBlockSize();
        return cont * blockSize;
    }

    private static final long MIN_AVALIED_SIZE = 1024 * 1024;

    private static boolean isExternalStorageStateAvalied() {
        return Environment.MEDIA_MOUNTED.equals(getExternalStorageStateInternal());
    }

    private static String getStorageState() {
        String status = getExternalStorageStateInternal();
        if (isExternalStorageStateAvalied()) {
            return status;
        }
        File[] files = getAllStoreRootPathFiles();
        if (files != null && files.length > 0) {
            return Environment.MEDIA_MOUNTED;
        }
        return status;
    }

    private static File getAvaliedStorageDirectory() {

        File defaultDir = Environment.getExternalStorageDirectory();
        if (isExternalStorageStateAvalied()) {
            return defaultDir;
        }

        File[] files = getAllStoreRootPathFiles();
        if (files != null) {
            for (File file : files) {
                long size = getExternalStoreAvaliedSize(file.getPath());
                if (size > MIN_AVALIED_SIZE) {
                    return file;
                }
            }
        }
        if (files != null && files.length > 0) {
            return files[0];
        }

        return defaultDir;
    }

    public static File[] getAllStoreRootPathFiles() {
        return getAllStoreRootPathFiles(MountPointCheckLevel.Normal);
    }

    public static File[] getAllStoreRootPathFiles(MountPointCheckLevel checkLevel) {
        String[] fileStrings = getStorageDirectories(checkLevel);
        File[] files = null;
        if (fileStrings != null) {
            int length = fileStrings.length;
            files = new File[length];
            for (int i = 0; i < length; i++) {
                files[i] = new File(fileStrings[i]);
            }
        }
        return files;
    }

    /**
     * Similar to android.os.Environment.getExternalStorageDirectory(), except that
     * here, we return all possible storage directories. The Environment class only
     * returns one storage directory. If you have an extended SD card, it does not
     * return the directory path. Here we are trying to return all of them.
     *
     * @return
     */
    private static String[] getStorageDirectories(MountPointCheckLevel checkLevel) {
        String[] dirs = null;
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(IOUtilities.newUtf8OrDefaultInputStreamReader(
                    new FileInputStream("/proc/mounts")));
            HashSet<String> list = new HashSet<String>();
            String line;
            while ((line = bufReader.readLine()) != null) {
                Log.d(LOG_TAG, line);
                String mountPoint = getValidMountPoint(line, checkLevel);
                if (!TextUtils.isEmpty(mountPoint)) {
                    list.add(mountPoint);
                }
            }

            dirs = new String[list.size()];
            final Iterator<String> iterator = list.iterator();
            if (iterator != null) {
                int i = 0;
                while (iterator.hasNext()) {
                    dirs[i] = iterator.next();
                    i ++;
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, e);
        } catch (IOException e) {
            Log.e(LOG_TAG, e);
        } finally {
            IOUtilities.closeStream(bufReader);
        }

        return dirs;
    }

    public enum MountPointCheckLevel {
        /***
         * find out all mount points which can be read & write,
         */
        Low,
        /***
         * find out all mount points which is vfat format or default sdcard,
         */
        Normal,
    }

    private static String getValidMountPoint(String mountLine, MountPointCheckLevel checkLevel) {

        String defaultSdcard = Environment.getExternalStorageDirectory().getPath();
        if (mountLine.contains(defaultSdcard)) {
            String mountPoint = getMountPointFrom(mountLine);
            if (TextUtils.equals(mountPoint, defaultSdcard)) {
                return mountPoint;
            }
        }
        if (checkLevel == MountPointCheckLevel.Low) {
            if (!mountLine.toLowerCase(Locale.US).contains("asec")
                    && mountLine.matches(MOUNT_LINE_VALID_REG)) {
                String mountPoint = getMountPointFrom(mountLine);
                if (mountPoint != null && mountPoint.startsWith("/")
                        && !mountPoint.toLowerCase(Locale.US).contains("vold")) {
                    return mountPoint;
                }
            }
        } else if (checkLevel == MountPointCheckLevel.Normal) {
            if (mountLine.contains("vfat") && mountLine.contains("/dev/block/vold")
                    && !mountLine.contains("/mnt/secure") && !mountLine.contains("/mnt/asec")
                    && !mountLine.contains("/mnt/obb") && !mountLine.contains("/dev/mapper")
                    && !mountLine.contains("tmpfs")) {
                return getMountPointFrom(mountLine);
            }
        }
        return null;
    }

    private static String getMountPointFrom(String mountLine) {

        String result = null;
        try {
            StringTokenizer tokens = new StringTokenizer(mountLine, " ");
            tokens.nextToken();
            result = tokens.nextToken();
        } catch (NoSuchElementException e) {
            Log.e(LOG_TAG, e);
        }
        return result;
    }

    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(getExternalStorageState());
    }

    @SuppressWarnings("deprecation")
    public static long getAvailedStorageSize() {
        File storageDir = StorageHelper.getExternalStorageDirectory();
        if (storageDir != null) {
            StatFs statFs = new StatFs(storageDir.getAbsolutePath());
            long blockSize = statFs.getBlockSize();
            long availedCount = statFs.getAvailableBlocks();
            return blockSize * availedCount;
        }
        return 0;
    }

    public static long getTotalStorageSize() {
        File storageDir = StorageHelper.getExternalStorageDirectory();
        if (storageDir != null) {
            StatFs statFs = new StatFs(storageDir.getAbsolutePath());
            long blockSize = statFs.getBlockSize();
            long blockCount = statFs.getBlockCount();
            return blockSize * blockCount;
        }
        return 0;
    }
}
