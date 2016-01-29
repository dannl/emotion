package com.qqdd.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.qqdd.lottery.BaseActivity;
import com.qqdd.lottery.R;
import com.qqdd.lottery.activities.adapters.SelectionHistoryAdapter;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.management.DataLoadingCallback;
import com.qqdd.lottery.data.management.DataProvider;
import com.qqdd.lottery.data.management.UserSelectionOperationResult;
import com.qqdd.lottery.data.management.UserSelectionManagerDelegate;

import java.util.List;

import la.niub.util.utils.UIUtil;

public class SelectionHistoryActivity extends BaseActivity {

    private TextView mSummary;
    private RecyclerView mList;
    private SelectionHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSummary = (TextView) findViewById(R.id.summary);
        mList = (RecyclerView) findViewById(R.id.history_list);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SelectionHistoryAdapter();
        mList.setAdapter(mAdapter);

        loadData();
    }

    private void loadData() {
        mAdapter.setIsLoading(true);
        showProgress(R.string.loading);
        DataProvider.getInstance().loadDLT(new DataLoadingCallback<List<HistoryItem>>() {
            @Override
            public void onLoaded(List<HistoryItem> result) {
                UserSelectionManagerDelegate.getInstance().load(result, new DataLoadingCallback<UserSelectionOperationResult>() {
                    @Override
                    public void onLoaded(UserSelectionOperationResult result) {
                        dismissProgress();
                        mAdapter.setIsLoading(false);
                        mAdapter.setData(result);
                        mSummary.setText(result.getSummary().toString());
                    }

                    @Override
                    public void onLoadFailed(String err) {
                        loadFailed();
                    }

                    @Override
                    public void onBusy() {
                        loadFailed();
                    }

                    @Override
                    public void onProgressUpdate(Object... progress) {

                    }
                });
            }

            @Override
            public void onLoadFailed(String err) {
                loadFailed();
            }

            @Override
            public void onBusy() {
                loadFailed();
            }

            @Override
            public void onProgressUpdate(Object... progress) {

            }
        });
    }

    private void loadFailed() {
        dismissProgress();
        UIUtil.showToastSafe(this, R.string.load_failed);
        finish();
    }

}
