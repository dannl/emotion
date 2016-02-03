package com.qqdd.lottery.test;

import com.qqdd.lottery.calculate.data.CalculatorCollection;
import com.qqdd.lottery.calculate.data.TimeToGoHome;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.RewardRule;
import com.qqdd.lottery.data.UserSelection;
import com.qqdd.lottery.data.management.Calculation;
import com.qqdd.lottery.data.management.DataSource;
import com.qqdd.lottery.data.management.History;
import com.qqdd.lottery.data.management.ProgressCallback;
import com.qqdd.lottery.data.management.UserSelections;
import com.qqdd.lottery.utils.NumUtils;
import com.qqdd.lottery.utils.Random;
import com.qqdd.lottery.utils.SimpleIOUtils;

import org.json.JSONArray;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestAlgorithm {

    private static final DecimalFormat TEST_RESULT_FORMAT = new DecimalFormat("##0.0000000");

    public static void main(String[] args) {
        Random.getInstance()
                .init();
        try {
                        new TestAlgorithm(SimpleIOUtils.getProjectRoot()).testAlgorithmAndPrintRateDetail(
                                Lottery.Type.DLT, Calculation.lastNTime(), 1000000, 1000);
//            new TestAlgorithm(SimpleIOUtils.getProjectRoot()).calculateAndSave(Lottery.Type.DLT,
//                    Calculation.lastNTime(), 3, 1000000);
        } catch (DataSource.DataLoadingException e) {
            System.out.println(e.getMessage());
        }
    }

    private File mRoot;

    public TestAlgorithm(final File root) {
        mRoot = root;
    }

    private File getProjectRoot() {
        return mRoot;
    }

    private List<Lottery> calculateResult(Lottery.Type type, CalculatorCollection calculators,
                                          int resultCount, int calculateTimes)
            throws DataSource.DataLoadingException {
        List<Lottery> result = new Calculation(getProjectRoot()).calculate(
                new History(getProjectRoot()).load(type), calculators, new ProgressCallback() {
                    @Override
                    public void onProgressUpdate(String progress) {
                        System.out.println("\r" + progress);
                    }
                }, calculateTimes, type, resultCount);
        System.out.println("\nresult is: ");
        for (int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i)
                    .toString());
        }
        return result;
    }

    public void calculateAndSave(Lottery.Type type, CalculatorCollection calculators,
                                 int resultCount, int calculateTimes)
            throws DataSource.DataLoadingException {
        List<Lottery> result = calculateResult(type, calculators, resultCount, calculateTimes);
        final List<UserSelection> userSelections = new ArrayList<>(result.size());
        for (int i = 0; i < result.size(); i++) {
            final UserSelection useSelection = new UserSelection(result.get(i));
            useSelection.sort();
            userSelections.add(useSelection);
        }
        UserSelections manager = new UserSelections(
                new File(getProjectRoot(), UserSelections.getCacheFolderWithType(type)));
        manager.addUserSelection(userSelections);
    }

    public void actualBuyingTest(Lottery.Type type, CalculatorCollection collection,
                                 int calculateTimes, int since)
            throws DataSource.DataLoadingException {
        double total = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        final int testRound = 1000;
        int earnCount = 0;
        for (int i = 0; i < testRound; i++) {
            final double v = actualBuyingARound(type, collection, calculateTimes, since);
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

    public double actualBuyingARound(Lottery.Type type, CalculatorCollection collection,
                                     int calculateTimes, int since)
            throws DataSource.DataLoadingException {
        final TestResult result = testAlgorithm(type, collection, calculateTimes, since, null,
                false);
        System.out.println(result.toString());
        return result.calculateTotal() - 2 * result.totalTestCount;
    }

    public double testAlgorithmAndPrintRateDetail(Lottery.Type type,
                                                  CalculatorCollection collection,
                                                  int calculateTimes, int since)
            throws DataSource.DataLoadingException {
        final TestResult result = testAlgorithm(type, collection, calculateTimes, since, null,
                false);
        System.out.println(result.toString());
        float maxRate = 0;
        float minRate = 1;
        final JSONArray json = new JSONArray();
        for (TestRoundRates entry : result.recordRate) {
            if (entry.getRate() > maxRate) {
                maxRate = entry.getRate();
            }
            if (entry.getRate() < minRate) {
                minRate = entry.getRate();
            }
            json.put(entry.toJson());
        }
        TestRoundRates.save(getProjectRoot(), result.title, result.recordRate);
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

    public TestResult testRandom(Lottery.Type type, int calculateTimes, int since)
            throws DataSource.DataLoadingException {
        final Task task = new Task(getProjectRoot(), type, Calculation.random(), calculateTimes,
                since, null, false);
        return task.execute();
    }

    public TestResult testAlgorithm(Lottery.Type type, CalculatorCollection collection,
                                    int calculateTimes, int since, ProgressCallback callback,
                                    boolean needAllResult) throws DataSource.DataLoadingException {
        final Task task = new Task(getProjectRoot(), type, collection, calculateTimes, since,
                callback, needAllResult);
        return task.execute();
    }

    public static final class TestResult {
        String title = "我的算法";
        long totalTestCount;
        long totalRewardCount;
        long totalMoney;
        HashMap<RewardRule.Reward, Integer> detail = new HashMap<>();
        TimeToGoHome mTimeToGoHome;
        long startTime;
        long endTime;
        private HashMap<LotteryRecord, Integer> goHomeDistribute = new HashMap<>();
        List<TestRoundRates> recordRate = new ArrayList<>();
        public ArrayList<Lottery> allLtResult;

        public TestResult(File root, final Lottery.Type type, final int testTime,
                          final String calculatorDesc) {
            title = calculatorDesc + "_" + type.toString() + "_测试" + testTime + "次";
            mTimeToGoHome = TimeToGoHome.load(root, type, testTime);
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
                    .append(TEST_RESULT_FORMAT.format(((double) totalRewardCount) / totalTestCount))
                    .append("\n");
            builder.append("测试速度：")
                    .append(TEST_RESULT_FORMAT.format(
                            ((double) totalTestCount) / (System.currentTimeMillis() - startTime) * 1000))
                    .append("\n");
            final Set<Map.Entry<RewardRule.Reward, Integer>> entrySet = detail.entrySet();
            for (Map.Entry<RewardRule.Reward, Integer> entry : entrySet) {
                final int value = entry.getValue();
                final RewardRule.Reward key = entry.getKey();
                totalMoney += value * key.getMoney();
                builder.append(key.getTitle())
                        .append(" 奖金:")
                        .append(key.getMoney())
                        .append(" 个数:")
                        .append(value)
                        .append(" 中奖率:")
                        .append(TEST_RESULT_FORMAT.format(((double) value) / totalTestCount))
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

    private class DefaultProgressCallback implements ProgressCallback {

        @Override
        public void onProgressUpdate(String progress) {
            System.out.print(progress);
        }
    }

    private class Task {

        private final CalculatorCollection mCalculators;
        private LotteryConfiguration mConfiguration;
        private TestResult mResult;
        private Lottery.Type mType;
        private int mCalculateTimes;
        private int mSince;
        private ProgressCallback mProgressCallback;


        public Task(File root, Lottery.Type type, CalculatorCollection calculators,
                    final int calculateTimes, final int since, ProgressCallback callback,
                    boolean needAllResult) {
            mCalculators = calculators;
            mCalculateTimes = calculateTimes;
            mConfiguration = LotteryConfiguration.getWithType(type);
            mType = type;
            mSince = since;
            mResult = new TestResult(root, type, calculateTimes, calculators.getTitle());
            if (needAllResult) {
                mResult.allLtResult = new ArrayList<>();
            }
            mProgressCallback = callback == null ?
                    new DefaultProgressCallback() :
                    callback;
        }

        protected TestResult execute() throws DataSource.DataLoadingException {
            List<HistoryItem> history = new History(getProjectRoot()).load(mType);
            final TestResult result = mResult;
            result.startTime = System.currentTimeMillis();
            NumberTable normalTable = new NumberTable(mConfiguration.getNormalRange());
            NumberTable specialTable = new NumberTable(mConfiguration.getSpecialRange());
            final int size = history.size();
            //全随机算法。
            for (int i = size / mSince; i > 0; i--) {
                final List<HistoryItem> subHistory = history.subList(i - 1, size);
                final HistoryItem record = history.get(i);
                int roundRewardCount = 0;
                for (int j = 0; j < mCalculateTimes; j++) {
                    result.totalTestCount++;
                    normalTable.reset();
                    specialTable.reset();
                    for (int k = 0; k < mCalculators.size(); k++) {
                        mCalculators.get(k)
                                .calculate(subHistory, normalTable, specialTable);
                    }
                    final Lottery tempResult = com.qqdd.lottery.data.management.NumberProducer.getInstance()
                            .pick(subHistory, normalTable, specialTable, mConfiguration);
                    if (mResult.allLtResult != null) {
                        mResult.allLtResult.add(tempResult);
                    }
                    final RewardRule.Reward reward = record.calculateReward(tempResult);
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
                        if (reward.isGoHome()) {
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
                mResult.recordRate.add(
                        new TestRoundRates(record, ((float) roundRewardCount) / mCalculateTimes));
            }
            result.endTime = System.currentTimeMillis();
            return result;
        }

        private void updateGoHomeRecord() {
            TimeToGoHome.save(getProjectRoot(), mType, mResult.mTimeToGoHome);
        }

        private long mLastPublishProgressTime = 0;

        private void publishProgressLocal() {
            if (System.currentTimeMillis() - mLastPublishProgressTime < 5000) {
                return;
            }
            mLastPublishProgressTime = System.currentTimeMillis();
            mProgressCallback.onProgressUpdate(
                    "\n" + mResult.toString() + "\n");
        }

    }

}
