package com.qqdd.lottery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.qqdd.lottery.BaseActivity;
import com.qqdd.lottery.R;
import com.qqdd.lottery.activities.adapters.SelectionPagerAdapter;
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

public class SelectionsActivity extends BaseActivity implements SelectionHistory {


    private static final int REQUEST_ADD_MANUAL_SELECTION = 0x1;
    private Lottery.Type mType;
    private SelectionPagerAdapter mAdapter;
    private TextView mIndicator;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = (Lottery.Type) getIntent().getSerializableExtra(Constants.KEY_TYPE);
        if (mType == null) {
            mType = Lottery.Type.DLT;
        }
        setContentView(R.layout.activity_selections);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapter = new SelectionPagerAdapter(this);
        mIndicator = (TextView) findViewById(R.id.page_indicator);
        mPager = (ViewPager) findViewById(R.id.content);
        mPager.setAdapter(mAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndicator.setText(getResources().getString(R.string.pager_indicator_format, position + 1, mAdapter.getCount()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        loadData();

        setTitle(mType.getName() + getTitle());
    }

    private void loadData() {
        showProgress(R.string.loading);
        HistoryDelegate.getInstance().load(mType, new DataLoadingCallback<List<HistoryItem>>() {
            @Override
            public void onLoaded(List<HistoryItem> result) {
                UserSelectionsDelegate.getInstance()
                        .loadNotRedeemed(mType, result,
                                new DataLoadingCallback<UserSelectionOperationResult>() {
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
        mAdapter.setData(result);
        mIndicator.setText(getResources().getString(R.string.pager_indicator_format, mPager.getCurrentItem() + 1, mAdapter.getCount()));
    }

    private void loadFailed() {
        dismissProgress();
        UIUtil.showToastSafe(this, R.string.load_failed);
        finish();
    }

    public void deleteUserSelection(UserSelection userSelection) {
        showProgress(R.string.deleting);
        UserSelectionsDelegate.getInstance().delete(mType, userSelection,
                new DataLoadingCallback<UserSelectionOperationResult>() {
                    @Override
                    public void onLoaded(UserSelectionOperationResult result) {
                        loadData();
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
    public void addUserSelection() {

    }

    @Override
    public void loadMore(LotteryRecord last) {

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
                            loadData();
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

    public void handleManualSelectionClicked(View view) {
        final Intent intent = new Intent(this, ManualSelectionActivity.class);
        intent.putExtra(Constants.KEY_TYPE, mType);
        startActivityForResult(intent, REQUEST_ADD_MANUAL_SELECTION);
    }
}
