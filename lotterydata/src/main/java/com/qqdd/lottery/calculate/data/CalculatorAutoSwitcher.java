package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.RewardRule;
import com.qqdd.lottery.data.management.DataSource;
import com.qqdd.lottery.data.management.History;
import com.qqdd.lottery.utils.Random;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by danliu on 3/14/16.
 */
public class CalculatorAutoSwitcher {

    private static final int TEST_COUNT = 100000;
    private static final int INITIAL_SINCE_DLT = 900;
    private static final int INITIAL_SINCE_SSQ = 1500;
    private static final int DEFAULT_LATEST_SINCE = 100;
    private static final String FOLDER = "rates";
    private static final float STANDARD_AVG_RATE = 0.066f;

    private File mRoot;
    private History mHistory;
    private HashMap<Lottery.Type, HashMap<CalculatorCollection, RateList>> mRates;

    public CalculatorAutoSwitcher(File root) {
        mRoot = new File(root, FOLDER);
        if (!mRoot.exists()) {
            mRoot.mkdirs();
        }
        mHistory = new History(root);
        mRates = new HashMap<>(2);
    }

    public void test(Lottery.Type type) {
        refresh(type);
        HashMap<CalculatorCollection,RateList> rateLists = mRates.get(type);
        Set<Map.Entry<CalculatorCollection, RateList>> entries = rateLists.entrySet();
        for (Map.Entry<CalculatorCollection, RateList> entry : entries) {
            final RateList list = entry.getValue();
            System.out.println(
                    "name: " + list.getName() + " av: " + list.getAverageRate() + " max: " + list.getMaxRate() + " min: " + list.getMinRate());
            float winCount = 0;
            //连续出现高于平均的次数的平均数
            float averageWinLast = 0;
            int continuousWin = 0;
            int continuousWinCount = 0;
            int totalContinuousWin = 0;
            for (int i = 0; i < list.size(); i++) {
                final float rate = list.get(i)
                        .getRate();
                if (rate > STANDARD_AVG_RATE) {
                    winCount ++;
                    continuousWin ++;
                } else {
                    if (continuousWin > 1) {
                        continuousWinCount ++;
                        totalContinuousWin += continuousWin;
                        continuousWin = 0;
                    }
                }
            }
            System.out.println("win: " + winCount + " win rate: " + (winCount / list.size()) + " continuous rate: " + ((float) totalContinuousWin) / list.size());
            System.out.println("continuous win count: " + continuousWinCount + " av: " + ((float) totalContinuousWin) / continuousWinCount);
            boolean lastWasWin = list.get(0).getRate() > STANDARD_AVG_RATE;
            int totalLostCount = lastWasWin ? 0 : 1;
            int totalWinCount = lastWasWin ? 1 : 0;
            int lastWinLeadToWin = 0;
            int lastLostLeadToWin = 0;
            for (int i = 1; i < list.size(); i++) {
                final float rate = list.get(i).getRate();
                if (rate > STANDARD_AVG_RATE) {
                    totalWinCount ++;
                    if (lastWasWin) {
                        lastWinLeadToWin ++;
                    } else {
                        lastLostLeadToWin ++;
                    }
                } else {
                    totalLostCount ++;
                }
                lastWasWin = rate > STANDARD_AVG_RATE;
            }
//            System.out.println("win to win: " + ((float) lastWinLeadToWin) / totalWinCount);
//            System.out.println("lost to win: " + ((float) lastLostLeadToWin) / totalLostCount);

            //
            int N = 3;
            int total = 0;
            int win = 0;
            for (int i = N; i < list.size(); i++) {
                boolean right = true;
                for (int j = i - 1; j >= i - N; j--) {
                    right &= (list.get(j).getRate() > STANDARD_AVG_RATE);
                }
                if (right) {
                    total ++;
                    if (list.get(i).getRate() > STANDARD_AVG_RATE) {
                        win ++;
                    }
                }
            }
            System.out.println(" n = " + N + " win: " + win + " total : " + total + " listsize: " + list.size() + " rate: " + ((float) win) / total);
        }


    }

