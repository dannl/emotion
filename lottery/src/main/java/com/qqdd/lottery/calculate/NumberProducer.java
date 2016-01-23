package com.qqdd.lottery.calculate;

import android.os.AsyncTask;

import com.qqdd.lottery.data.*;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.data.management.DataLoadingCallback;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by danliu on 1/21/16.
 */
public class NumberProducer {

    private static final class SingletonHolder {
        private static final NumberProducer INSTANCE = new NumberProducer();
    }

    public static NumberProducer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final Random RANDOM = new Random();

    private CalculateTask mTask;

    private NumberProducer() {
    }


    public void calculate(@NotNull final NumberTable normals, @NotNull final NumberTable specials,
                          @NotNull final LotteryConfiguration lotteryConfiguration,
                          @NotNull final DataLoadingCallback<Lottery> callback) {
        if (mTask != null) {
            callback.onBusy();
            return;
        }
        if (!isTableValid(normals, lotteryConfiguration.getNormalSize()) || !isTableValid(specials, lotteryConfiguration.getSpecialSize())) {
            callback.onLoadFailed("请先计算概率.");
            return;
        }
        mTask = new CalculateTask(normals, specials, lotteryConfiguration, callback);
        mTask.execute();
    }

    private class CalculateTask extends AsyncTask<Void, Void, Lottery> {

        private NumberTable mNormals;
        private NumberTable mSpecials;
        private LotteryConfiguration mConfiguration;
        private DataLoadingCallback<Lottery> mCallback;

        public CalculateTask(NumberTable normals, NumberTable specials,
                             LotteryConfiguration lotteryConfiguration,
                             DataLoadingCallback<Lottery> callback) {
            mNormals = normals;
            mSpecials = specials;
            mConfiguration = lotteryConfiguration;
            mCallback = callback;
        }

        @Override
        protected Lottery doInBackground(Void... params) {
            Set<Integer> normalValues = calculateValues(mNormals, mConfiguration.getNormalSize());
            Set<Integer> specialValues = calculateValues(mSpecials, mConfiguration.getSpecialSize());
            final Lottery result = Lottery.newLotteryWithConfiguration(mConfiguration);
            assert result != null;
            result.addNormals(normalValues);
            result.addSpecials(specialValues);
            return result;
        }

        @Override
        protected void onPostExecute(Lottery lottery) {
            mCallback.onLoaded(lottery);
            mTask = null;
        }

    }

    private Set<Integer> calculateValues(NumberTable table, int size) {
        float total = 0;
        for (int i = 0; i < table.size(); i++) {
            final com.qqdd.lottery.data.Number number = table.get(i);
            total += number.getWeight();
        }
        final Set<Integer> result = new HashSet<>(size);
        while (result.size() < size) {
            float calculated = RANDOM.nextFloat() * total;
            float indexer = 0f;
            for (int i = 0; i < table.size(); i++) {
                Number number = table.get(i);
                float occ = number.getWeight();
                if (calculated >= indexer && calculated < indexer + occ) {
                    result.add(number.getValue());
                    break;
                }
                indexer += occ;
            }
        }
        return result;
    }

    private boolean isTableValid(final NumberTable table, int size) {
        int valuedNumberCount = 0;
        for (int i = 0; i < table.size(); i++) {
            final com.qqdd.lottery.data.Number number = table.get(i);
            if (number.getWeight() > 0f){
                valuedNumberCount ++;
            }
        }
        return valuedNumberCount > size;
    }



}
