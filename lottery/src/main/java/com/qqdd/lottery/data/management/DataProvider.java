package com.qqdd.lottery.data.management;

import com.qqdd.lottery.data.LotteryRecord;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import la.niub.util.utils.AsyncTask;

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

    private DataProvider() {}

    private List<LotteryRecord> mDLTs;
    private LoadTask mLoadTask;

    public void loadDLTHistory(@NotNull final DataLoadingCallback callback) {
        if (mDLTs != null) {
            callback.onLoaded(mDLTs);
            return;
        }
        //load from cache.
        if (mLoadTask != null) {
            callback.onBusy();
            return;
        }
        mLoadTask = new LoadTask();
    }

    private class LoadTask extends AsyncTask<Void, Void, List<LotteryRecord>> {

        @Override
        protected List<LotteryRecord> doInBackground(Void... params) {
            return null;
        }
    }

}
