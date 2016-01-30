package com.qqdd.lottery.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.qqdd.lottery.BaseActivity;
import com.qqdd.lottery.R;
import com.qqdd.lottery.activities.adapters.NumAreaAdapter;
import com.qqdd.lottery.calculate.CalculatorCollection;
import com.qqdd.lottery.calculate.data.CalculatorFactory;
import com.qqdd.lottery.calculate.data.CalculatorItem;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.UserSelection;
import com.qqdd.lottery.data.management.DataLoadingCallback;
import com.qqdd.lottery.data.management.DataProvider;
import com.qqdd.lottery.data.management.UserSelectionOperationResult;
import com.qqdd.lottery.data.management.UserSelectionManagerDelegate;
import com.qqdd.lottery.ui.view.NumberView;

import java.util.ArrayList;
import java.util.List;

import la.niub.util.display.DisplayManager;

public class OccurrenceProbabilityActivity extends BaseActivity {
    private NumberTable mNormalNumbers;
    private NumberTable mSpecialNumbers;
    private CalculatorCollection mCalculators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occurrence_probability);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupCalculatorsView();

        setupData();
    }


    private void setupCalculatorsView() {
        mCalculators = new CalculatorCollection();
        mCalculators.add(new CalculatorItem(
                CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                        .createCalculator()));
        mCalculators.add(new CalculatorItem(CalculatorFactory.SameNumberCalculatorFactory.instance().createCalculator()));
        mCalculators.add(new CalculatorItem(CalculatorFactory.LastNTimeOccurIncreaseCalculatorFactory.instance().createCalculator()));
    }


    private void setupData() {
        LotteryConfiguration lotteryConfiguration = LotteryConfiguration.DLTConfiguration();
        mNormalNumbers = new NumberTable(lotteryConfiguration.getNormalRange());
        mSpecialNumbers = new NumberTable(lotteryConfiguration.getSpecialRange());

    }

    public void handleCalculateClicked(View view) {
        showProgress(R.string.calculating);
        final String countText = ((TextView) findViewById(R.id.pick_number_count)).getText().toString();
        final String loopCountText = ((TextView) findViewById(R.id.calculate_count)).getText().toString();
        DataProvider.getInstance().loadDLT(new DataLoadingCallback<List<HistoryItem>>() {

            @Override
            public void onLoaded(List<HistoryItem> result) {
                int count = 5;
                try {
                    count = Math.max(1, Math.min(Integer.parseInt(countText), 10));
                } catch (Exception ignore) {
                }
                int loop = 1000;
                try {
                    loop = Math.max(Integer.parseInt(loopCountText), loop);
                } catch (Exception ignore) {
                }
                mCalculators.calculate(result, count, loop,
                        new DataLoadingCallback<List<Lottery>>() {
                            @Override
                            public void onLoaded(final List<Lottery> result) {
                                dismissProgress();
                                final StringBuilder builder = new StringBuilder();
                                for (int i = 0; i < result.size(); i++) {
                                    builder.append(result.get(i).toString()).append("\n");
                                }
                                new AlertDialog.Builder(OccurrenceProbabilityActivity.this)
                                        .setMessage(builder.toString())
                                        .setNegativeButton(R.string.cancel, null)
                                        .setTitle(R.string.calculate_result)
                                        .setPositiveButton(R.string.save_calculate_result, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                saveResult(result);
                                            }
                                        })
                                        .show();
                            }

                            @Override
                            public void onProgressUpdate(Object... progress) {
                                if (progress != null && progress.length > 0) {
                                    showProgress(getString(R.string.calculating) + progress[0]);
                                }
                            }

                            @Override
                            public void onLoadFailed(String err) {
                                dismissProgress();
                                showSnakeBar(err);
                            }

                            @Override
                            public void onBusy() {
                                dismissProgress();
                                showSnakeBar(getString(R.string.duplicated_operation));
                            }
                        });
            }

            @Override
            public void onLoadFailed(String err) {
                dismissProgress();
                showSnakeBar(err);
            }

            @Override
            public void onBusy() {
                dismissProgress();
                showSnakeBar(getString(R.string.duplicated_operation));
            }

            @Override
            public void onProgressUpdate(Object... progress) {

            }
        });
    }

    private void showSnakeBar(final String msg) {
        Snackbar.make(findViewById(R.id.drawer_layout), msg, Snackbar.LENGTH_LONG).show();
    }

    private void saveResult(List<Lottery> result) {
        final List<UserSelection> userSelections = new ArrayList<>(result.size());
        for (int i = 0; i < result.size(); i++) {
            userSelections.add(new UserSelection(result.get(i)));
        }
        UserSelectionManagerDelegate.getInstance().addUserSelections(userSelections, new DataLoadingCallback<UserSelectionOperationResult>() {
            @Override
            public void onLoaded(UserSelectionOperationResult result) {
                new AlertDialog.Builder(OccurrenceProbabilityActivity.this)
                        .setMessage(R.string.view_selection_hint)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.view_it, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleViewSelectionClicked(null);
                            }
                        })
                        .show();
            }

            @Override
            public void onLoadFailed(String err) {
                showSnakeBar(err);
            }

            @Override
            public void onBusy() {
                showSnakeBar(getString(R.string.duplicated_operation));
            }

            @Override
            public void onProgressUpdate(Object... progress) {

            }
        });
    }


    public void handleViewSelectionClicked(View view) {
        final Intent intent = new Intent(this, SelectionHistoryActivity.class);
        startActivity(intent);
    }

}
