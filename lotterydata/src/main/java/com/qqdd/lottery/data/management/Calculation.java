package com.qqdd.lottery.data.management;

import com.qqdd.lottery.calculate.data.CalculatorCollection;
import com.qqdd.lottery.calculate.data.TimeToGoHome;
import com.qqdd.lottery.calculate.data.calculators.AverageProbabilityCalculator;
import com.qqdd.lottery.calculate.data.calculators.KillLastNormal;
import com.qqdd.lottery.calculate.data.calculators.LastNTimeOccurIncreaseCalculator_new;
import com.qqdd.lottery.calculate.data.calculators.NoSelectionCalculator;
import com.qqdd.lottery.calculate.data.calculators.SameNumberCalculator;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.utils.Random;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by danliu on 2/2/16.
 */
public class Calculation {

    private File mRoot;

    public Calculation(final File root) {
        mRoot = root;
    }

    public List<Lottery> calculate(List<HistoryItem> history, CalculatorCollection calculatorList, ProgressCallback progressCallback,
                                   int calculateTimes, Lottery.Type type, final int count) {
        Random.getInstance().init();
        final LotteryConfiguration configuration = LotteryConfiguration.getWithType(type);
        final NumberTable normalTable = new NumberTable(configuration.getNormalRange());
        final NumberTable specialTable = new NumberTable(configuration.getSpecialRange());
        List<Lottery> tempBuffer = new ArrayList<>();
        for (int i = 0; i < calculateTimes; i++) {
            final int i1 = calculateTimes / 100;
            if (i1!= 0 && i % i1 == 0) {
                progressCallback.onProgressUpdate(String.valueOf(((float) i) / calculateTimes));
            }
            normalTable.reset();
            specialTable.reset();
            for (int j = 0; j < calculatorList.size(); j++) {
                calculatorList.get(j)
                        .calculate(history, normalTable, specialTable);
            }
            tempBuffer.add(NumberProducer.getInstance()
                    .pick(history, normalTable, specialTable, configuration));
        }
        if (calculateTimes <= count) {
            return tempBuffer;
        }
        return NumberProducer.getInstance()
                .select(tempBuffer, count, TimeToGoHome.load(mRoot, type, calculateTimes));
    }

}
