package com.qqdd.lottery.test;

import com.qqdd.lottery.calculate.data.CalculatorFactory;
import com.qqdd.lottery.calculate.data.CalculatorItem;
import com.qqdd.lottery.calculate.data.NumberProducer;
import com.qqdd.lottery.calculate.data.TimeToGoHome;
import com.qqdd.lottery.calculate.data.calculators.AverageProbabilityCalculator;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.RewardRule;
import com.qqdd.lottery.utils.NumUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestAlgorithm {

    public static final int CALCULATE_TIMES = 100000;
    public static final int TEST_SINCE = 4;

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            testARound();
        }
//                calculateResult();
    }

    private static void calculateResult() {
        final int resultCount = 5;
        List<Lottery> result = calculate(resultCount);
        System.out.println("\nresult is: ");
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i)
                    .toString());
        }
    }

    private static List<Lottery> calculate(final int count) {
        List<LotteryRecord> history = DataLoader.loadData(getHistoryFile());
        List<CalculatorItem> calculatorList = createCalculatorList();
        final LotteryConfiguration configuration = LotteryConfiguration.DLTConfiguration();
        final NumberTable normalTable = new NumberTable(configuration.getNormalRange());
        final NumberTable specialTable = new NumberTable(configuration.getSpecialRange());
        List<Lottery> tempBuffer = new ArrayList<>();
        for (int i = 0; i < CALCULATE_TIMES; i++) {
            if (i % (CALCULATE_TIMES / 100) == 0) {
                System.out.print("\r progress: " + (((float) i) / CALCULATE_TIMES));
            }
            normalTable.reset();
            specialTable.reset();
            for (int j = 0; j < calculatorList.size(); j++) {
                calculatorList.get(j)
                        .calculate(history, normalTable, specialTable);
            }
            tempBuffer.add(NumberProducer.getInstance()
                    .calculate(history, normalTable, specialTable, configuration));
        }
        return NumberProducer.getInstance()
                .select(tempBuffer, count, loadTimeToGoHome(CALCULATE_TIMES));
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
        float maxRate = 0;
        float minRate = 1;
        for (Map.Entry<LotteryRecord, Float> entry : result.recordRate.entrySet()) {
            if (entry.getValue() > maxRate) {
                maxRate = entry.getValue();
            }
            if (entry.getValue() < minRate) {
                minRate = entry.getValue();
            }
        }
        System.out.println("max: " + maxRate + " min: " + minRate);

        StringBuilder builder = new StringBuilder();
        builder.append("=============\n");
        builder.append("回家次数统计：\n");
        for (Map.Entry<LotteryRecord, Integer> entry : result.goHomeDistribute.entrySet()) {
            builder.append(entry.getKey()
                    .toString())
                    .append(": ")
                    .append(entry.getValue())
                    .append("次")
                    .append("\n");
        }
        return result.getExpectationOnBuying(5);
    }

    private static TestResult testRandom() {
        List<LotteryRecord> history = DataLoader.loadData(getHistoryFile());
        List<CalculatorItem> calculatorList = new ArrayList<>();
        calculatorList.add(new CalculatorItem(new AverageProbabilityCalculator()));
        final Task task = new Task(LotteryConfiguration.DLTConfiguration(), history,
                calculatorList);
        final TestResult result = task.execute();
        return result;
    }

    private static TestResult testAlgorithm() {
        List<LotteryRecord> history = DataLoader.loadData(getHistoryFile());
        List<CalculatorItem> calculatorList = createCalculatorList();
        final Task task = new Task(LotteryConfiguration.DLTConfiguration(), history,
                calculatorList);
        final TestResult result = task.execute();
        return result;
    }

    private static File getHistoryFile() {
        final File rootFolder = getProjectRoot();
        final File history = new File(rootFolder, "DLT");
        return history;
    }

    private static File getProjectRoot() {
        final String file = TestAlgorithm.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getFile();
        final String projectRoot = file.substring(0, file.indexOf("lotterydata"));
        return new File(projectRoot);
    }

    private static File getGoHomeRecordFile(int testTime) {
        return new File(getProjectRoot(), "GO_HOME_RECORD" + testTime);
    }

    private static TimeToGoHome loadTimeToGoHome(int testTime) {
        try {
            final String content = DataLoader.loadContent(
                    new FileInputStream(getGoHomeRecordFile(testTime)), "UTF-8");
            TimeToGoHome result = TimeToGoHome.fromJson(content);
            if (result != null) {
                return result;
            }
        } catch (IOException e) {
        }
        return new TimeToGoHome(testTime);
    }


    //        final SelectionIncreaseCalculator selectionIncrease = CalculatorFactory.SelectionIncreaseCalculatorFactory.instance()
    //                .createCalculator();
    //        selectionIncrease.addNormal(4);
    //        selectionIncrease.addNormal(15);
    //        selectionIncrease.addNormal(10);
    //        selectionIncrease.addNormal(22);
    //        selectionIncrease.addNormal(8);
    //        selectionIncrease.addNormal(29);
    //        selectionIncrease.addNormal(6);
    //        selectionIncrease.addNormal(26);
    //        selectionIncrease.addSpecial(1);
    //        selectionIncrease.addSpecial(4);
    //calculatorList.add(new CalculatorItem(selectionIncrease));

    private static List<CalculatorItem> createCalculatorList() {
        List<CalculatorItem> calculatorList = new ArrayList<>();
        calculatorList.add(new CalculatorItem(
                CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                        .createCalculator()));
        calculatorList.add(new CalculatorItem(
                CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
                        .createCalculator()));
        return calculatorList;
    }

    private static final class TestResult {
        String title = "我的算法";
        long totalTestCount;
        long totalRewardCount;
        long totalMoney;
        HashMap<RewardRule.Reward, Integer> detail = new HashMap<>();
        TimeToGoHome mTimeToGoHome;
        long startTime;
        long endTime;
        private HashMap<LotteryRecord, Integer> goHomeDistribute = new HashMap<>();
        HashMap<LotteryRecord, Float> recordRate = new HashMap<>();

        public TestResult(final int testTime) {
            mTimeToGoHome = loadTimeToGoHome(testTime);
        }

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
            mResult = new TestResult(CALCULATE_TIMES);
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
                int roundRewardCount = 0;
                for (int j = 0; j < CALCULATE_TIMES; j++) {
                    result.totalTestCount++;
                    normalTable.reset();
                    specialTable.reset();
                    for (int k = 0; k < mCalculators.size(); k++) {
                        mCalculators.get(k)
                                .calculate(subHistory, normalTable, specialTable);
                    }
                    final Lottery tempResult = com.qqdd.lottery.calculate.data.NumberProducer.getInstance()
                            .calculate(subHistory, normalTable, specialTable, mConfiguration);
                    final RewardRule.Reward reward = tempResult.getReward(record);
                    final int money = reward.getMoney();
                    if (money > 0) {
                        roundRewardCount++;
                        Integer value = result.detail.get(reward);
                        if (value == null) {
                            value = 0;
                        }
                        value++;
                        result.detail.put(reward, value);
                        result.totalRewardCount++;
                        result.totalMoney += money;
                        if (money == 10000000) {
                            mResult.mTimeToGoHome.add(j);
                            updateGoHomeRecord();
                            Integer v = mResult.goHomeDistribute.get(record);
                            if (v == null) {
                                v = 0;
                            }
                            v++;
                            mResult.goHomeDistribute.put(record, v);
                        }
                    }
                    publishProgressLocal();
                }
                mResult.recordRate.put(record, ((float) roundRewardCount) / CALCULATE_TIMES);
            }
            result.endTime = System.currentTimeMillis();
            return result;
        }

        private void updateGoHomeRecord() {
            final File file = getGoHomeRecordFile(CALCULATE_TIMES);
            try {
                DataLoader.saveToFile(file, mResult.mTimeToGoHome.toJson()
                        .toString(), "UTF8");
            } catch (IOException e) {
            }
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
