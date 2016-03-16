package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.calculate.data.calculators.AverageProbabilityCalculator;
import com.qqdd.lottery.calculate.data.calculators.HistoryOccurrenceProbabilityCalculator;
import com.qqdd.lottery.calculate.data.calculators.LastNMediumIncrease;
import com.qqdd.lottery.calculate.data.calculators.LastNTimeOccurIncreaseCalculator_new;
import com.qqdd.lottery.calculate.data.calculators.NoSelectionCalculator;
import com.qqdd.lottery.calculate.data.calculators.SameNumberCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danliu on 2/2/16.
 */
public class CalculatorCollection extends ArrayList<Calculator> {

    private String mTitle;

    public CalculatorCollection() {
        super();
        mTitle = "";
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public int hashCode() {
        return mTitle.hashCode();
    }

    @Override
    public boolean add(Calculator calculator) {
        boolean result = super.add(calculator);
        if (result) {
            mTitle += "_" + calculator.getTitle();
        }
        return result;
    }

    public static List<CalculatorCollection> allCollections() {
        final List<CalculatorCollection> result = new ArrayList<>();
        result.add(sameNumber());
        result.add(lastNTime());
        result.add(lastNTime_sameNumber());
        result.add(lastNTimeRevert());
        result.add(lastNTimeRevert_sameNumber());
        result.add(lastNTimeMedium(1.2f, 0.9f));
        result.add(lastNTimeMedium(1.2f, 0.8f));
        result.add(lastNTimeMedium(1.2f, 0.7f));
        result.add(lastNTimeMedium(1.2f, 0.6f));
        result.add(lastNTimeMedium(1.2f, 0.5f));
        result.add(lastNTimeMedium(1.2f, 1.1f));
        result.add(lastNTimeMedium(1.2f, 1.2f));
        result.add(lastNTimeMedium(1.2f, 1.3f));
        result.add(lastNTimeMedium(1.2f, 1.4f));
        result.add(lastNTimeMedium(1.2f, 1.5f));
        result.add(lastNTimeMedium(1.1f,1f));
        result.add(lastNTimeMedium(1.3f,1f));
        result.add(lastNTimeMedium(1.4f,1f));
        result.add(lastNTimeMedium(1.5f,1f));
        result.add(lastNTimeMedium(0.9f,1f));
        result.add(lastNTimeMedium(0.8f,1f));
        result.add(lastNTimeMedium(0.7f,1f));
        result.add(lastNTimeMedium(0.6f,1f));
        result.add(lastNTimeMedium(0.5f,1f));
        return result;
    }

    public static CalculatorCollection lastNTime() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator_new(false));
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTimeMedium() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNMediumIncrease());
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTimeMedium(float scale, float move) {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNMediumIncrease(scale, move));
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTimeRevert() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator_new(true));
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection sameNumber() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new SameNumberCalculator());
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTimeRevert_sameNumber() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator_new(true));
        calculatorList.add(new SameNumberCalculator());
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTime_sameNumber() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator_new(false));
        calculatorList.add(new SameNumberCalculator());
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection random() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new AverageProbabilityCalculator());
        return calculatorList;
    }

}

