package com.qqdd.lottery.data.management;

import android.os.AsyncTask;

import com.example.niub.utils.FileUtils;
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

    private class LoadTask extends AsyncTask<Void, Void, List<HistoryItem>> {


        private final Lottery.Type mType;
        private final DataLoadingCallback<List<HistoryItem>> mCallback;

        public LoadTask(Lottery.Type type,
                        DataLoadingCallback<List<HistoryItem>> callback) {
            mType = type;
            mCallback = callback;
        }

        @Override
        protected List<HistoryItem> doInBackground(Void... params) {
            return mHistoryManager.load(mType);
        }

        @Override
        protected void onPostExecute(List<HistoryItem> historyItems) {
            if (historyItems == null) {
                mCallback.onLoadFailed("failed to load history");
            } else {
                mCallback.onLoaded(historyItems);
            }
            mTaskCache.put(mType, null);
        }
    }

}
