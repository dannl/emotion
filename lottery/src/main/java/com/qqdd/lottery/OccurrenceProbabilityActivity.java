package com.qqdd.lottery;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.qqdd.lottery.calculate.CalculatorController;
import com.qqdd.lottery.calculate.CalculatorListAdapter;
import com.qqdd.lottery.calculate.data.CalculatorFactory;
import com.qqdd.lottery.data.Configuration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.management.DataLoadingCallback;
import com.qqdd.lottery.data.management.DataProvider;
import com.qqdd.lottery.ui.view.NumberView;

import java.util.List;

import la.niub.util.display.DisplayManager;

public class OccurrenceProbabilityActivity extends BaseActivity {

    private GridView mNormalNumberView;
    private GridView mSpecialNumberView;
    private NumAreaAdapter mNormalNumberAdapter;
    private NumAreaAdapter mSpecialNumberApdater;
    private NumberTable mNormalNumbers;
    private NumberTable mSpecialNumbers;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mCalculatorsView;
    private CalculatorController mCalculators;
    private CalculatorListAdapter mCalculatorListAdapter;
    private View mFloatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occurrence_probability);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        setupFloating();
        setupNumberView(mNormalNumberView);
        setupNumberView(mSpecialNumberView);
        setupCalculatorsView();

        setupData();
    }


    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNormalNumberView = (GridView) findViewById(R.id.normal_num_area);
        mSpecialNumberView = (GridView) findViewById(R.id.special_num_area);
        mCalculatorsView = (RecyclerView) findViewById(R.id.calculators);
        mFloatingButton = findViewById(R.id.fab);
    }


    private void setupFloating() {
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
        });
    }

    private void setupNumberView(GridView v) {
        final int screenWidth = DisplayManager.screenWidthPixel(this);
        final int columns = (int) (screenWidth / (getResources().getDimensionPixelSize(R.dimen.number_item_size) * 1.3));
        v.setNumColumns(columns);
    }

    private void setupCalculatorsView() {
        mCalculators = new CalculatorController();
        mCalculators.add(CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance().createCalculator());
        mCalculatorsView.setLayoutManager(new LinearLayoutManager(this));
        mCalculatorListAdapter = new CalculatorListAdapter(mCalculators);
        mCalculatorsView.setAdapter(mCalculatorListAdapter);
    }


    private void setupData() {
        Configuration configuration = Configuration.DLTConfiguration();
        mNormalNumbers = new NumberTable(configuration.getNormalRange());
        mSpecialNumbers = new NumberTable(configuration.getSpecialRange());
        mNormalNumberAdapter = new NumAreaAdapter(mNormalNumbers,
                NumberView.Display.NORMAL);
        mNormalNumberView.setAdapter(mNormalNumberAdapter);
        mSpecialNumberApdater = new NumAreaAdapter(mSpecialNumbers,
                NumberView.Display.SPECIAL);
        mSpecialNumberView.setAdapter(
                mSpecialNumberApdater);
    }

    public void handleCalculateNumberClicked(View view) {
        mDrawerLayout.closeDrawer(GravityCompat.END);
    }

    public void handleCalculateClicked(View view) {
        mDrawerLayout.closeDrawer(GravityCompat.END);
        showProgress(R.string.calculating);
        DataProvider.getInstance().loadDLT(new DataLoadingCallback<List<LotteryRecord>>() {

            @Override
            public void onLoaded(List<LotteryRecord> result) {
                mCalculators.calculate(result, mNormalNumbers, mSpecialNumbers,
                        new DataLoadingCallback<NumberTable>() {
                            @Override
                            public void onLoaded(NumberTable result) {
                                dismissProgress();
                                mNormalNumberAdapter.notifyDataSetChanged();
                                mSpecialNumberApdater.notifyDataSetChanged();
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
        });
    }

    private void showSnakeBar(final String msg) {
        Snackbar.make(mFloatingButton, msg, Snackbar.LENGTH_LONG).show();
    }


    public void handleIntroduceClicked(View view) {
        mDrawerLayout.closeDrawer(GravityCompat.END);
    }
}
