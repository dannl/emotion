package com.qqdd.lottery.calculate;

import android.os.AsyncTask;

import com.example.niub.utils.FileUtils;
import com.qqdd.lottery.calculate.data.CalculatorItem;
import com.qqdd.lottery.calculate.data.NumberProducer;
import com.qqdd.lottery.calculate.data.TimeToGoHome;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.management.DataLoadingCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import la.niub.util.utils.IOUtilities;

/**
 * Created by danliu on 1/20/16.
 */
public class CalculatorCollection extends ArrayList<CalculatorItem> {

    private CalculateTask mCalculateTask;

    private static final DecimalFormat PROGRESS_FORMAT = new DecimalFormat("#.0");

    public void calculate(final List<LotteryRecord> lts, final int resultSize, final int loopCount, final DataLoadingCallback<List<Lottery>> callback) {
        if (mCalculateTask != null) {
            callback.onBusy();
            return;
        }
        mCalculateTask = new CalculateTask(lts, resultSize, loopCount, callback);
        mCalculateTask.execute();
    }

    private class CalculateTask extends AsyncTask<Void, String, List<Lottery>> {

        private List<LotteryRecord> mHistory;
        private NumberTable mNormalTable;
        private NumberTable mSpecialTable;
        private DataLoadingCallback<List<Lottery>> mCallback;
        private int mResultSize;
        private int mLoopCount;
        private NumberProducer mNumberProducer;
        private LotteryConfiguration mConfiguration;

        public CalculateTask(List<LotteryRecord> lts, int resultSize, int loopCount,
                             DataLoadingCallback<List<Lottery>> callback) {
            mHistory = lts;
            mResultSize = resultSize;
            mLoopCount = loopCount;
            mConfiguration = lts.get(0).getLottery().getLotteryConfiguration();
            mNormalTable = new NumberTable(mConfiguration.getNormalRange());
            mSpecialTable = new NumberTable(mConfiguration.getSpecialRange());
            mCallback = callback;
            mNumberProducer = NumberProducer.getInstance();
        }

        @Override
        protected List<Lottery> doInBackground(Void... params) {
            List<Lottery> tempBuffer = new ArrayList<>(mLoopCount);
            for (int i = 0; i < mLoopCount; i++) {
                publishProgress(PROGRESS_FORMAT.format(((float) i) / mLoopCount * 100) + "%");
                mNormalTable.reset();
                mSpecialTable.reset();
                for (int j = 0; j < size(); j++) {
                    CalculatorCollection.this.get(j).calculate(mHistory, mNormalTable, mSpecialTable);
                }
                final Lottery lottery = mNumberProducer.calculate(mHistory, mNormalTable, mSpecialTable,
                        mConfiguration);
                tempBuffer.add(lottery);
            }
            final File cacheFile = new File(FileUtils.getCacheDir(), "GO_HOME_RECORD" + mLoopCount);
            TimeToGoHome ttgh = null;
            if (cacheFile.exists()) {
                try {
                    ttgh = TimeToGoHome.fromJson(IOUtilities.loadContent(new FileInputStream(cacheFile), "UTF-8"));
                } catch (IOException e) {
                }
            }
            return mNumberProducer.select(tempBuffer, mResultSize,
                    ttgh);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            mCallback.onProgressUpdate(values[0]);
        }

        @Override
        protected void onPostExecute(List<Lottery> result) {
            mCalculateTask = null;
            mCallback.onLoaded(result);
        }
    }
}
