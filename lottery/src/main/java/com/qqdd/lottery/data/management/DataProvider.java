package com.qqdd.lottery.data.management;

import android.os.AsyncTask;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;

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

import la.niub.util.utils.IOUtilities;
import la.niub.util.utils.StorageHelper;

/**
 * Created by danliu on 2016/1/19.
 */
public class DataProvider {

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;

    private static class SingletonHolder {
        private static final DataProvider INSTANCE = new DataProvider();
    }

    public static DataProvider getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private DataProvider() {
    }

    private HashMap<Lottery.Type, LoadTask> mTaskCache = new HashMap<>();
    private HashMap<Lottery.Type, List<HistoryItem>> mHistoryCache = new HashMap<>();

    public void load(@NotNull Lottery.Type type, @NotNull final DataLoadingCallback<List<HistoryItem>> callback) {
        if (mHistoryCache.get(type) != null) {
            callback.onLoaded(mHistoryCache.get(type));
            return;
        }
        if (mTaskCache.get(type) != null) {
            callback.onBusy();
            return;
        }
        DataSource dataSource;
        if (type == Lottery.Type.DLT) {
            dataSource = new DLTSource();
        } else if (type == Lottery.Type.SSQ) {
            dataSource = new SSQSource();
        } else {
            callback.onLoadFailed("不支持的类型!");
            return;
        }
        final LoadTask task = new LoadTask(type, dataSource, callback);
        mTaskCache.put(type, task);
        task.execute();
    }

    public static File getCacheFile(Lottery.Type type) {
        final File cacheDir = new File(StorageHelper.getExternalStorageDirectory(), "Ltt");
        return new File(cacheDir, type.toString());
    }

    private class LoadTask {

        private DataSource mDataSource;
        private Lottery.Type mType;
        private DataLoadingCallback<List<HistoryItem>> mCallback;

        LoadTask(final Lottery.Type type, final DataSource dataSource,
                 DataLoadingCallback<List<HistoryItem>> callback) {
            mType = type;
            mDataSource = dataSource;
            mCallback = callback;
        }

        public void execute() {
            final File cacheFile = getCacheFile(mType);
            if (cacheFile.exists()) {
                mLoadFromLocalTask.execute();
            } else {
                mDataSource.getAll(new DataLoadingCallback<List<HistoryItem>>() {
                    @Override
                    public void onLoaded(List<HistoryItem> result) {
                        mHistoryCache.put(mType, result);
                        mSaveToLocalTask.execute();
                    }

                    @Override
                    public void onProgressUpdate(Object... progress) {

                    }

                    @Override
                    public void onLoadFailed(String err) {
                        mCallback.onLoadFailed(err);
                        mTaskCache.put(mType, null);
                    }

                    @Override
                    public void onBusy() {
                        mCallback.onBusy();
                        mTaskCache.put(mType, null);
                    }
                });
            }
        }

        private AsyncTask<Void, Void, Void> mSaveToLocalTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final List<HistoryItem> records = mHistoryCache.get(mType);
                final JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < records.size(); i++) {
                    jsonArray.put(records.get(i)
                            .toJson());
                }
                try {
                    IOUtilities.saveToFile(getCacheFile(mType), jsonArray.toString(), "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mCallback.onLoaded(mHistoryCache.get(mType));
                mTaskCache.put(mType, null);
            }
        };

        private AsyncTask<Void, Void, List<HistoryItem>> mLoadFromLocalTask = new AsyncTask<Void, Void, List<HistoryItem>>() {
            @Override
            protected List<HistoryItem> doInBackground(Void... params) {
                final File cacheFile = getCacheFile(mType);
                try {
                    final String content = IOUtilities.loadContent(new FileInputStream(cacheFile),
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
                    return records;
                } catch (IOException e) {
                } catch (JSONException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(final List<HistoryItem> lotteryRecords) {
                if (lotteryRecords == null || lotteryRecords.isEmpty()) {
                    mCallback.onLoadFailed("load from local failed.");
                    mTaskCache.put(mType, null);
                } else {
                    final HistoryItem firstCache = lotteryRecords.get(0);
                    final Date date = firstCache.getDate();
                    final long deltaTime = System.currentTimeMillis() - date.getTime();
                    if (deltaTime < 2 * ONE_DAY
                            //周3是3.
                            || (date.getDay() == 3 && deltaTime < 3 * ONE_DAY)) {
//                        mDLTs = lotteryRecords;
                        mHistoryCache.put(mType, lotteryRecords);
                        mCallback.onLoaded(mHistoryCache.get(mType));
                        mTaskCache.put(mType, null);
                        return;
                    }
                    mDataSource.getNewSince(lotteryRecords.get(0), new DataLoadingCallback<List<HistoryItem>>() {
                        @Override
                        public void onLoaded(List<HistoryItem> result) {
                            lotteryRecords.addAll(0, result);
                            mHistoryCache.put(mType, lotteryRecords);
                            mSaveToLocalTask.execute();
                        }

                        @Override
                        public void onLoadFailed(String err) {
                            mCallback.onLoadFailed(err);
                            mTaskCache.put(mType, null);
                        }

                        @Override
                        public void onBusy() {
                            mCallback.onBusy();
                            mTaskCache.put(mType, null);
                        }

                        @Override
                        public void onProgressUpdate(Object... progress) {

                        }
                    });
                }
            }

        };

    }
}
