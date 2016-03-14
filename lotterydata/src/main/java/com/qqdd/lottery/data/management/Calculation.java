package com.qqdd.lottery.data.management;

import com.qqdd.lottery.calculate.data.CalculatorCollection;
import com.qqdd.lottery.calculate.data.CalculatorFactory;
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

    public static CalculatorCollection lastNTime() {
        CalculatorCollection calculatorList = new CalculatorCollection("稳健型", "");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
//        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
//                .createCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator_new(false));
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection killLast() {
        CalculatorCollection calculatorList = new CalculatorCollection("kill_last", "");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
//        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
//                .createCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator_new(false));
        calculatorList.add(new KillLastNormal());
//        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }



    public static CalculatorCollection lastNTimeRevert() {
        CalculatorCollection calculatorList = new CalculatorCollection("稳健型_revert", "");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
//        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
//                .createCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator_new(true));
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTimeRevert_sameNumber() {
        CalculatorCollection calculatorList = new CalculatorCollection("lastRevert_sameNumber", "");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
//        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
//                .createCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator_new(true));
        calculatorList.add(new SameNumberCalculator());
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTime_sameNumber() {
        CalculatorCollection calculatorList = new CalculatorCollection("波动型","");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator_new(false));
        calculatorList.add(new SameNumberCalculator());
        return calculatorList;
    }

    public static CalculatorCollection sameNumber() {
        CalculatorCollection calculatorList = new CalculatorCollection("sameNumber","");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(new SameNumberCalculator());
        return calculatorList;
    }

    public static CalculatorCollection random() {
        CalculatorCollection calculatorList = new CalculatorCollection("纯随机","平均中奖率6.6%");
        calculatorList.add(new AverageProbabilityCalculator());
        return calculatorList;
    }

    public static List<CalculatorCollection> allCalculorGroups() {
        final List<CalculatorCollection> result = new ArrayList<>();
        result.add(lastNTime());
        result.add(lastNTime_sameNumber());
        result.add(random());
        return result;
    }
}
