package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.NumberTable;

import java.util.List;

/**
 * Created by danliu on 2016/1/23.
 */
public class AverageProbabilityCalculator extends CalculatorImpl {
    public AverageProbabilityCalculator() {
        super("平均分布，测试用", "平均分布，测试用");
    }

    @Override
    public void calculate(List<HistoryItem> lts, NumberTable normalTable, NumberTable specialTable) {
        for (int i = 0; i < normalTable.size(); i++) {
            normalTable.get(i).setWeight(1);
        }
        for (int i = 0; i < specialTable.size(); i++) {
            specialTable.get(i).setWeight(1);
        }
    }
}
