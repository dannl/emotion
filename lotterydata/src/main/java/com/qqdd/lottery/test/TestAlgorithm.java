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
import com.qqdd.lottery.utils.NumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by danliu on 1/25/16.
 */
public class TestAlgorithm {

        private static final String PATH = "H://environment/code/emotion/DLT";
//    private static final String PATH = "/home/niub/Desktop/DLT";
    public static final int TEST_TIME = 1000000;
    public static final int TEST_SINCE = 4;

    public static void main(String[] args) {
        testARound();
    }

    private static void actualBuyingTest() {
        double total = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        final int testRound = 1000;
        int earnCount = 0;
        for (int i = 0; i < testRound; i++) {
            final double v = actualBuyingARound();
            total += v;
            if (v > 0) {
                earnCount++;
            }
            if (v > max) {
                max = v;
            }
            if (v < min) {
                min = v;
            }
        }
        System.out.println(
                "total: " + total + " avr: " + total / testRound + " max: " + max + " min: " + min + " earn count: " + earnCount + " earn rate: " + (((float) earnCount) / testRound));
    }

    private static double actualBuyingARound() {
        final TestResult result = testAlgorithm();
        System.out.println(result.toString());
        return result.calculateTotal() - 2 * result.totalTestCount;
    }

    private static double testARound() {
        final TestResult result = testAlgorithm();
        System.out.println(result.toString());
        return result.getExpectationOnBuying(5);
    }

    private static TestResult testRandom() {
        List<LotteryRecord> history = DataLoader.loadData(PATH);
        List<CalculatorItem> calculatorList = new ArrayList<>();
        calculatorList.add(new CalculatorItem(new AverageProbabilityCalculator()));
        final Task task = new Task(LotteryConfiguration.DLTConfiguration(), history,
                calculatorList);
        final TestResult result = task.execute();
        return result;
    }

    private static TestResult testAlgorithm() {
        List<LotteryRecord> history = DataLoader.loadData(PATH);
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
        calculatorList.add(new CalculatorItem(
                CalculatorFactory.Last4TimeOccurIncreaseCalculatorFactory.instance()
                        .createCalculator()));
        final Task task = new Task(LotteryConfiguration.DLTConfiguration(), history,
                calculatorList);
        final TestResult result = task.execute();
        return result;
    }

    private static final class TestResult {
        String title = "神一般的算法";
        long totalTestCount;
        long totalRewardCount;
        long totalMoney;
        HashMap<RewardRule.Reward, Integer> detail = new HashMap<>();
        HashMap<LotteryRecord, List<Integer>> goBackHomeRecord = new HashMap<>();
        long startTime;
        long endTime;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(title)
                    .append("\n");
            builder.append("测试次数：")
                    .append(totalTestCount)
                    .append("\n");
            builder.append("总金额：")
                    .append(totalMoney)
                    .append("\n");
            builder.append("总中奖率：")
                    .append(((double) totalRewardCount) / totalTestCount)
                    .append("\n");
            builder.append("测试速度：")
                    .append(((double) totalTestCount) / (System.currentTimeMillis() - startTime) * 1000)
                    .append("\n");
            final Set<Map.Entry<RewardRule.Reward, Integer>> entrySet = detail.entrySet();
            for (Map.Entry<RewardRule.Reward, Integer> entry : entrySet) {
                final int value = entry.getValue();
                final RewardRule.Reward key = entry.getKey();
                totalMoney += value * key.getMoney();
                builder.append(key.getTitle())
                        .append(" 奖金：")
                        .append(key.getMoney())
                        .append(" 个数:")
                        .append(value)
                        .append(" 中奖率：")
                        .append(((double) value) / totalTestCount)
                        .append("\n");
            }

            builder.append("===========when we can go home===========\n");
            for (Map.Entry<LotteryRecord, List<Integer>> entry : goBackHomeRecord.entrySet()) {
                for (Integer v : entry.getValue()) {
                    builder.append(entry.getKey()
                            .toString())
                            .append("第")
                            .append(v)
                            .append("次计算\n");
                }
            }
            return builder.toString();
        }

