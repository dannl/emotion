package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.calculate.data.calculators.AverageProbabilityCalculator;
import com.qqdd.lottery.calculate.data.calculators.HistoryOccurrenceProbabilityCalculator;
import com.qqdd.lottery.calculate.data.calculators.LastNMediumIncrease;
import com.qqdd.lottery.calculate.data.calculators.LastNNormalizedIncrease;
import com.qqdd.lottery.calculate.data.calculators.LastNTimeOccurIncreaseCalculator;
import com.qqdd.lottery.calculate.data.calculators.LastNTimeOccurIncreaseCalculator_new;
import com.qqdd.lottery.calculate.data.calculators.NoSelectionCalculator;
import com.qqdd.lottery.calculate.data.calculators.SameNumberCalculator;
import com.qqdd.lottery.calculate.data.calculators.UniverseRateRangeCalculator;

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
        result.add(lastNTimeRevert());
        result.add(lastNNormalized());
        result.add(lastNMedium());
        result.add(urr());
        result.add(oldLastNTime());
        return result;
    }

    public static CalculatorCollection oldLastNTime() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator());
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection urr() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new UniverseRateRangeCalculator());
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNTime() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNTimeOccurIncreaseCalculator_new(false));
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNNormalized() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNNormalizedIncrease());
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNNormalized(float scale, float move) {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNNormalizedIncrease(scale, move));
        calculatorList.add(new NoSelectionCalculator());
        return calculatorList;
    }

    public static CalculatorCollection lastNMedium() {
        CalculatorCollection calculatorList = new CalculatorCollection();
        calculatorList.add(new HistoryOccurrenceProbabilityCalculator());
        calculatorList.add(new LastNMediumIncrease());
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