    public void refresh(Lottery.Type type) {
        HashMap<CalculatorCollection,RateList> rateLists = mRates.get(type);
        if (rateLists == null) {
            rateLists = new HashMap<>();
            mRates.put(type, rateLists);
        }
        List<CalculatorCollection> calculators = CalculatorCollection.allCollections();
        for (int i = 0; i < calculators.size(); i++) {
            final CalculatorCollection item = calculators.get(i);
            RateList rates = rateLists.get(item);
            if (rates == null) {
                final String title = getRateListName(item, type);
                rates = loadFromLocal(title);
                if (rates == null) {
                    rates = new RateList(title);
                }
                rateLists.put(item, rates);
            }
            try {
                if (refreshRateList(rates, item, type)) {
                    rates.saveTo(mRoot);
                }
            } catch (DataSource.DataLoadingException ignored) {
                ignored.printStackTrace();
            }
        }
    }

    private RateList loadFromLocal(final String title) {
        final File file = new File(mRoot, title);
        return RateList.loadFrom(file);
    }

    private String getRateListName(CalculatorCollection calculators, Lottery.Type type) {
        return type.toString() + calculators.getTitle();
    }

    private int getDefaultSince(Lottery.Type type) {
        if (type == Lottery.Type.DLT) {
            return INITIAL_SINCE_DLT;
        } else {
            return INITIAL_SINCE_SSQ;
        }
    }

    private boolean refreshRateList(final RateList rates, final CalculatorCollection calculators, final
                                 Lottery.Type type) throws DataSource.DataLoadingException {
        final List<HistoryItem> historyItems = mHistory.load(type);
        int since = 0;
        if (rates.isEmpty()) {
            since = historyItems.size() - getDefaultSince(type);
//            since = DEFAULT_LATEST_SINCE;
        } else {
            final LotteryRecord last = rates.get(rates.size() - 1).getRecord();
            for (int i = 0; i < historyItems.size(); i++) {
                if (historyItems.get(i).equals(last)) {
                    since = i;
                    break;
                }
            }
        }
        if (since == 0) {
            return false;
        }
        final LotteryConfiguration configuration = LotteryConfiguration.getWithType(type);
        Random.getInstance().init();
        for (int i = since; i > 0; i --) {
            NumberTable normalTable = new NumberTable(configuration.getNormalRange());
            NumberTable specialTable = new NumberTable(configuration.getSpecialRange());
            List<HistoryItem> subHistory = historyItems.subList(i, historyItems.size());
            HistoryItem record = historyItems.get(i - 1);
            System.out.println("====calculating: " + rates.getName() + " at " + record + "====");
//            Random.getInstance().init();
            int roundRewardCount = 0;
            for (int j = 0; j < TEST_COUNT; j++) {
                normalTable.reset();
                specialTable.reset();
                for (int k = 0; k < calculators.size(); k++) {
                    calculators.get(k)
                            .calculate(subHistory, normalTable, specialTable);
                }
                final Lottery tempResult = com.qqdd.lottery.data.management.NumberProducer.getInstance()
                        .pick(subHistory, normalTable, specialTable, configuration);
                final RewardRule.Reward reward = record.calculateReward(tempResult);
                final int money = reward.getMoney();
                if (money > 0) {
                    roundRewardCount++;
                }
            }
            Rate rate = new Rate(record, ((float)roundRewardCount) / TEST_COUNT);
            rates.add(rate);
            System.out.println("after all: average: " + rates.getAverageRate() + " max: " + rates.getMaxRate() + " min: " + rates.getMinRate());

        }
        return true;
    }

    /**
     * 默认的奇偶+大小,最大的出现组合的概率.
     */
    private static final float DEFAULT_PASS_RATE = 0.8f;

    private boolean shouldIgnore(Lottery tempResult) {
        final NumberList normals = tempResult.getNormals();
        int odd = 0;
        int large = 0;
        return false;
    }

}
