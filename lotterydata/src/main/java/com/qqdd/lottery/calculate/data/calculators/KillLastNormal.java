package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.*;

import java.util.List;

/**
 * Created by danliu on 3/14/16.
 */
public class KillLastNormal extends CalculatorImpl {
    public KillLastNormal() {
        super("killLastNormal");
    }

    @Override
    public void calculate(List<HistoryItem> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        final HistoryItem item = lts.get(0);
        for (int i = 0; i < item.getNormals().size(); i++) {
            final com.qqdd.lottery.data.Number number = normalTable.getWithNumber(item
            .getNormals().get(i));
            number.setWeight(0);
        }
    }
}
