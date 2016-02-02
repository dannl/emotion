package com.qqdd.lottery.data.management;

import android.os.AsyncTask;

import com.example.niub.utils.FileUtils;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import la.niub.util.utils.UIUtil;

/**
 * Created by danliu on 2/2/16.
 */
public class CalculationDelegate {


    private static class SingletonHolder {
        private static final CalculationDelegate INSTANCE = new CalculationDelegate();
    }

    public static CalculationDelegate getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Calculation mCalculation;
    private HashMap<Lottery.Type, CalculateTask> mTaskCache;

    private static final DecimalFormat PROGRESS_FORMAT = new DecimalFormat("0.0");

    private CalculationDelegate() {
        mCalculation = new Calculation(FileUtils.getCacheDir());
        mTaskCache = new HashMap<>();
    }

    public void calculate(final List<HistoryItem> lts, final Lottery.Type type,
                          final int resultSize, final int loopCount,
                          final DataLoadingCallback<List<Lottery>> callback) {
        if (mTaskCache.get(type) != null) {
            callback.onBusy();
            return;
        }
        final CalculateTask task = new CalculateTask(lts, type, resultSize, loopCount, callback);
        mTaskCache.put(type, task);
        task.execute();
    }

    private class CalculateTask extends AsyncTask<Void, Void, List<Lottery>> {

        private final List<HistoryItem> mHistory;
        private final Lottery.Type mType;
        private final int mResultSize;
        private final int mLoopCount;
        private final DataLoadingCallback<List<Lottery>> mCallback;

        public CalculateTask(List<HistoryItem> lts, Lottery.Type type, int resultSize,
                             int loopCount, DataLoadingCallback<List<Lottery>> callback) {
            mHistory = lts;
            mType = type;
            mResultSize = resultSize;
            mLoopCount = loopCount;
            mCallback = callback;
        }

        @Override
        protected List<Lottery> doInBackground(Void... params) {
            return mCalculation.calculate(mHistory, new ProgressCallback() {
                @Override
                public void onProgressUpdate(final String progress) {
                    UIUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onProgressUpdate(PROGRESS_FORMAT.format(Double.parseDouble(progress) * 100));
                        }
                    });
                }
            }, mLoopCount, mType, mResultSize);
        }

        @Override
        protected void onPostExecute(List<Lottery> lotteries) {
            if (lotteries == null || lotteries.isEmpty()) {
                mCallback.onLoadFailed("failed to calculate.");
            } else {
                mCallback.onLoaded(lotteries);
            }
            mTaskCache.put(mType, null);
        }
    }


}
