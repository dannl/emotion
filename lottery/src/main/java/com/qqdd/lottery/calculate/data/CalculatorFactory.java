package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.calculate.data.calculator.HistoryOccurrenceProbabilityCalculator;
import com.qqdd.lottery.calculate.data.calculator.SelectionIncreaseCalculator;

/**
 * Created by danliu on 1/21/16.
 */
public abstract class CalculatorFactory<T extends CalculatorImpl> {

    private final String mTitle;
    private final String mDesc;

    private CalculatorFactory(final String title, final String desc) {
        mTitle = title;
        mDesc = desc;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDesc() {
        return mDesc;
    }

    public abstract T createCalculator();

    public static final class OccurrenceProbabilityCalculatorFactory extends CalculatorFactory<HistoryOccurrenceProbabilityCalculator> {

        private static class SingletonHolder {
            private static final OccurrenceProbabilityCalculatorFactory INSTANCE = new OccurrenceProbabilityCalculatorFactory();
        }

        public static OccurrenceProbabilityCalculatorFactory instance() {
            return SingletonHolder.INSTANCE;
        }

        private OccurrenceProbabilityCalculatorFactory() {
            super("历史出现概率计算器","根据历史数据，计算每个数字的出现概率。");
        }

        @Override
        public HistoryOccurrenceProbabilityCalculator createCalculator() {
            return new HistoryOccurrenceProbabilityCalculator(getTitle(), getDesc());
        }
    }

    public static final class SelectionIncreaseCalculatorFactory extends CalculatorFactory<SelectionIncreaseCalculator> {

        private static class SingletonHolder {
            private static final SelectionIncreaseCalculatorFactory INSTANCE = new SelectionIncreaseCalculatorFactory();
        }

        public static SelectionIncreaseCalculatorFactory instance() {
            return SingletonHolder.INSTANCE;
        }

        private SelectionIncreaseCalculatorFactory() {
            super("选中号码增加概率", "选中的号码增加指定的概率");
        }

        @Override
        public SelectionIncreaseCalculator createCalculator() {
            return new SelectionIncreaseCalculator(getTitle(), getDesc());
        }
    }

}
