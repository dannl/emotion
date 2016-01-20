package com.qqdd.lottery.calculate;

import android.os.AsyncTask;

import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.management.DataLoadingCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danliu on 1/20/16.
 */
public class CalculatorController extends ArrayList<Calculator> {

    private CalculateTask mCalculateTask;

    public void calculate(final List<LotteryRecord> lts, final NumberTable table, final DataLoadingCallback<NumberTable> callback) {
        if (mCalculateTask != null) {
            callback.onBusy();
            return;
        }
        mCalculateTask = new CalculateTask(lts, table, callback);
        mCalculateTask.execute();
    }

    private class CalculateTask extends AsyncTask<Void, Void, Void> {

        private List<LotteryRecord> mHistory;
        private NumberTable mNumberTable;
        private DataLoadingCallback<NumberTable> mCallback;

        public CalculateTask(List<LotteryRecord> lts, NumberTable table,
                             DataLoadingCallback<NumberTable> callback) {
            mHistory = lts;
            mNumberTable = table;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < size(); i++) {
                CalculatorController.this.get(i).calculate(mHistory, mNumberTable);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mCallback.onLoaded(mNumberTable);
        }
    }
}
