package com.qqdd.lottery.data.management;

/**
 * Created by danliu on 1/19/16.
 */
public interface DataLoadingCallback<T> {
    void onLoaded(T result);

    void onLoadFailed(String err);

    void onBusy();

    void onProgressUpdate(Object... progress);
}