package com.qqdd.lottery.data.management;

import com.qqdd.lottery.data.HistoryItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by danliu on 1/19/16.
 */
public abstract class DataSource {

    public abstract List<HistoryItem> getAll();

    public abstract List<HistoryItem> getNewSince(@NotNull HistoryItem since);
}
