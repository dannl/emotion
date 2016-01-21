package com.qqdd.lottery.calculate;

import com.qqdd.lottery.calculate.data.CalculatorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danliu on 1/21/16.
 */
public class CalculatorStore {

    private static class SingletonHolder {
        private static final CalculatorStore INSTANCE = new CalculatorStore();
    }

    public static CalculatorStore getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private List<CalculatorFactory> mCalculators;

    private CalculatorStore() {
        mCalculators = new ArrayList<>();
        mCalculators.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance());
    }

}
