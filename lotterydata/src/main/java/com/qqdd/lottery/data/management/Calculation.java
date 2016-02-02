package com.qqdd.lottery.data.management;

import com.qqdd.lottery.calculate.data.Calculator;
import com.qqdd.lottery.calculate.data.CalculatorCollection;
import com.qqdd.lottery.calculate.data.CalculatorFactory;
import com.qqdd.lottery.calculate.data.NumberProducer;
import com.qqdd.lottery.calculate.data.TimeToGoHome;
import com.qqdd.lottery.calculate.data.calculators.AverageProbabilityCalculator;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.NumberTable;

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

    public List<Lottery> calculate(List<HistoryItem> history, ProgressCallback progressCallback,
                                   int calculateTimes, Lottery.Type type, final int count) {
        List<Calculator> calculatorList = lastNTime();
        final LotteryConfiguration configuration = LotteryConfiguration.DLTConfiguration();
        final NumberTable normalTable = new NumberTable(configuration.getNormalRange());
        final NumberTable specialTable = new NumberTable(configuration.getSpecialRange());
        List<Lottery> tempBuffer = new ArrayList<>();
        for (int i = 0; i < calculateTimes; i++) {
            if (i % (calculateTimes / 100) == 0) {
                progressCallback.onProgressUpdate(String.valueOf(((float) i) / calculateTimes));
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
                .select(tempBuffer, count, TimeToGoHome.load(mRoot, type, calculateTimes));
    }


    public static CalculatorCollection lastNTime() {
        CalculatorCollection calculatorList = new CalculatorCollection("稳健型");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
                .createCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTime_sameNumber() {
        CalculatorCollection calculatorList = new CalculatorCollection("波动型");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.SameNumberCalculatorFactory.instance()
                        .createCalculator());
        return calculatorList;
    }
    public static CalculatorCollection lastNTime_sameTail() {
        CalculatorCollection calculatorList = new CalculatorCollection("波动型_sameTtail");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.SameNumberCalculatorFactory.instance()
                        .createCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTime_sameNumber_sameTail() {
        CalculatorCollection calculatorList = new CalculatorCollection("波动型_sameNumber_sameTail");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.SameNumberCalculatorFactory.instance()
                        .createCalculator());
        calculatorList.add(CalculatorFactory.SameTailCalculatorFactory.instance().createCalculator());
        return calculatorList;
    }

    public static CalculatorCollection random() {
        CalculatorCollection calculatorList = new CalculatorCollection("纯随机");
        calculatorList.add(new AverageProbabilityCalculator());
        return calculatorList;
    }
}
