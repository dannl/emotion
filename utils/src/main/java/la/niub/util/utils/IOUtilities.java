package la.niub.util.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcel;
import android.text.TextUtils;

import org.apache.http.util.CharArrayBuffer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;

public final class IOUtilities {
    private static final String LOG_TAG = "IOUtilities";

    private static final int IO_BUFFER_SIZE = 4 * 1024;

    /**
     * Copy the content of the input stream into the output stream, using a temporary
     * byte array buffer whose size is defined by {@link #IO_BUFFER_SIZE}.
     *
     * @param in The input stream to copy from.
     * @param out The output stream to copy to.
     *
     * @throws java.io.IOException If any error occurs during the copy.
     */

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    /**
     * Please use {@link #closeSQLiteDatabase(SQLiteDatabase)}.
     */
    @Deprecated
    public static void closeStream(SQLiteDatabase db) {
        closeSQLiteDatabase(db);
    }

    //Some one use this method to close a cursor, but cursor isn't extends interface Closeable before 4.1,
    //so add this method make it works before 4.1
    /**
     * Please use {@link #closeCursor(Cursor)}.
     */
    @Deprecated
    public static void closeStream(Cursor cursor) {
        closeCursor(cursor);
    }

    /**
     * Closes the specified stream.
     *
     * @param stream The stream to close.
     */

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Could not close stream", e);
            }
        }
    }

    /**
     * Close an {@link SQLiteDatabase}.
     *
     * <br/>DO NOT close an database that you plan to reuse.
     * @param db the database to close.
     */
    public static void closeSQLiteDatabase(SQLiteDatabase db) {
        if (db != null) {
            try {
                db.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Could not close db", e);
            }
        }
    }

    /**
     * Close the cursor if necessary.
     *
     * <br/>DO NOT close an cursor that you plan to reuse.
     * @param cursor the cursor to close.
     */
    public static void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            try {
                cursor.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Could not close cursor", e);
            }
        }
    }


    public static String loadContent(InputStream stream,String encoding) throws IOException{
        if (TextUtils.isEmpty(encoding)) {
            encoding = System.getProperty("file.encoding", "utf-8");
        }
        Reader reader = new InputStreamReader(stream, encoding);
        CharArrayBuffer buffer = new CharArrayBuffer(stream.available());
        try {
            char[] tmp = new char[IO_BUFFER_SIZE];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
        } finally {
            closeStream(reader);
        }
        int start = 0;
        if ("utf-8".equalsIgnoreCase(encoding) && buffer.length() > 0) {
            if (buffer.charAt(0) == '\uFEFF') {
                //skip utf-8 file BOM
                start = 1;
            }
        }
        return buffer.substring(start, buffer.length());
    }

    public static void saveToFile(File file,String content,String encoding) throws IOException{
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream(file),encoding);
            writer.write(content);
        } finally {
            closeStream(writer);
        }
    }

    public static void copyFile(File src, File dest) throws IOException {
        if(src.exists()) {
            FileChannel channel1 = new FileInputStream(src).getChannel();
            FileChannel channel2 = new FileOutputStream(dest).getChannel();
            channel1.transferTo(0, channel1.size(), channel2);
        }
    }

    /**
     * Deletes a file.
     *
     * <p>If Java impl fails, we will call linux command to do so.</p>
     * @param file the file to delete.
     * @param waitUntilDone whether wait until the linux command returns.
     */

    public static void deleteFile(File file, boolean waitUntilDone) {
        boolean success = file.delete();
        if (!success) {
            try {
                String[] args = new String[] {
                        "rm",
                        "-rf",
                        file.toString()
                };
                Process proc = Runtime.getRuntime().exec(args);
                if (waitUntilDone) {
                    int exitCode = proc.waitFor();
                    Log.d(LOG_TAG, "Linux rm -rf %s: %d.", file, exitCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes a file.
     *
     * <p>If Java impl fails, we will call linux command to do so.</p>
     * @param file the file to delete.
     */

    public static void deleteFile(File file) {
        deleteFile(file, true);
    }

    /**
     * Deletes a file.
     *
     * <p>If Java impl fails, we will call linux command to do so.</p>
     * @param path the path to the file to delete.
     * @param waitUntilDone whether wait until the linux command returns.
     */

    public static void deleteFile(String path, boolean waitUntilDone) {
        deleteDir(new File(path), waitUntilDone);
    }

    /**
     * Deletes a file.
     *
     * <p>If Java impl fails, we will call linux command to do so.</p>
     * @param path the path to the file to delete.
     */

    public static void deleteFile(String path) {
        deleteDir(new File(path));
    }


    public static boolean ensureDir(File file) {
        boolean result = false;
        if(file != null) {
            if(file.exists()) {
                result = true;
            }
            else {
                result = file.mkdirs();
            }
        }
        return result;
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

    public static byte[] loadBytes(InputStream stream) throws IOException{
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            byte[] tmp = new byte[IO_BUFFER_SIZE];
            int l;
            while ((l = stream.read(tmp)) != -1) {
                bytes.write(tmp, 0, l);
            }
        } finally {
            closeStream(stream);
        }
        return bytes.toByteArray();
    }

    /**
     * Clean a specified directory.
     * @param dir the directory to clean.
     */
    public static void cleanDir(final File dir) {
        deleteDir(dir, false);
    }

    /**
     * Clean a specified directory.
     * @param dir the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void cleanDir(final File dir, final FilenameFilter filter) {
        deleteDir(dir, false, filter);
    }

    /**
     * Clean a specified directory.
     * @param dir the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void cleanDir(final File dir, final FileFilter filter) {
        deleteDir(dir, false, filter);
    }

    public static void deleteDir(final String dir) {
        deleteDir(new File(dir));
    }

    /**
     * Delete a specified directory.
     * @param dir the directory to clean.
     */
    public static void deleteDir(final File dir) {
        deleteDir(dir, true);
    }

    /**
     * Delete a specified directory.
     * @param dir the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final FileFilter filter) {
        deleteDir(dir, true, filter);
    }

    /**
     * Delete a specified directory.
     * @param dir the directory to clean.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final FilenameFilter filter) {
        deleteDir(dir, true, filter);
    }


    /**
     * Delete a specified directory.
     * @param dir the directory to clean.
     * @param removeDir true to remove the {@code dir}.
     */
    public static void deleteDir(final File dir, final boolean removeDir) {
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir);
                    } else {
                        file.delete();
                    }
                }
            }
            if (removeDir) {
                dir.delete();
            }
        }
    }

    /**
     * Delete a specified directory.
     * @param dir the directory to clean.
     * @param removeDir true to remove the {@code dir}.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final boolean removeDir, final FileFilter filter) {
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles(filter);
            if (files != null) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir, filter);
                    } else {
                        file.delete();
                    }
                }
            }
            if (removeDir) {
                dir.delete();
            }
        }
    }

    /**
     * Delete a specified directory.
     * @param dir the directory to clean.
     * @param removeDir true to remove the {@code dir}.
     * @param filter the filter to determine which file or directory to delete.
     */
    public static void deleteDir(final File dir, final boolean removeDir, final FilenameFilter filter) {
        if (dir != null && dir.isDirectory()) {
            final File[] files = dir.listFiles(filter);
            if (files != null) {
                for (final File file : files) {
                    if (file.isDirectory()) {
                        deleteDir(file, removeDir, filter);
                    } else {
                        file.delete();
                    }
                }
            }
            if (removeDir) {
                dir.delete();
            }
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

    public static String readFileText(String file) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return loadContent(stream, "utf-8");
        } catch (Exception e) {
            Log.w(e);
        } catch(OutOfMemoryError ee){
            Log.w(ee);
        }finally {
            closeStream(stream);
        }
        return null;
    }

    /**
     * Clear content of a file.
     * If the file doesn't exist, it will return false.
     *
     * @param file the file to clear.
     *
     * @return true if file becomes empty, false otherwise.
     */
    public static boolean clearFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }

        if (file.length() == 0) {
            return true;
        }

        boolean result = true;
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write("".getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            result = false;
            Log.e(e.toString());
        } catch (IOException e) {
            result = false;
            Log.e(e.toString());
        } finally {
            closeStream(out);
        }
        return result;
    }

    public static String loadFromAssets(Context context, String fileName) {
        if(TextUtils.isEmpty(fileName)){
            return "";
        }
        try {
            return loadContent(context.getAssets().open(fileName), "utf-8");
        }
        catch (Exception e) {
            Log.e(e.toString());
            return "";
        }
    }

    public static void writeBundleToStream(Bundle data, OutputStream stream) throws IOException {
        final Parcel parcel = Parcel.obtain();
        data.writeToParcel(parcel, 0);
        stream.write(parcel.marshall());
        parcel.recycle();
    }

    public static Bundle readBundleFromStream(InputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(stream, out);
        Parcel parcel = Parcel.obtain();

        byte[] data = out.toByteArray();
        parcel.unmarshall(data, 0, data.length);
        parcel.setDataPosition(0);

        Bundle bundle = new Bundle();
        bundle.readFromParcel(parcel);
        parcel.recycle();

        return bundle;
    }

    public static InputStreamReader newUtf8OrDefaultInputStreamReader(InputStream stream) {
        try {
            return new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new InputStreamReader(stream);
        }
    }

    public static OutputStreamWriter newUtf8OrDefaultOutputStreamWriter(OutputStream stream) {
        try {
            return new OutputStreamWriter(stream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new OutputStreamWriter(stream);
        }
    }
}
