package com.qqdd.lottery.data.management;

import com.qqdd.lottery.data.LotteryRecord;

import java.util.ArrayList;

/**
 * Created by danliu on 1/29/16.
 */
public class UserSelectionOperationResult extends ArrayList<LotteryRecord> {

    public enum ResultType {
        SUCCESS, FAILED
    }

    private boolean mHasMore = false;
    private UserSelectionManager.UserSelectionSummary mSummary;
    private ResultType mResultType;

    public UserSelectionOperationResult() {
        super();
    }

    public UserSelectionOperationResult(UserSelectionOperationResult src, ResultType type) {
        super(src);
        mHasMore = src.hasMore();
        mSummary = src.getSummary();
        mResultType = type;
    }

    public ResultType getResultType() {
        return mResultType;
    }

    public boolean hasMore() {
        return mHasMore;
    }

    public void setHasMore(boolean hasMore) {
        mHasMore = hasMore;
    }

    public UserSelectionManager.UserSelectionSummary getSummary() {
        return mSummary;
    }

    void setSummary(UserSelectionManager.UserSelectionSummary summary) {
        mSummary = summary;
    }

}
