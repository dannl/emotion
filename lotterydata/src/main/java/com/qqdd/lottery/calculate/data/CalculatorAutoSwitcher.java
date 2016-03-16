package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.RewardRule;
import com.qqdd.lottery.data.management.DataSource;
import com.qqdd.lottery.data.management.History;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 3/14/16.
 */
public class CalculatorAutoSwitcher {

    private static final int TEST_COUNT = 10000;
    private static final int INITIAL_SINCE_DLT = 900;
    private static final int INITIAL_SINCE_SSQ = 1500;
    private static final String FOLDER = "rates";

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

}