        public double getExpectationOnBuying(int boughtAmount) {
            double totalRate = ((double) totalRewardCount) / totalTestCount;
            double expectation = calculateExpectation(detail);
            double totalExpectation = 0;
            for (int i = 1; i <= boughtAmount; i++) {
                totalExpectation += NumUtils.C(boughtAmount, i) * expectation * i *
                        Math.pow(totalRate, i) * Math.pow(1 - totalRate, boughtAmount - i);
            }
            double earnMoney = totalExpectation - boughtAmount * 2;
            System.out.println("买" + boughtAmount + "个：赚" + earnMoney + "元");
            return earnMoney;
        }


        private double calculateExpectation(HashMap<RewardRule.Reward, Integer> detail) {
            final Set<Map.Entry<RewardRule.Reward, Integer>> entrySet = detail.entrySet();
            int totalCount = 0;
            for (Map.Entry<RewardRule.Reward, Integer> entry : entrySet) {
                final int value = entry.getValue();
                totalCount += value;
            }
            double result = 0;
            for (Map.Entry<RewardRule.Reward, Integer> entry : entrySet) {
                final float value = entry.getValue();
                result += value / totalCount * entry.getKey()
                        .getMoney();
            }
            return result;
        }

        private double calculateTotal() {
            final Set<Map.Entry<RewardRule.Reward, Integer>> entrySet = detail.entrySet();
            double result = 0;
            for (Map.Entry<RewardRule.Reward, Integer> entry : entrySet) {
                final float value = entry.getValue();
                result += value * entry.getKey()
                        .getMoney();
            }
            return result;
        }
    }

    private static class Task {

        private final List<LotteryRecord> mHistory;
        private final List<CalculatorItem> mCalculators;
        private LotteryConfiguration mConfiguration;
        private TestResult mResult;

        public Task(LotteryConfiguration configuration, List<LotteryRecord> history,
                    List<CalculatorItem> calculators) {
            mHistory = history;
            mCalculators = calculators;
            mConfiguration = configuration;
            mResult = new TestResult();
        }

        protected TestResult execute() {
            final TestResult result = mResult;
            result.startTime = System.currentTimeMillis();
            NumberTable normalTable = new NumberTable(mConfiguration.getNormalRange());
            NumberTable specialTable = new NumberTable(mConfiguration.getSpecialRange());
            final AverageProbabilityCalculator randomCalculator = new AverageProbabilityCalculator();
            randomCalculator.calculate(mHistory, normalTable, specialTable);
            final int size = mHistory.size();
            //全随机算法。
            for (int i = size / TEST_SINCE; i > 0; i--) {
                final List<LotteryRecord> subHistory = mHistory.subList(i - 1, size);
                final LotteryRecord record = mHistory.get(i);
                for (int j = 0; j < TEST_TIME; j++) {
                    result.totalTestCount++;
                    normalTable.reset();
                    specialTable.reset();
                    for (int k = 0; k < mCalculators.size(); k++) {
                        mCalculators.get(k)
                                .calculate(subHistory, normalTable, specialTable);
                    }
                    final Lottery tempResult = NumberProducer.getInstance()
                            .calculateSync(normalTable, specialTable, mConfiguration);
                    final RewardRule.Reward reward = tempResult.getReward(record);
                    final int money = reward.getMoney();
                    if (money > 0) {
                        Integer value = result.detail.get(reward);
                        if (value == null) {
                            value = 0;
                        }
                        value++;
                        result.detail.put(reward, value);
                        result.totalRewardCount++;
                        result.totalMoney += money;
                        if (money == 10000000) {
                            List<Integer> recordIt = result.goBackHomeRecord.get(record);
                            if (recordIt == null) {
                                recordIt = new ArrayList<>();
                                result.goBackHomeRecord.put(record, recordIt);
                            }
                            recordIt.add(j);
                        }
                    }
                    publishProgressLocal();
                }
            }
            result.endTime = System.currentTimeMillis();
            return result;
        }

        private long mLastPublishProgressTime = 0;

        private void publishProgressLocal() {
            if (System.currentTimeMillis() - mLastPublishProgressTime < 5000) {
                return;
            }
            mLastPublishProgressTime = System.currentTimeMillis();
            System.out.print("=========================\n" + mResult.toString() + "\n");
        }

    }

}
