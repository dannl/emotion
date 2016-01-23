package com.qqdd.lottery.calculate;

import android.os.AsyncTask;

import com.qqdd.lottery.calculate.data.CalculatorItem;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.management.DataLoadingCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by danliu on 1/23/16.
 */
public class AlgorithmTester {

    private static class SingletonHolder {
        private static final AlgorithmTester INSTANCE = new AlgorithmTester();
    }

    public static AlgorithmTester getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static final int TEST_TIME = 1;

    public void test(@NotNull final List<LotteryRecord> mHistory,
                     @NotNull final List<CalculatorItem> calculators,
                     @NotNull DataLoadingCallback<Void> callback) {

    }

    private class Task extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }
}
