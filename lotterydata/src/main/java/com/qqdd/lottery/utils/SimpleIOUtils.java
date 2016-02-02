package com.qqdd.lottery.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import cz.msebera.android.httpclient.util.CharArrayBuffer;

public class SimpleIOUtils {

    public static File getProjectRoot() {
        final String file = SimpleIOUtils.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getFile();
        final String projectRoot = file.substring(0, file.indexOf("lotterydata"));
        return new File(projectRoot);
    }

    public static void saveToFile(File file, String content, String encoding) throws IOException {
        if (!file.getParentFile()
                .exists()) {
            file.getParentFile()
                    .mkdirs();
        }
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), encoding);
            writer.write(content);
        } finally {
            writer.close();
        }
    }


    public static String loadContent(InputStream stream, String encoding) throws IOException {
        if (encoding == null) {
            encoding = System.getProperty("file.encoding", "utf-8");
        }
        Reader reader = new InputStreamReader(stream, encoding);
        CharArrayBuffer buffer = new CharArrayBuffer(stream.available());
        try {
            final int bufferSize = 4096;
            char[] tmp = new char[bufferSize];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
        } finally {
            reader.close();
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

}
