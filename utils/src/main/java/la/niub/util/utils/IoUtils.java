
package la.niub.util.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class IoUtils {

    private static final String TAG = IoUtils.class.getSimpleName();

    private static final int EOF = -1;

    private static final int BUFFER_SIZE = 1024;

    public static interface ProgressListener {

        public void progress(long current, long total);

    }

    /**
     * Copy the content of the input stream into the output stream, using a
     * temporary byte array buffer whose size is defined by
     * {@link #IO_BUFFER_SIZE}.
     *
     * @param in The input stream to copy from.
     * @param out The output stream to copy to.
     * @throws java.io.IOException If any error occurs during the copy.
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        IOUtilities.copy(in, out);
    }

    public static void copyStream(InputStream in, File outFile) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = FileUtil.openNewFileOutput(outFile);
            copyStream(in, fos);
        } finally {
            closeQuietly(fos);
        }
    }

    public static void copyStream(InputStream in, File outFile, long total, ProgressListener l)
            throws IOException {
        FileOutputStream fos = null;
        try {
            fos = FileUtil.openNewFileOutput(outFile);
            copyStream(in, fos, total, l);
        } finally {
            closeQuietly(fos);
        }
    }

    public static long copyStream(InputStream in, OutputStream out, long total, ProgressListener l)
            throws IOException {
        int read = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        long current = 0;
        while ((read = in.read(buffer)) != EOF) {
            out.write(buffer, 0, read);
            current += read;
            if (l != null) {
                l.progress(current, total);
            }
        }
        return current;
    }

    /**
     * Closes the specified stream.
     *
     * @param stream The stream to close.
     */
    public static void closeQuietly(Closeable stream) {
        IOUtilities.closeStream(stream);
    }

    public static String loadContent(final InputStream stream) throws IOException {
        return loadContent(stream, null);
    }

    /**
     * Convert an {@link InputStream} to String.
     *
     * @param stream the stream that contains data.
     * @param encoding the encoding of the data.
     * @return the result string.
     * @throws IOException an I/O error occurred.
     */
    public static String loadContent(final InputStream stream, String encoding)
            throws IOException {
        return IOUtilities.loadContent(stream, encoding);
    }

    public static byte[] loadBytes(InputStream in) {
        try {
            return IOUtilities.loadBytes(in);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将数据进行GZip压缩
     *
     * @param content
     * @return
     * @throws IOException
     */
    public static byte[] getGZIPData(String content) throws IOException {
        ByteArrayOutputStream os = null;
        GZIPOutputStream zos = null;
        byte[] bytes = null;
        try {
            os = new ByteArrayOutputStream();
            zos = new GZIPOutputStream(os);
            zos.write(content.getBytes("UTF-8"));
            zos.finish();
            bytes = os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(zos);
            closeQuietly(os);
        }
        return bytes;
    }

}
