package com.qqdd.lottery;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.qqdd.lottery.calculate.AlgorithmTester;
import com.qqdd.lottery.calculate.CalculatorCollection;
import com.qqdd.lottery.calculate.data.CalculatorFactory;
import com.qqdd.lottery.calculate.data.CalculatorItem;
import com.qqdd.lottery.calculate.data.calculator.SelectionIncreaseCalculator;
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
        final TextView msg = (TextView) findViewById(R.id.msg);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showProgress("正在加载数据...");
                DataProvider.getInstance().loadDLT(new DataLoadingCallback<List<LotteryRecord>>() {

                    @Override
                    public void onLoaded(List<LotteryRecord> result) {
                        dismissProgress();
                        CalculatorCollection calculatorList = new CalculatorCollection();
                        calculatorList.add(new CalculatorItem(
                                CalculatorFactory.OccurrenceProbabilityCalculatorFactory.instance()
                                        .createCalculator()));
                        final SelectionIncreaseCalculator selectionIncrease = CalculatorFactory.SelectionIncreaseCalculatorFactory.instance()
                                .createCalculator();
                        selectionIncrease.addNormal(4);
                        selectionIncrease.addNormal(15);
                        selectionIncrease.addNormal(10);
                        selectionIncrease.addNormal(22);
                        selectionIncrease.addNormal(8);
                        selectionIncrease.addNormal(29);
                        selectionIncrease.addNormal(6);
                        selectionIncrease.addNormal(26);
                        selectionIncrease.addSpecial(1);
                        selectionIncrease.addSpecial(4);
                        //calculatorList.add(new CalculatorItem(selectionIncrease));
                        if (mTest == null) {
                            mTest = new AlgorithmTester();
                        }
                        mTest.test(LotteryConfiguration.DLTConfiguration(), result, calculatorList, new DataLoadingCallback<String>() {
                            @Override
                            public void onLoaded(String result) {
                                msg.setText(result);
                                msg.append("测试完毕！");
                            }

                            @Override
                            public void onLoadFailed(String err) {
                                msg.append("测试失败!");
                            }

                            @Override
                            public void onBusy() {
                                Snackbar.make(view, "busy!", Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onProgressUpdate(final Object... progress) {
                                if (progress != null && progress.length > 0) {
                                    msg.setText(String.valueOf(progress[0]));
                                }
                            }
                        });
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
        });
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
}
