package com.qqdd.lottery.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qqdd.lottery.BaseActivity;
import com.qqdd.lottery.R;
import com.qqdd.lottery.activities.adapters.TestAlgorithmResultAdapter;
import com.qqdd.lottery.calculate.data.CalculatorCollection;
import com.qqdd.lottery.data.Constants;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.UserSelection;
import com.qqdd.lottery.data.management.Calculation;
import com.qqdd.lottery.data.management.DataLoadingCallback;
import com.qqdd.lottery.data.management.DataSource;
import com.qqdd.lottery.data.management.HistoryDelegate;
import com.qqdd.lottery.data.management.ProgressCallback;
import com.qqdd.lottery.test.TestAlgorithm;
import com.qqdd.lottery.ui.view.FileUtils;

import java.util.ArrayList;
import java.util.List;

import la.niub.util.utils.AsyncTask;
import la.niub.util.utils.AsyncTaskUtils;
import la.niub.util.utils.UIUtil;

public class TestActivity extends BaseActivity implements SelectionHistory {

    private static final int DEFAULT_TEST_COUNT = 10000;

    private TextView mTestResult;
    private Lottery.Type mType;
    private EditText mTestCount;
    private TestAlgorithmResultAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mType = (Lottery.Type) getIntent().getSerializableExtra(Constants.KEY_TYPE);
        if (mType == null) {
            mType = Lottery.Type.DLT;
        }

        setTitle(mType.getName() + getTitle());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTestResult =  (TextView) findViewById(R.id.test_result);
        final RecyclerView resultContent = (RecyclerView) findViewById(R.id.result_content);
        mTestCount = (EditText) findViewById(R.id.test_count);

        mAdapter = new TestAlgorithmResultAdapter(this);
        resultContent.setLayoutManager(new LinearLayoutManager(this));
        resultContent.setAdapter(mAdapter);

    }

    public void handleCalculateCLicked(View view) {
        final List<CalculatorCollection> calculatorCollections = Calculation.allCalculorGroups();
        final CharSequence[] items = new CharSequence[calculatorCollections.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = calculatorCollections.get(i).getTitle() + "\n" + calculatorCollections.get(i).getDesc();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_calculator_set);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calculationImpl(calculatorCollections.get(which));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void calculationImpl(final CalculatorCollection calculators) {
        int testTime = DEFAULT_TEST_COUNT;
        try {
            testTime = Math.max(testTime, Integer.parseInt(mTestCount.getText().toString()));
        }catch (Exception ignored) {

        }
        if (mTask != null) {
            showSnackBar(getString(R.string.duplicated_operation));
            return;
        }
        final int finalTestTime = testTime;
        showProgress(R.string.calculating);
        HistoryDelegate.getInstance().load(mType, new DataLoadingCallback<List<HistoryItem>>() {
            @Override
            public void onLoaded(final List<HistoryItem> result) {
                testImpl(result, calculators, finalTestTime);
            }

            @Override
            public void onLoadFailed(String err) {
                dismissProgress();
            }

            @Override
            public void onBusy() {
                dismissProgress();
            }

            @Override
            public void onProgressUpdate(Object... progress) {

            }
        });

    }

    private void testImpl(final List<HistoryItem> result, final CalculatorCollection calculators,
                          final int finalTestTime) {
        final HistoryItem parent = result.get(0);
        mTask = new AsyncTask<Void, Void, TestAlgorithm.TestResult>() {
            @Override
            protected TestAlgorithm.TestResult doInBackground(Void... params) {
                TestAlgorithm testAlgorithm = new TestAlgorithm(FileUtils.getCacheDir());
                try {
                    return testAlgorithm.testAlgorithm(mType, calculators,
                            finalTestTime, result.size(), new ProgressCallback() {
                        @Override
                        public void onProgressUpdate(final String progress) {
                            UIUtil.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    showProgress(progress);
                                }
                            });
                        }
                    }, true);
                } catch (DataSource.DataLoadingException e) {
                    showSnackBar(e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(TestAlgorithm.TestResult testResult) {
                if (null == testResult) {
                    mTestResult.setText(R.string.test_failed);
                } else {
                    mTestResult.setText(testResult.toString());
                    final List<Lottery> result = testResult.allLtResult;
                    final List<LotteryRecord> userSelections = new ArrayList<>(result.size());
                    for (int i = 0; i < result.size(); i++) {
                        final UserSelection userSelection = new UserSelection(result.get(i));
                        userSelection.setParent(parent);
                        userSelections.add(userSelection);
                    }
                    mAdapter.setData(userSelections);
                }
                mTask = null;
                dismissProgress();
            }
        };
        AsyncTaskUtils.executeAsyncTask(mTask, AsyncTaskUtils.Priority.HIGH);
    }

    private AsyncTask<Void, Void, TestAlgorithm.TestResult> mTask;

    @Override
    public void deleteUserSelection(UserSelection userSelection) {

    }

    @Override
    public void addUserSelection() {

    }

    @Override
    public void loadMore(LotteryRecord last) {

    }
}
