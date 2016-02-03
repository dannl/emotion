package com.qqdd.lottery.data.management;

import android.os.AsyncTask;

import com.qqdd.lottery.ui.view.FileUtils;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 2/2/16.
 */
public class HistoryDelegate {

    private static class SingletonHolder {
        private static final HistoryDelegate INSTANCE = new HistoryDelegate();
    }

    public static HistoryDelegate getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private History mHistoryManager;

    private HistoryDelegate() {
        mHistoryManager = new History(FileUtils.getCacheDir());
    }

    private HashMap<Lottery.Type, LoadTask> mTaskCache = new HashMap<>();

    public void load(@NotNull Lottery.Type type, @NotNull final DataLoadingCallback<List<HistoryItem>> callback) {
        if (mTaskCache.get(type) != null) {
            callback.onBusy();
            return;
        }
        final LoadTask task = new LoadTask(type, callback);
        mTaskCache.put(type, task);
        task.execute();
    }

    private class LoadResult {
        List<HistoryItem> items;
        String error;
    }

    private class LoadTask extends AsyncTask<Void, Void, LoadResult> {


        private final Lottery.Type mType;
        private final DataLoadingCallback<List<HistoryItem>> mCallback;

        public LoadTask(Lottery.Type type,
                        DataLoadingCallback<List<HistoryItem>> callback) {
            mType = type;
            mCallback = callback;
        }

        @Override
        protected LoadResult doInBackground(Void... params) {
            LoadResult result = new LoadResult();
            try {
                result.items = mHistoryManager.load(mType);
            } catch (DataSource.DataLoadingException e) {
                result.error = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.items == null) {
                mCallback.onLoadFailed(result.error);
            } else {
                mCallback.onLoaded(result.items);
            }
            mTaskCache.put(mType, null);
        }
    }

}
