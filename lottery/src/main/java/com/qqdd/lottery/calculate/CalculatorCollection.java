package com.qqdd.lottery.calculate;

import android.os.AsyncTask;

import com.qqdd.lottery.calculate.data.CalculatorItem;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.management.DataLoadingCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danliu on 1/20/16.
 */
public class CalculatorCollection extends ArrayList<CalculatorItem> {

    private CalculateTask mCalculateTask;

    public void calculate(final List<LotteryRecord> lts, final NumberTable normalTable, final NumberTable specialTable, final DataLoadingCallback<NumberTable> callback) {
        if (mCalculateTask != null) {
            callback.onBusy();
            return;
        }
        mCalculateTask = new CalculateTask(lts, normalTable, specialTable, callback);
        mCalculateTask.execute();
    }

    private class CalculateTask extends AsyncTask<Void, Void, Void> {

        private List<LotteryRecord> mHistory;
        private NumberTable mNormalTable;
        private NumberTable mSpecialTable;
        private DataLoadingCallback<NumberTable> mCallback;

        public CalculateTask(List<LotteryRecord> lts, NumberTable normalTable, NumberTable specialTable,
                             DataLoadingCallback<NumberTable> callback) {
            mHistory = lts;
            mNormalTable = normalTable;
            mSpecialTable = specialTable;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < size(); i++) {
                CalculatorCollection.this.get(i).calculate(mHistory, mNormalTable, mSpecialTable);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCalculateTask = null;
            mCallback.onLoaded(mNormalTable);
        }
    }
}
