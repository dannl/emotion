package com.qqdd.lottery.test;

import com.qqdd.lottery.calculate.data.CalculatorAutoSwitcher;
import com.qqdd.lottery.calculate.data.CalculatorCollection;
import com.qqdd.lottery.calculate.data.Rate;
import com.qqdd.lottery.calculate.data.TimeToGoHome;
import com.qqdd.lottery.calculate.data.calculators.LastNTimeOccurIncreaseCalculator_new;
import com.qqdd.lottery.calculate.data.calculators.NoSelectionCalculator;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.KeyValuePair;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberList;
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
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestAlgorithm {

    private static final DecimalFormat TEST_RESULT_FORMAT = new DecimalFormat("##0.0000000");

    public static void main(String[] args) {
//        new CalculatorAutoSwitcher(SimpleIOUtils.getProjectRoot()).test(Lottery.Type.SSQ);
        try {
                        NoSelectionCalculator.setExclusion(Lottery.Type.SSQ, new int[][]{
                                new int[]{19},
                        });
//                        new TestAlgorithm(SimpleIOUtils.getProjectRoot()).testAlgorithmAndPrintRateDetail(
//                                Lottery.Type.DLT, CalculatorCollection.urr(), 10000, 10);
            new TestAlgorithm(SimpleIOUtils.getProjectRoot()).calculateAndSave(Lottery.Type.SSQ, 5,
                    2000000);
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

    private void testLastNTime(List<HistoryItem> items) {
        final LastNTimeOccurIncreaseCalculator_new calculator = new LastNTimeOccurIncreaseCalculator_new(
                false);
        for (int i = 300; i >= 0; i--) {
            final List<HistoryItem> sub = items.subList(i, items.size());
            calculator.calculateUniverses(sub);
        }

    }

    private void calculateTotalValue(List<HistoryItem> items, int to) {
        if (to > items.size()) {
            return;
        }
        final Lottery.Type type = items.get(0)
                .getType();
        final List<KeyValuePair> list = new ArrayList<>();
        for (int i = 0; i < to; i++) {
            final HistoryItem item = items.get(i);
            int total = NumUtils.calculateTotalInList(
                    item.getNormals()) + NumUtils.calculateTotalInList(item.getSpecials());
            list.add(new KeyValuePair(item.getDateDisplay(), total));
        }
        final File destFile = new File(mRoot, type.getName() + to + KeyValuePair.TAIL);
        try {
            SimpleIOUtils.saveToFile(destFile, KeyValuePair.toArray(list)
                    .toString());
        } catch (IOException e) {
        }
    }

    private void testBuyCount(final Lottery.Type type, final CalculatorCollection calculators,
                              final int buyCount, final int since)
            throws DataSource.DataLoadingException {
        final List<HistoryItem> history = new History(SimpleIOUtils.getProjectRoot()).load(type);
        final int from = Math.max(1, history.size() / since);
        int total = 0;
        int win = 0;
        long allMoney = 0;
        for (int i = from; i > 0; i--) {
            total++;
            final HistoryItem record = history.get(i - 1);
            final List<Lottery> result = new Calculation(SimpleIOUtils.getProjectRoot()).calculate(
                    history.subList(i, history.size()), calculators, new ProgressCallback() {
                        @Override
                        public void onProgressUpdate(String progress) {
                        }
                    }, 1000000, type, buyCount);
            long totalMoney = 0;
            for (int j = 0; j < result.size(); j++) {
                final RewardRule.Reward reward = record.calculateReward(result.get(j));
                totalMoney += reward.getMoney();
            }
            final long earn = totalMoney - buyCount * 2;
            if (earn > 0) {
                win++;
            }
            allMoney += earn;
            System.out.println(
                    "total round: " + total + " win: " + win + " all money: " + allMoney + " earn this round: " + earn);
        }
    }

    private void calculateSelectMethod(Lottery.Type type, CalculatorCollection calculators,
                                       int resultCount, int calculateTimes)
            throws DataSource.DataLoadingException {
        final List<HistoryItem> loaded = new History(getProjectRoot()).load(type);
        final HistoryItem record = loaded.get(0);
        int rewardCount = 0;
        int totalCount = 0;
        for (int i = 0; i < 100; i++) {
            List<Lottery> result = new Calculation(getProjectRoot()).calculate(
                    loaded.subList(1, loaded.size()), calculators, new ProgressCallback() {
                        @Override
                        public void onProgressUpdate(String progress) {
                            System.out.println("\r" + progress);
                        }
                    }, calculateTimes, type, resultCount);
            for (int j = 0; j < result.size(); j++) {
                totalCount++;
                RewardRule.Reward reward = record.calculateReward(result.get(j));
                if (reward != null && reward.getMoney() > 0) {
                    rewardCount++;
                }
            }
            System.out.println(
                    "reward count: " + rewardCount + " total: " + totalCount + " rate: " + (((float) rewardCount) / totalCount));
        }
    }

    private List<Lottery> calculateResult(Lottery.Type type,
                                          int resultCount, int calculateTimes)
            throws DataSource.DataLoadingException {
        final List<HistoryItem> history = new History(getProjectRoot()).load(type);
        CalculatorCollection collection = new CalculatorAutoSwitcher(mRoot).getCalculators(
                history.get(0));
        List<Lottery> result = new Calculation(getProjectRoot()).calculate(history, collection,
                new ProgressCallback() {
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

    private static final DecimalFormat FORMAT = new DecimalFormat("00");

    public void printAllHistory(final List<HistoryItem> items) {
        final LotteryConfiguration configuration = items.get(0)
                .getConfiguration();
        final String[] line = new String[configuration.getNormalRange() + configuration.getSpecialRange() + 1];
        for (int i = items.size() - 1; i >= 0; i--) {
            for (int j = 0; j < line.length; j++) {
                line[j] = "==";
                if (j == configuration.getNormalRange()) {
                    line[j] = "||";
                }
            }
            final HistoryItem item = items.get(i);
            final NumberList normals = item.getNormals();
            final NumberList specials = item.getSpecials();
            for (int j = 0; j < normals.size(); j++) {
                final int value = normals.get(j);
                line[value - 1] = FORMAT.format(value);
            }
            for (int j = 0; j < specials.size(); j++) {
                final int value = specials.get(j);
                line[configuration.getNormalRange() + value] = FORMAT.format(value);
            }
            printStringArray(line);
        }
    }

    private void printStringArray(final String[] array) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
        }
        System.out.println(builder.toString());
    }

    public void calculateAndSave(Lottery.Type type,
                                 int resultCount, int calculateTimes)
            throws DataSource.DataLoadingException {
        List<Lottery> result = calculateResult(type, resultCount, calculateTimes);
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
                                 int calculateTimes, int since, int testRound)
            throws DataSource.DataLoadingException {
        double total = 0;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
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
            System.out.println(
                    "total: " + total + " avr: " + total / testRound + " max: " + max + " min: " + min + " earn count: " + earnCount + " earn rate: " + (((float) earnCount) / testRound));
        }
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
        for (Rate entry : result.recordRate) {
            if (entry.getRate() > maxRate) {
                maxRate = entry.getRate();
            }
            if (entry.getRate() < minRate) {
                minRate = entry.getRate();
            }
            json.put(entry.toJson());
        }
        Rate.save(getProjectRoot(), result.title, result.recordRate);
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
        List<Rate> recordRate = new ArrayList<>();
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
            CalculatorAutoSwitcher switcher = new CalculatorAutoSwitcher(mRoot);
            for (int i = size / mSince; i > 0; i--) {
                final List<HistoryItem> subHistory = history.subList(i, size);
                final HistoryItem record = history.get(i - 1);
                int roundRewardCount = 0;
                Random.getInstance()
                        .init();
                final CalculatorCollection calculators = switcher.getCalculators(record);
                for (int j = 0; j < mCalculateTimes; j++) {
                    result.totalTestCount++;
                    normalTable.reset();
                    specialTable.reset();
                    for (int k = 0; k < calculators.size(); k++) {
                        calculators.get(k)
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
                            //                            updateGoHomeRecord();
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
                        new Rate(record, ((float) roundRewardCount) / mCalculateTimes));
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
            mProgressCallback.onProgressUpdate("\n" + mResult.toString() + "\n");
        }

    }

}
