
package la.niub.util.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class FileUtil {

    public static final int S_IRWXU = 00700; // rwx u
    public static final int S_IRUSR = 00400; // r-- u
    public static final int S_IWUSR = 00200; // -w- u
    public static final int S_IXUSR = 00100; // --x u

    public static final int S_IRWXG = 00070; // rwx g
    public static final int S_IRGRP = 00040;
    public static final int S_IWGRP = 00020;
    public static final int S_IXGRP = 00010;

    public static final int S_IRWXO = 00007; // rwx o
    public static final int S_IROTH = 00004;
    public static final int S_IWOTH = 00002;
    public static final int S_IXOTH = 00001;

    /** Regular expression for safe filenames: no spaces or metacharacters */
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");

    private FileUtil() {
    }

    /**
     * Check if a filename is "safe" (no metacharacters or spaces).
     * 
     * @param file The file to check
     */
    public static boolean isFilenameSafe(File file) {
        // Note, we check whether it matches what's known to be safe,
        // rather than what's known to be unsafe. Non-ASCII, control
        // characters, etc. are all unsafe by default.
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    public static int setPermissions(String file, int mode) {
        return setPermissions(file, mode, -1, -1);
    }

    public static int setPermissions(String file, int mode, int uid, int gid) {
        Class<?>[] parameterTypes = new Class<?>[] {
                String.class, int.class, int.class, int.class
        };
        Object[] parameters = new Object[] {
                file, mode, uid, gid
        };
        return (Integer) ReflectionUtils.invokeStaticMethod("android.os.FileUtils",
                "setPermissions", parameterTypes, parameters);
    }

    // 复制文件
    public static void copyFile(String src, String dest) {
        FileInputStream in = openFileInputStream(src);
        if (in == null) {
            return;
        }
        try {
            IoUtils.copyStream(in, new File(dest));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(in);
        }
    }

    public static void copyFile(File src, File dest) throws IOException {
        if (src.exists()) {
            FileChannel channel1 = new FileInputStream(src).getChannel();
            FileChannel channel2 = new FileOutputStream(dest).getChannel();
            channel1.transferTo(0, channel1.size(), channel2);
        }
    }

    public static void copyDirectory(File src, File dest) throws IOException {
        if (src.exists()) {
            dest.mkdirs();
            File[] files = src.listFiles();
            if (files == null) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    copyDirectory(file, new File(dest, file.getName()));
                } else {
                    copyFile(file, new File(dest, file.getName()));
                }
            }
        }
    }

    public static void ensureDir(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                file.mkdirs();
            }
        } else {
            file.mkdirs();
        }
    }

    public static boolean ensureMkdir(final File dir) {
        if (dir == null) {
            return false;
        }
        File tempDir = dir;
        int i = 1;
        while (tempDir.exists()) {
            tempDir = new File(dir.getParent(), dir.getName() + "(" + i + ")");
            i++;
        }
        return tempDir.mkdir();
    }

    public static void ensureParent(final File file) {
        if (null != file) {
            final File parentFile = file.getParentFile();
            if (null != parentFile && !parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
    }

    /**
     * Retrieve the main file name.
     * 
     * @param path the file name.
     * @return the main file name without the extension.
     */
    public static String getFileNameWithoutExtensionByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return getFileNameWithoutExtension(new File(path));
    }

    /**
     * Retrieve the main file name.
     * 
     * @param file the file.
     * @return the main file name without the extension.
     */
    public static String getFileNameWithoutExtension(File file) {
        if (file == null) {
            return null;
        }
        String fileName = file.getName();
        return getFileNameWithoutExtension(fileName);
    }

    /**
     * Helper method to get a filename without its extension
     * 
     * @param fileName String
     * @return String
     */
    public static String getFileNameWithoutExtension(String fileName) {
        String name = fileName;

        int index = fileName.lastIndexOf('.');
        if (index != -1) {
            name = fileName.substring(0, index);
        }

        return name;
    }

    /**
     * Retrieve the main file name.
     * 
     * @param path the file name.
     * @return the extension of the file.
     */
    public static String getExtension(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return getExtension(new File(path));
    }

    /**
     * Retrieve the extension of the file.
     * 
     * @param file the file.
     * @return the extension of the file.
     */
    public static String getExtension(File file) {
        if (null == file) {
            return null;
        }
        final String name = file.getName();
        final int index = name.lastIndexOf('.');
        String extension = "";
        if (index >= 0) {
            extension = name.substring(index + 1);
        }
        return extension;
    }

    public static boolean existsFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return existFile(new File(path));
    }

    public static boolean existFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    public static boolean deleteFileIfExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static boolean deleteFileIfExist(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static void saveToFile(File file, String text) {
        saveToFile(file, text, false, "utf-8");
    }

    public static void saveToFile(File file, String text, boolean append) {
        saveToFile(file, text, append, "utf-8");
    }

    public static void saveToFile(File file, String text, String encoding) {
        saveToFile(file, text, false, encoding);
    }

    public static void saveToFile(File file, String text, boolean append, String encoding) {
        if (file == null || TextUtils.isEmpty(text)) {
            return;
        }
        ensureParent(file);
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file, append), encoding);
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtils.closeQuietly(writer);
        }
    }

    public static String readFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return readFile(new File(path));
    }

    public static String readFile(File file) {
        String text = null;
        if (existFile(file)) {
            FileInputStream fis = openFileInputStream(file);
            if (fis != null) {
                try {
                    text = IoUtils.loadContent(fis);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IoUtils.closeQuietly(fis);
                }
            }
        }
        return text;
    }

    public static byte[] readFileBytes(File file) {
        byte[] data = null;
        FileInputStream fis = openFileInputStream(file);
        if (fis != null) {
            data = IoUtils.loadBytes(fis);
            IoUtils.closeQuietly(fis);
        }
        return data;
    }

    public static Map<String, String> readConfig(File file) {
        Map<String, String> map = new HashMap<String, String>();
        String text = readFile(file);
        if (TextUtils.isEmpty(text)) {
            return map;
        }

        String[] lines = text.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (TextUtils.isEmpty(line)) {
                continue;
            } else if (line.startsWith("#")) {
                continue;
            }
            String[] array = line.split("=", 2);
            map.put(array[0].trim(), array[1].trim());
        }
        return map;
    }

    private static FileInputStream openFileInputStream(String path) {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static FileInputStream openFileInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static FileOutputStream openFileOutputStream(String path) {
        try {
            return new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static FileOutputStream openNewFileOutput(File file) throws IOException {
        deleteFileIfExist(file);
        ensureParent(file);
        file.createNewFile();
        return new FileOutputStream(file);
    }

    public static File getUserDir() {
        String path = System.getProperty("user.dir");
        return new File(path);
    }

    public static File getUserHome() {
        String path = System.getProperty("user.home");
        return new File(path);
    }

}
