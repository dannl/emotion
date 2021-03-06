package com.qqdd.lottery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.qqdd.lottery.BaseActivity;
import com.qqdd.lottery.R;
import com.qqdd.lottery.activities.adapters.SelectionHistoryAdapter;
import com.qqdd.lottery.data.Constants;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.UserSelection;
import com.qqdd.lottery.data.management.DataLoadingCallback;
import com.qqdd.lottery.data.management.HistoryDelegate;
import com.qqdd.lottery.data.management.UserSelectionOperationResult;
import com.qqdd.lottery.data.management.UserSelectionsDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import la.niub.util.utils.UIUtil;

public class SelectionRewardHistoryActivity extends BaseActivity implements SelectionHistory {

    private static final int REQUEST_ADD_MANUAL_SELECTION = 0x1;
    private TextView mSummary;
    private RecyclerView mList;
    private SelectionHistoryAdapter mAdapter;
    private Lottery.Type mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = (Lottery.Type) getIntent().getSerializableExtra(Constants.KEY_TYPE);
        if (mType == null) {
            mType = Lottery.Type.DLT;
        }
        setContentView(R.layout.activity_selection_reward_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSummary = (TextView) findViewById(R.id.summary);
        mList = (RecyclerView) findViewById(R.id.history_list);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SelectionHistoryAdapter(this);
        mList.setAdapter(mAdapter);

        loadData();

        setTitle(mType.getName() + getTitle());
    }

    private void loadData() {
        mAdapter.setIsLoading(true);
        showProgress(R.string.loading);
        HistoryDelegate.getInstance().load(mType, new DataLoadingCallback<List<HistoryItem>>() {
            @Override
            public void onLoaded(List<HistoryItem> result) {
                UserSelectionsDelegate.getInstance()
                        .load(mType,result, new DataLoadingCallback<UserSelectionOperationResult>() {
                            @Override
                            public void onLoaded(UserSelectionOperationResult result) {
                                operationSucceeded(result);
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

    private void operationSucceeded(UserSelectionOperationResult result) {
        dismissProgress();
        mAdapter.setIsLoading(false);
        mAdapter.setData(result);
        mSummary.setText(result.getSummary().toString());
    }

    private void loadFailed() {
        dismissProgress();
        UIUtil.showToastSafe(this, R.string.load_failed);
        finish();
    }

    public void deleteUserSelection(UserSelection userSelection) {
        showProgress(R.string.deleting);
        UserSelectionsDelegate.getInstance().delete(mType,userSelection, new DataLoadingCallback<UserSelectionOperationResult>() {
            @Override
            public void onLoaded(UserSelectionOperationResult result) {
                operationSucceeded(result);
            }

            @Override
            public void onLoadFailed(String err) {
                dismissProgress();
                showSnackBar(err);
            }

            @Override
            public void onBusy() {
                dismissProgress();
                showSnackBar(getString(R.string.duplicated_operation));
            }

            @Override
            public void onProgressUpdate(Object... progress) {

            }
        });
    }

    public void loadMore(final LotteryRecord last) {
        HistoryDelegate.getInstance().load(mType, new DataLoadingCallback<List<HistoryItem>>() {
            @Override
            public void onLoaded(List<HistoryItem> result) {
                UserSelectionsDelegate.getInstance()
                        .loadMore(mType,result, last.getDate()
                                .getTime(),
                                new DataLoadingCallback<UserSelectionOperationResult>() {
                                    @Override
                                    public void onLoaded(UserSelectionOperationResult result) {
                                        operationSucceeded(result);
                                    }

                                    @Override
                                    public void onLoadFailed(String err) {
                                        dismissProgress();
                                        showSnackBar(err);
                                    }

                                    @Override
                                    public void onBusy() {
                                        dismissProgress();
                                        showSnackBar(getString(R.string.duplicated_operation));
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

    public void addUserSelection() {
        final Intent intent = new Intent(this, ManualSelectionActivity.class);
        intent.putExtra(Constants.KEY_TYPE, mType);
        startActivityForResult(intent, REQUEST_ADD_MANUAL_SELECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_MANUAL_SELECTION && resultCode == RESULT_OK) {
            final String json = data.getStringExtra(Constants.KEY_LOTTERY);
            if (!TextUtils.isEmpty(json)) {
                try {
                    final Lottery lottery = Lottery.fromJson(new JSONObject(json));
                    UserSelection userSelection = new UserSelection(lottery);
                    showProgress(R.string.adding);
                    UserSelectionsDelegate.getInstance().addUserSelection(mType, userSelection, new DataLoadingCallback<UserSelectionOperationResult>() {
                        @Override
                        public void onLoaded(UserSelectionOperationResult result) {
                            operationSucceeded(result);
                        }

                        @Override
                        public void onLoadFailed(String err) {
                            dismissProgress();
                            showSnackBar(err);
                        }

                        @Override
                        public void onBusy() {
                            dismissProgress();
                            showSnackBar(getString(R.string.duplicated_operation));
                        }

                        @Override
                        public void onProgressUpdate(Object... progress) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
