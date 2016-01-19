package com.qqdd.lottery.data.management;

import com.loopj.android.http.AsyncHttpClient;
import com.qqdd.lottery.data.LotteryRecord;

import org.jetbrains.annotations.NotNull;

/**
 * Created by danliu on 1/19/16.
 */
public abstract class DataSource {

    AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient() {};

    protected AsyncHttpClient getAsyncHttpClient() {
        return mAsyncHttpClient;
    }

    public abstract void getAll(@NotNull DataLoadingCallback callback);

    public abstract void getNewSince(@NotNull LotteryRecord since, @NotNull DataLoadingCallback callback);
}
