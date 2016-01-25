package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.utils.NumUtils;

import java.util.List;

/**
 * 简单计算，所有球在历史数据中出现的概率。
 * Created by danliu on 1/21/16.
 */
public class HistoryOccurrenceProbabilityCalculator extends CalculatorImpl {
    public HistoryOccurrenceProbabilityCalculator(String title, String desc) {
        super(title, desc);
    }

    @Override
    public void calculate(List<LotteryRecord> lts, NumberTable normalTable, NumberTable specialTable) {
        //数字是从1开始而不是0,为了方便数组计算，数组中加入0.
        final int normalSize =normalTable.getRange() + 1;
        final int specialSize = specialTable.getRange() + 1;
        final int[] normalOcc = NumUtils.newEmptyIntArray(
                normalSize);
        final int[] specialOcc = NumUtils.newEmptyIntArray(
                specialSize);
        for (int i = 0; i < lts
                .size(); i++) {
            LotteryRecord record = lts.get(i);
            for (int v : record.getNormals()) {
                normalOcc[v]++;
            }
            for (int v : record.getSpecials()) {
                specialOcc[v]++;
            }
        }
        float totalNormalOcc = NumUtils.calculateTotalInIntArray(normalOcc);
        float totalSpecialOcc = NumUtils.calculateTotalInIntArray(specialOcc);

        for (int i = 1; i < normalSize; i++) {
            normalTable.getWithNumber(i).setWeight(normalOcc[i] / totalNormalOcc);
        }
        for (int i = 1; i < specialSize; i++) {
            specialTable.getWithNumber(i).setWeight(specialOcc[i] / totalSpecialOcc);
        }


    }
}
