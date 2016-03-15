package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.management.DataSource;
import com.qqdd.lottery.data.management.History;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 3/14/16.
 */
public class CalculatorAutoSwitcher {

    private static final int TEST_COUNT = 100000;
    private static final int INITIAL_SINCE_DLT = 900;
    private static final int INITIAL_SINCE_SSQ = 1500;

    private File mRoot;
    private History mHistory;
    private HashMap<Lottery.Type, List<RateList>> mRates;

    public CalculatorAutoSwitcher(File root) {
        mRoot = root;
        mHistory = new History(root);
        mRates = new HashMap<>(2);
    }

    public void refresh(Lottery.Type type) {
        final List<RateList> rateLists = mRates.get(type);
        if (rateLists == null) {
            //TODO calculate all.
        } else {
            //TODO calculate since.
        }
    }


    private class CalculateRateTask {

        private CalculatorCollection mCalculators;
        private Lottery.Type mType;
        private int mSince;

        private CalculateRateTask(final CalculatorCollection calculators, final Lottery.Type type, final int since) {
            mCalculators = calculators;
            mType = type;
            mSince = since;
        }

        List<Rate> execute() throws DataSource.DataLoadingException {
            List<Rate> result = new ArrayList<>();
            final List<HistoryItem> historyItems = mHistory.load(mType);
            for (int i = mSince; i > 0; i --) {

            }
            return result;
        }

    }

}
