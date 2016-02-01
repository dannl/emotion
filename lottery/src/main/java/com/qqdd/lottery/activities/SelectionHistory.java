package com.qqdd.lottery.activities;

import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.UserSelection;

/**
 * Created by danliu on 2/1/16.
 */
public interface SelectionHistory {
    void deleteUserSelection(UserSelection userSelection);

    void addUserSelection();

    void loadMore(LotteryRecord last);
}
