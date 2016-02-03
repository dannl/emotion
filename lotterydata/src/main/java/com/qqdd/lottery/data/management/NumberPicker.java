package com.qqdd.lottery.data.management;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.NumberTable;

import java.util.List;

/**
 * Created by danliu on 1/27/16.
 */
public interface NumberPicker {

    void pick(List<HistoryItem> history, NumberTable normals, NumberTable specials, Lottery picked);

}
