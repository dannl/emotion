package com.qqdd.lottery.data.management;

import android.os.AsyncTask;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryRecord;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import la.niub.util.utils.IOUtilities;
import la.niub.util.utils.StorageHelper;

/**
 * Created by danliu on 2016/1/19.
 */
public class DataProvider {

    private static class SingletonHolder {
        private static final DataProvider INSTANCE = new DataProvider();
    }

    public static DataProvider getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private DataProvider() {
    }

    private List<LotteryRecord> mDLTs;
    private LoadTask mLoadTask;

    public void loadDLT(@NotNull final DataLoadingCallback callback) {
        if (mDLTs != null) {
            callback.onLoaded(mDLTs);
            return;
        }
        if (mLoadTask != null) {
            callback.onBusy();
            return;
        }
        mLoadTask = new LoadTask(Lottery.Type.DLT, new DLTSource(), callback);
        mLoadTask.execute();
    }

    public static File getCacheFile(Lottery.Type type) {
        final File cacheDir = new File(StorageHelper.getExternalStorageDirectory(), "Ltt");
        return new File(cacheDir, type.toString());
    }

    private class LoadTask {

        private DataSource mDataSource;
        private Lottery.Type mType;
        private DataLoadingCallback mCallback;

        LoadTask(final Lottery.Type type, final DataSource dataSource,
                 DataLoadingCallback callback) {
            mType = type;
            mDataSource = dataSource;
            mCallback = callback;
        }

        public void execute() {
            final File cacheFile = getCacheFile(mType);
            if (cacheFile.exists()) {
                mLoadFromLocalTask.execute();
            } else {
                mDataSource.getAll(new DataLoadingCallback<List<LotteryRecord>>() {
                    @Override
                    public void onLoaded(List<LotteryRecord> result) {
                        mDLTs = result;
                        mSaveToLocalTask.execute();
                    }

                    @Override
                    public void onProgressUpdate(Object... progress) {

                    }

                    @Override
                    public void onLoadFailed(String err) {
                        mCallback.onLoadFailed(err);
                        mLoadTask = null;
                    }

                    @Override
                    public void onBusy() {
                        mCallback.onBusy();
                        mLoadTask = null;
                    }
                });
            }
        }

        private AsyncTask<Void, Void, Void> mSaveToLocalTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final List<LotteryRecord> records = mDLTs;
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
                mCallback.onLoaded(mDLTs);
                mLoadTask = null;
            }
        };

        private AsyncTask<Void, Void, List<LotteryRecord>> mLoadFromLocalTask = new AsyncTask<Void, Void, List<LotteryRecord>>() {
            @Override
            protected List<LotteryRecord> doInBackground(Void... params) {
                final File cacheFile = getCacheFile(mType);
                try {
                    final String content = IOUtilities.loadContent(new FileInputStream(cacheFile),
                            "UTF-8");
                    final JSONArray jsonArray = new JSONArray(content);
                    final List<LotteryRecord> records = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        final JSONObject json = jsonArray.getJSONObject(i);
                        final LotteryRecord record = HistoryItem.fromJson(json);
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
            protected void onPostExecute(final List<LotteryRecord> lotteryRecords) {
                if (lotteryRecords == null || lotteryRecords.isEmpty()) {
                    mCallback.onLoadFailed("load from local failed.");
                    mLoadTask = null;
                } else {
                    mDataSource.getNewSince(lotteryRecords.get(0), new DataLoadingCallback<List<LotteryRecord>>() {
                        @Override
                        public void onLoaded(List<LotteryRecord> result) {
                            lotteryRecords.addAll(0, result);
                            mDLTs = lotteryRecords;
                            mSaveToLocalTask.execute();
                        }

                        @Override
                        public void onLoadFailed(String err) {
                            mCallback.onLoadFailed(err);
                            mLoadTask = null;
                        }

                        @Override
                        public void onBusy() {
                            mCallback.onBusy();
                            ;
                            mLoadTask = null;
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
