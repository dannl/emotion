package com.qqdd.lottery.data.management;

import com.qqdd.lottery.data.LotteryRecord;

import java.util.List;

/**
 * Created by danliu on 1/19/16.
 */
public interface DataSourceCallback {
    void onLoaded(List<LotteryRecord> result);

    void onLoadFailed(String err);

    void onBusy();
}