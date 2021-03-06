package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.NumberTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by danliu on 1/23/16.
 */
public class SelectionIncreaseCalculator extends CalculatorImpl {

    private Set<Integer> mNormals;
    private Set<Integer> mSpecials;
    private float mPower;

    public SelectionIncreaseCalculator() {
        super("selectionIncrease");
        mNormals = new HashSet<>();
        mSpecials = new HashSet<>();
        //by default, set power to 2.
        mPower = 2f;
    }

    public void addNormal(final int value) {
        mNormals.add(value);
    }

    public void addSpecial(final int value) {
        mSpecials.add(value);
    }

    public void setPower(float power) {
        mPower = power;
    }

    @Override
    public void calculate(List<HistoryItem> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        calculateWeight(mNormals, normalTable);
        calculateWeight(mSpecials, specialTable);

    }

    private void calculateWeight(Set<Integer> selection, NumberTable normalTable) {
        for (Integer v : selection) {
            final com.qqdd.lottery.data.Number number = normalTable.getWithNumber(v);
            if (number == null) {
                continue;
            }
            number.setWeight(number.getWeight() * mPower);
        }
    }
}
