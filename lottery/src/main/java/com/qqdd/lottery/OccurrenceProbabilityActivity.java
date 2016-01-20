package com.qqdd.lottery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.qqdd.lottery.data.Configuration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.management.DataLoadingCallback;
import com.qqdd.lottery.data.management.DataProvider;
import com.qqdd.lottery.ui.view.NumberView;

import java.util.List;

import la.niub.util.display.DisplayManager;

public class OccurrenceProbabilityActivity extends AppCompatActivity {

    private RecyclerView mNormalNumberView;
    private RecyclerView mSpecialNumberView;
    private NumberTable mNormalNumbers;
    private NumberTable mSpecialNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occurrence_probability);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNormalNumberView = (RecyclerView) findViewById(R.id.normal_num_area);
        mSpecialNumberView = (RecyclerView) findViewById(R.id.special_num_area);

        final int screenWidth = DisplayManager.screenWidthPixel(this);
        final int columns = (int) (screenWidth / (getResources().getDimensionPixelSize(R.dimen.number_item_size) * 1.4));

        mNormalNumberView.setLayoutManager(new GridLayoutManager(this, columns));
        mSpecialNumberView.setLayoutManager(new GridLayoutManager(this, columns));

        Configuration configuration = Configuration.DLTConfiguration();

        mNormalNumbers = new NumberTable(configuration.getNormalRange());
        mSpecialNumbers = new NumberTable(configuration.getSpecialRange());


        mNormalNumberView.setAdapter(new NumAreaAdapter(mNormalNumbers, NumberView.Display.NORMAL));
        mSpecialNumberView.setAdapter(new NumAreaAdapter(mSpecialNumbers, NumberView.Display.SPECIAL));
    }

}
