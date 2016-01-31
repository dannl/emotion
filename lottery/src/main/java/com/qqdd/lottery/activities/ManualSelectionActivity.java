package com.qqdd.lottery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.qqdd.lottery.R;
import com.qqdd.lottery.activities.adapters.NumAreaAdapter;
import com.qqdd.lottery.data.*;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.ui.view.NumberView;

import java.util.List;

import la.niub.util.display.DisplayManager;

public class ManualSelectionActivity extends AppCompatActivity {


    private GridView mNormalNumberView;
    private GridView mSpecialNumberView;
    private NumAreaAdapter mNormalNumberAdapter;
    private NumAreaAdapter mSpecialNumberAdapter;
    private View mConfirmView;
    private LotteryConfiguration mConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Lottery.Type type = (Lottery.Type) getIntent().getSerializableExtra(Constants.KEY_TYPE);
        if (type == null) {
            finish();
            return;
        }

        final LotteryConfiguration configuration = LotteryConfiguration.getWithType(type);
        if (configuration == null) {
            finish();
            return;
        }

        initViews(configuration);
        setupNumberView(mNormalNumberView);
        setupNumberView(mSpecialNumberView);

    }

    private void initViews(LotteryConfiguration configuration) {
        mConfiguration = configuration;
        mNormalNumberView = (GridView) findViewById(R.id.normal_num_area);
        mSpecialNumberView = (GridView) findViewById(R.id.special_num_area);

        mNormalNumberView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mSpecialNumberView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        mNormalNumberView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mNormalNumberAdapter.toggleSelected(position);
                updateConfirmStatus();
            }
        });
        mSpecialNumberView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSpecialNumberAdapter.toggleSelected(position);
                updateConfirmStatus();
            }
        });

        mNormalNumberAdapter = new NumAreaAdapter(configuration.getNormalRange(),
                NumberView.Display.NORMAL);
        mNormalNumberView.setAdapter(mNormalNumberAdapter);
        mSpecialNumberAdapter = new NumAreaAdapter(configuration.getSpecialRange(),
                NumberView.Display.SPECIAL);
        mSpecialNumberView.setAdapter(mSpecialNumberAdapter);

        mConfirmView = findViewById(R.id.confirm);
        mConfirmView.setEnabled(false);
        mConfirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<com.qqdd.lottery.data.Number> normal = mNormalNumberAdapter.getSelections();
                final List<Number> specials = mSpecialNumberAdapter.getSelections();
                Lottery lottery = Lottery.newLotteryWithConfiguration(mConfiguration);
                assert lottery != null;
                for (int i = 0; i < normal.size(); i++) {
                    lottery.addNormal(normal.get(i).getValue());
                }
                for (int i = 0; i < specials.size(); i++) {
                    lottery.addSpecial(specials.get(i).getValue());
                }
                final Intent intent = new Intent();
                intent.putExtra(Constants.KEY_LOTTERY, lottery.toJson().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    private void updateConfirmStatus() {
        mConfirmView.setEnabled(mNormalNumberAdapter.getSelections().size() == mConfiguration.getNormalSize() && mSpecialNumberAdapter.getSelections().size() == mConfiguration.getSpecialSize());
    }

    private void setupNumberView(GridView v) {
        final int screenWidth = DisplayManager.screenWidthPixel(this);
        final int columns = (int) (screenWidth / (getResources().getDimensionPixelSize(R.dimen.number_item_size) * 1.3));
        v.setNumColumns(columns);
    }
}
