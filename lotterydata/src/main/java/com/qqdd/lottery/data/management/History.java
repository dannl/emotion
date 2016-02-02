package com.qqdd.lottery.data.management;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.utils.SimpleIOUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 2/2/16.
 */
public class History {
    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    private File mRoot;

    public History(final File root) {
        mRoot = root;
    }

    private HashMap<Lottery.Type, List<HistoryItem>> mHistoryCache = new HashMap<>();

    public List<HistoryItem> load(@NotNull Lottery.Type type) {
        if (mHistoryCache.get(type) != null) {
            return mHistoryCache.get(type);
        }
        DataSource dataSource;
        if (type == Lottery.Type.DLT) {
            dataSource = new DLTSource();
        } else if (type == Lottery.Type.SSQ) {
            dataSource = new SSQSource();
        } else {
            return null;
        }
        final LoadTask task = new LoadTask(type, dataSource);
        return task.load();
    }

    private File getCacheFile(Lottery.Type type) {
        return new File(mRoot, type.toString());
    }

    private class LoadTask {

        private DataSource mDataSource;
        private Lottery.Type mType;

        LoadTask(final Lottery.Type type, final DataSource dataSource) {
            mType = type;
            mDataSource = dataSource;
        }

        public List<HistoryItem> load() {
            final File cacheFile = getCacheFile(mType);
            if (cacheFile.exists()) {
                return loadFromLocal();
            } else {
                final List<HistoryItem> result = mDataSource.getAll();
                mHistoryCache.put(mType, result);
                saveToLocal();
                return result;
            }
        }

        protected void saveToLocal() {
            final List<HistoryItem> records = mHistoryCache.get(mType);
            final JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < records.size(); i++) {
                jsonArray.put(records.get(i)
                        .toJson());
            }
            try {
                SimpleIOUtils.saveToFile(getCacheFile(mType), jsonArray.toString(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected List<HistoryItem> loadFromLocal() {
            final File cacheFile = getCacheFile(mType);
            try {
                final String content = SimpleIOUtils.loadContent(new FileInputStream(cacheFile),
                        "UTF-8");
                final JSONArray jsonArray = new JSONArray(content);
                final List<HistoryItem> records = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    final JSONObject json = jsonArray.getJSONObject(i);
                    final HistoryItem record = HistoryItem.fromJson(json);
                    if (record != null) {
                        records.add(record);
                    }
                }
                if (records.isEmpty()) {
                    return records;
                } else {
                    final HistoryItem firstCache = records.get(0);
                    final Date date = firstCache.getDate();
                    final long deltaTime = System.currentTimeMillis() - date.getTime();
                    int dayOfWeek = 3;
                    if (mType == Lottery.Type.SSQ) {
                        dayOfWeek = 4;
                    }
                    if (deltaTime < 2 * ONE_DAY
                            //周3是3.
                            || (date.getDay() == dayOfWeek && deltaTime < 3 * ONE_DAY)) {
                        //                        mDLTs = lotteryRecords;
                        mHistoryCache.put(mType, records);
                        return mHistoryCache.get(mType);
                    }
                    final List<HistoryItem> newer = mDataSource.getNewSince(records.get(0));
                    records.addAll(0, newer);
                    mHistoryCache.put(mType, records);
                    saveToLocal();
                    return records;
                }
            } catch (IOException | JSONException ignored) {
            }
            return null;
        }

    }

}
