package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.NumberTable;

import java.util.List;

/**
 * Created by danliu on 1/20/16.
 */
public interface Calculator {

    String getTitle();

    void calculate(List<HistoryItem> lts, NumberTable normalTable, NumberTable specialTable);
}
