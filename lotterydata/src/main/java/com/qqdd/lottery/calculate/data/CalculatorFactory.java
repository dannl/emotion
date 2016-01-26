package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.calculate.data.calculators.HistoryOccurrenceProbabilityCalculator;
import com.qqdd.lottery.calculate.data.calculators.LastNTimeOccurIncreaseCalculator;
import com.qqdd.lottery.calculate.data.calculators.SameNumberCalculator;
import com.qqdd.lottery.calculate.data.calculators.SelectionIncreaseCalculator;

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

    public static final class SameNumberCalculatorFactory extends CalculatorFactory<SameNumberCalculator> {

        private static class SingletonHolder {
            private static final SameNumberCalculatorFactory INSTANCE = new SameNumberCalculatorFactory();
        }

        public static SameNumberCalculatorFactory instance() {
            return SingletonHolder.INSTANCE;
        }

        private SameNumberCalculatorFactory() {
            super("重复出现的号码增加概率", "根据历史数据，计算出重复号码个数出现的概率，再在上一期中根据个数的概率随机选出N个重复的号码,增加这些号码的出现概率.");
        }

        @Override
        public SameNumberCalculator createCalculator() {
            return new SameNumberCalculator(getTitle(), getDesc());
        }
    }

    public static final class Last4TimeOccurIncreaseCalculatorFactory extends CalculatorFactory<LastNTimeOccurIncreaseCalculator> {

        private static class SingletonHolder {
            private static final Last4TimeOccurIncreaseCalculatorFactory INSTANCE = new Last4TimeOccurIncreaseCalculatorFactory();
        }

        public static Last4TimeOccurIncreaseCalculatorFactory instance() {
            return SingletonHolder.INSTANCE;
        }

        private Last4TimeOccurIncreaseCalculatorFactory() {
            super("最近4次出现多的概率增加", "按最近4次出现的次数作为权值的倍数");
        }

        @Override
        public LastNTimeOccurIncreaseCalculator createCalculator() {
            return new LastNTimeOccurIncreaseCalculator(getTitle(), getDesc());
        }
    }

}
