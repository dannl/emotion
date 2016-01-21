package com.qqdd.lottery.calculate.data;

/**
 * Created by danliu on 1/21/16.
 */
public abstract class CalculatorFactory {

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

    public abstract CalculatorItem createCalculator();

    public static final class OccurrenceProbabilityCalculatorFactory extends CalculatorFactory {

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
        public CalculatorItem createCalculator() {
            return new CalculatorItem(getTitle(), getDesc(), new HistoryOccurrenceProbabilityCalculator());
        }
    }

}
