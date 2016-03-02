package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.NumberTable;

import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 3/2/16.
 */
public class NoSelectionCalculator extends CalculatorImpl {

    private static final HashMap<Lottery.Type,int[][]> sExclusion = new HashMap<>();

    public static void setExclusion(Lottery.Type type, int[][] exclusion) {
        if (exclusion != null && exclusion.length != 2) {
            return;
        }
        if (type == null) {
            return;
        }
        LotteryConfiguration configuration = LotteryConfiguration.getWithType(type);
        assert exclusion != null;
        assert configuration != null;
        for (int i = 0; i < exclusion[0].length; i++) {
            int number = exclusion[0][i];
            if (number < 1 || number > configuration.getNormalRange()) {
                return;
            }
        }
        for (int i = 0; i < exclusion[1].length; i++) {
            int number = exclusion[1][i];
            if (number < 1 || number > configuration.getSpecialRange()) {
                return;
            }
        }
        sExclusion.put(type, exclusion);
    }

    public NoSelectionCalculator() {
        super("选中的号码不选","选中的号码不选");
    }

    @Override
    public void calculate(List<HistoryItem> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        final int[][] ex = sExclusion.get(lts.get(0).getType());
        if (ex == null) {
            return;
        }
        for (int i = 0; i < ex[0].length; i++) {
            normalTable.getWithNumber(ex[0][i]).setWeight(0);
        }
        for (int i = 0; i < ex[1].length; i++) {
            specialTable.getWithNumber(ex[1][i]).setWeight(0);
        }
    }
}
