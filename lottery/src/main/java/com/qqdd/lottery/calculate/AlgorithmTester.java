package com.qqdd.lottery.calculate;

import android.os.AsyncTask;

import com.qqdd.lottery.calculate.data.CalculatorItem;
import com.qqdd.lottery.calculate.data.NumberProducer;
import com.qqdd.lottery.calculate.data.calculators.AverageProbabilityCalculator;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.RewardRule;
import com.qqdd.lottery.data.management.DataLoadingCallback;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static final String RESULT_FORMAT = "%s：\n" +
            "正在基于%s测试" +
            "\n" +
            "计算速度(个/秒)：%s\n" +
            "总次数: %s\n" +
            "中奖率: %s\n" +
            "中奖次数累计：%s\n" +
            "中奖金额累计：%s\n" +
            "详情：\n%s\n";

    public static final int TEST_TIME = 10;
    private Task mTask;

    public void test(@NotNull final LotteryConfiguration configuration,
                     @NotNull final List<HistoryItem> mHistory,
                     @NotNull final List<CalculatorItem> calculators,
                     @NotNull DataLoadingCallback<String> callback) {
        if (mTask != null) {
            callback.onBusy();
            return;
        }
        mTask = new Task(configuration, mHistory, calculators, callback);
        mTask.execute();
    }

    private class Task extends AsyncTask<Void, Void, String> {

        private final List<HistoryItem> mHistory;
        private final List<CalculatorItem> mCalculators;
        private final DataLoadingCallback<String> mCallback;
        private String mRandomResult;
        private String mAlgorithmResult;
        private HashMap<RewardRule.Reward, Integer> mRandomResultDetail;
        private HashMap<RewardRule.Reward, Integer> mAlgorithmResultDetail;
        private LotteryConfiguration mConfiguration;
        private long mStartTime;

        public Task(LotteryConfiguration configuration, List<HistoryItem> history, List<CalculatorItem> calculators, DataLoadingCallback<String> callback) {
            mHistory = history;
            mCalculators = calculators;
            mCallback = callback;
            mRandomResult = String.format(RESULT_FORMAT, "全随机结果", "无", 0, 0, 0, 0, 0, "没有详情");
            mAlgorithmResult = String.format(RESULT_FORMAT, "使用算法结果", "无", 0, 0, 0, 0, 0, "没有详情");
            mRandomResultDetail = new HashMap<>();
            mAlgorithmResultDetail = new HashMap<>();
            mConfiguration = configuration;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            mCallback.onLoaded(aVoid);
            mTask = null;
        }

        @Override
        protected String doInBackground(Void... params) {
            mStartTime = System.currentTimeMillis();
            NumberTable normalTable = new NumberTable(mConfiguration.getNormalRange());
            NumberTable specialTable = new NumberTable(mConfiguration.getSpecialRange());
            final AverageProbabilityCalculator randomCalculator = new AverageProbabilityCalculator();
            randomCalculator.calculate(mHistory, normalTable, specialTable);
            final int size = mHistory.size();
            //全随机算法。
            long totalTime = 0;
/*            for (int i = size / 2; i > 0; i--) {
                final LotteryRecord record = mHistory.get(i - 1);
                for (int j = 0; j < CALCULATE_TIMES; j++) {
                    totalTime++;
                    final Lottery tempResult = NumberProducer.getInstance().calculateAndSave(normalTable, specialTable, mConfiguration);
                    final RewardRule.Reward reward = tempResult.calculateReward(record);
                    final int money = reward.getMoney();
                    if (money > 0) {
                        Integer value = mRandomResultDetail.get(reward);
                        if (value == null) {
                            value = 0;
                        }
                        value++;
                        mRandomResultDetail.put(reward, value);
                    }
                    mRandomResult = formatResult("全随机算法", totalTime, record, mRandomResultDetail);
                    publishProgressLocal();
                }
            }*/
            totalTime = 0;
            for (int i = size / 2; i > 0; i--) {
                final List<HistoryItem> subHistory = mHistory.subList(i - 1, size);
                final HistoryItem record = mHistory.get(i);
                for (int j = 0; j < TEST_TIME; j++) {
                    normalTable = new NumberTable(mConfiguration.getNormalRange());
                    specialTable = new NumberTable(mConfiguration.getSpecialRange());
                    for (int k = 0; k < mCalculators.size(); k++) {
                        mCalculators.get(k).calculate(subHistory, normalTable, specialTable);
                    }
                    totalTime++;
                    final Lottery tempResult = NumberProducer.getInstance().calculate(subHistory, normalTable, specialTable, mConfiguration);
                    final RewardRule.Reward reward = record.calculateReward(tempResult);
                    final int money = reward.getMoney();
                    if (money > 0) {
                        Integer value = mAlgorithmResultDetail.get(reward);
                        if (value == null) {
                            value = 0;
                        }
                        value++;
                        mAlgorithmResultDetail.put(reward, value);
                    }
                    mAlgorithmResult = formatResult("我的算法", totalTime, record, mAlgorithmResultDetail);
                    publishProgressLocal();
                }
            }
            return mRandomResult + mAlgorithmResult;
        }

        private long mLastPublishProgressTime = 0;
        private void publishProgressLocal() {
            if(System.currentTimeMillis() - mLastPublishProgressTime < 1000) {
                return;
            }
            mLastPublishProgressTime = System.currentTimeMillis();
            publishProgress();
        }


        private String formatResult(String type, long totalTime, LotteryRecord record, HashMap<RewardRule.Reward, Integer> detail) {
            StringBuilder builder = new StringBuilder();
            final Set<Map.Entry<RewardRule.Reward, Integer>> entrySet = detail.entrySet();
            int totalCount = 0;
            long totalMoney = 0;
            for (Map.Entry<RewardRule.Reward, Integer> entry : entrySet) {
                final int value = entry.getValue();
                totalCount += value;
                final RewardRule.Reward key = entry.getKey();
                totalMoney += value * key.getMoney();
                builder.append(key.getTitle()).append(" 奖金：").append(key.getMoney()).append(" ---- 个数:").append(value).append("个\n");
            }
            return String.format(RESULT_FORMAT, type, record.toString(), ((double) totalTime) / (System.currentTimeMillis() - mStartTime) * 1000, totalTime, ((double) totalCount) / totalTime, totalCount, totalMoney, builder.toString());

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mCallback.onProgressUpdate(mRandomResult + mAlgorithmResult);
        }
    }

}
