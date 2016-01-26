package com.qqdd.lottery;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.qqdd.lottery.calculate.AlgorithmTester;
import com.qqdd.lottery.calculate.CalculatorCollection;
import com.qqdd.lottery.calculate.data.CalculatorFactory;
import com.qqdd.lottery.calculate.data.CalculatorItem;
import com.qqdd.lottery.calculate.data.calculators.SelectionIncreaseCalculator;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.management.DataLoadingCallback;
import com.qqdd.lottery.data.management.DataProvider;
import com.qqdd.lottery.dlt.DLTHistoryActivity;

import java.util.List;

public class MainActivity extends BaseActivity {

    private AlgorithmTester mTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_dlt_history) {
            final Intent intent = new Intent(MainActivity.this, DLTHistoryActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleDLTClicked(View view) {
        final Intent intent = new Intent(MainActivity.this, OccurrenceProbabilityActivity.class);
        startActivity(intent);
    }
}
