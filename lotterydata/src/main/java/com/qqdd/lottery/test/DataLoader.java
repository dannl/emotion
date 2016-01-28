package com.qqdd.lottery.test;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.LotteryRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.util.CharArrayBuffer;

/**
 * Created by danliu on 1/25/16.
 */
public class DataLoader {

    public static List<LotteryRecord> loadData(File input) {
        final JSONArray jsonArray;
        final List<LotteryRecord> records = new ArrayList<>();
        try {
            final String content = loadContent(new FileInputStream(input), "UTF-8");
            jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject json = jsonArray.getJSONObject(i);
                //FIXME type here should be history item??
                final LotteryRecord record = HistoryItem.fromJson(json);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (JSONException e) {
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return records;
    }

    private static int IO_BUFFER_SIZE = 4096;

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
            char[] tmp = new char[IO_BUFFER_SIZE];
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
