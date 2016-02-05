package com.qqdd.lottery.data.management;

import com.qqdd.lottery.calculate.data.CalculatorCollection;
import com.qqdd.lottery.calculate.data.CalculatorFactory;
import com.qqdd.lottery.calculate.data.TimeToGoHome;
import com.qqdd.lottery.calculate.data.calculators.AverageProbabilityCalculator;
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
                    .pick(history, normalTable, specialTable, configuration));
        }
        return NumberProducer.getInstance()
                .select(tempBuffer, count, TimeToGoHome.load(mRoot, type, calculateTimes));
    }

    public static CalculatorCollection lastNTime() {
        CalculatorCollection calculatorList = new CalculatorCollection("稳健型", "根据测试结果，概率分布最稳定，平均中奖率最高，约20%（大乐透），13.5%(双色球)");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
                .createCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTime_sameNumber() {
        CalculatorCollection calculatorList = new CalculatorCollection("波动型","根据测试结果，概率分布波动较大，平均中奖率19%(大乐透),13%(双色球)，使用往期开奖号码进行测试，最高中奖率可以到40%,同时最低也到了9%");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.SameNumberCalculatorFactory.instance()
                        .createCalculator());
        return calculatorList;
    }
    public static CalculatorCollection lastNTime_sameTail() {
        CalculatorCollection calculatorList = new CalculatorCollection("测试用_波动型_同尾","测试用...");
        calculatorList.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance()
                .createCalculator());
        calculatorList.add(CalculatorFactory.SameNumberCalculatorFactory.instance()
                        .createCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTime_sameNumber_sameTail() {
        CalculatorCollection calculatorList = new CalculatorCollection("测试用_波动型_同号_同尾","测试用...");
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
        CalculatorCollection calculatorList = new CalculatorCollection("纯随机","平均中奖率6.6%");
        calculatorList.add(new AverageProbabilityCalculator());
        return calculatorList;
    }

    public static List<CalculatorCollection> allCalculorGroups() {
        final List<CalculatorCollection> result = new ArrayList<>();
        result.add(lastNTime());
        result.add(lastNTime_sameNumber());
        result.add(random());
//        result.add(lastNTime_sameTail());
//        result.add(lastNTime_sameNumber_sameTail());
        return result;
    }
}
