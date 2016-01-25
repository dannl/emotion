package com.qqdd.lottery.test;

import com.qqdd.lottery.calculate.data.CalculatorFactory;
import com.qqdd.lottery.calculate.data.CalculatorItem;
import com.qqdd.lottery.calculate.data.calculators.AverageProbabilityCalculator;
import com.qqdd.lottery.calculate.data.calculators.SelectionIncreaseCalculator;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.RewardRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by danliu on 1/25/16.
 */
public class TestAlgorithm {

    public static void main(String[] args) {
        List<LotteryRecord> history = DataLoader.loadData("/home/niub/Desktop/DLT");
        List<CalculatorItem> calculatorList = new ArrayList<>();
        calculatorList.add(new CalculatorItem(
                CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                        .createCalculator()));
        final SelectionIncreaseCalculator selectionIncrease = CalculatorFactory.SelectionIncreaseCalculatorFactory.instance()
                .createCalculator();
        selectionIncrease.addNormal(4);
        selectionIncrease.addNormal(15);
        selectionIncrease.addNormal(10);
        selectionIncrease.addNormal(22);
        selectionIncrease.addNormal(8);
        selectionIncrease.addNormal(29);
        selectionIncrease.addNormal(6);
        selectionIncrease.addNormal(26);
        selectionIncrease.addSpecial(1);
        selectionIncrease.addSpecial(4);
        //calculatorList.add(new CalculatorItem(selectionIncrease));
        calculatorList.add(new CalculatorItem(
                CalculatorFactory.SameNumberCalculatorFactory.instance()
                        .createCalculator()));
        final String result = new Task(LotteryConfiguration.DLTConfiguration(), history, calculatorList).doInBackground();
        System.out.print("\r" + result + "\n测试完毕!");

    }

    public static final int TEST_TIME = 1000000;
    public static final int TEST_SINCE = 200;

    private static final String RESULT_FORMAT = "%s：\n" +
            "正在基于%s测试" +
            "\n" +
            "计算速度(个/秒)：%s\n" +
            "总次数: %s\n" +
            "中奖率: %s\n" +
            "中奖次数累计：%s\n" +
            "中奖金额累计：%s\n" +
            "详情：\n%s\n";

    private static class Task {

        private final List<LotteryRecord> mHistory;
        private final List<CalculatorItem> mCalculators;
        private String mRandomResult;
        private String mAlgorithmResult;
        private HashMap<RewardRule.Reward, Integer> mRandomResultDetail;
        private HashMap<RewardRule.Reward, Integer> mAlgorithmResultDetail;
        private LotteryConfiguration mConfiguration;
        private long mStartTime;

        public Task(LotteryConfiguration configuration, List<LotteryRecord> history, List<CalculatorItem> calculators) {
            mHistory = history;
            mCalculators = calculators;
            mRandomResult = String.format(RESULT_FORMAT, "全随机结果", "无", 0, 0, 0, 0, 0, "没有详情");
            mAlgorithmResult = String.format(RESULT_FORMAT, "使用算法结果", "无", 0, 0, 0, 0, 0, "没有详情");
            mRandomResultDetail = new HashMap<>();
            mAlgorithmResultDetail = new HashMap<>();
            mConfiguration = configuration;
        }

        protected String doInBackground() {
            mStartTime = System.currentTimeMillis();
            NumberTable normalTable = new NumberTable(mConfiguration.getNormalRange());
            NumberTable specialTable = new NumberTable(mConfiguration.getSpecialRange());
            final AverageProbabilityCalculator randomCalculator = new AverageProbabilityCalculator();
            randomCalculator.calculate(mHistory, normalTable, specialTable);
            final int size = mHistory.size();
            //全随机算法。
            long totalTime = 0;
//            for (int i = size / TEST_SINCE; i > 0; i--) {
//                final LotteryRecord record = mHistory.get(i - 1);
//                for (int j = 0; j < TEST_TIME; j++) {
//                    totalTime++;
//                    final Lottery tempResult = NumberProducer.getInstance().calculateSync(normalTable, specialTable, mConfiguration);
//                    final RewardRule.Reward reward = tempResult.getReward(record);
//                    final int money = reward.getMoney();
//                    if (money > 0) {
//                        Integer value = mRandomResultDetail.get(reward);
//                        if (value == null) {
//                            value = 0;
//                        }
//                        value++;
//                        mRandomResultDetail.put(reward, value);
//                    }
//                    mRandomResult = formatResult("全随机算法", totalTime, record, mRandomResultDetail);
//                    publishProgressLocal();
//                }
//            }
            totalTime = 0;
            for (int i = size / TEST_SINCE; i > 0; i--) {
                final List<LotteryRecord> subHistory = mHistory.subList(i - 1, size);
                final LotteryRecord record = mHistory.get(i);
                for (int j = 0; j < TEST_TIME; j++) {
                    normalTable = new NumberTable(mConfiguration.getNormalRange());
                    specialTable = new NumberTable(mConfiguration.getSpecialRange());
                    for (int k = 0; k < mCalculators.size(); k++) {
                        mCalculators.get(k).calculate(subHistory, normalTable, specialTable);
                    }
                    totalTime++;
                    final Lottery tempResult = NumberProducer.getInstance().calculateSync(normalTable, specialTable, mConfiguration);
                    final RewardRule.Reward reward = tempResult.getReward(record);
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
            if(System.currentTimeMillis() - mLastPublishProgressTime < 5000) {
                return;
            }
            mLastPublishProgressTime = System.currentTimeMillis();
            System.out.print("\r[" + mRandomResult + mAlgorithmResult + "]");
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
    }

}
