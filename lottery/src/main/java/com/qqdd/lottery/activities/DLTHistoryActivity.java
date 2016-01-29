package com.qqdd.lottery.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qqdd.lottery.R;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.management.DataLoadingCallback;
import com.qqdd.lottery.data.management.DataProvider;
import com.qqdd.lottery.utils.NumUtils;

import java.util.List;

public class DLTHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                DataProvider.getInstance()
                        .loadDLT(new DataLoadingCallback<List<HistoryItem>>() {
                            @Override
                            public void onLoaded(List<HistoryItem> result) {
                                if (result == null || result.isEmpty()) {
                                    return;
                                }
                                LotteryConfiguration lotteryConfiguration = result.get(0)
                                        .getLottery()
                                        .getConfiguration();
                                final int[] normalOcc = NumUtils.newEmptyIntArray(
                                        lotteryConfiguration.getNormalRange() + 1);
                                final int[] specialOcc = NumUtils.newEmptyIntArray(
                                        lotteryConfiguration.getSpecialRange() + 1);
                                for (LotteryRecord record : result) {
                                    for (int i : record.getNormals()) {
                                        normalOcc[i]++;
                                    }
                                    for (int i : record.getSpecials()) {
                                        specialOcc[i]++;
                                    }
                                }
                                float totalNormalOcc = NumUtils.calculateTotalInIntArray(normalOcc);
                                float totalSpecialOcc = NumUtils.calculateTotalInIntArray(specialOcc);
                                Log.e("TEST", "=======================================");
                                for (int i = 0; i < normalOcc.length; i++) {
                                    Log.e("TEST", "normal num " + i + " occ rate: " + normalOcc[i] / totalNormalOcc);
                                }
                                Log.e("TEST", "=======================================");
                                for (int i = 0; i < specialOcc.length; i++) {
                                    Log.e("TEST", "special num " + i + " occ rate: " + specialOcc[i] / totalSpecialOcc);
                                }
                                Log.e("TEST", "=======================================");
                            }

                            @Override
                            public void onLoadFailed(String err) {
                                Snackbar.make(view, "load failed: " + err, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null)
                                        .show();
                            }

                            @Override
                            public void onBusy() {
                                Snackbar.make(view, "busy!! try later.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null)
                                        .show();
                            }

                            @Override
                            public void onProgressUpdate(Object... progress) {

                            }
                        });
            }
        });
        final ListView listView = (ListView) findViewById(R.id.list);
        DataProvider.getInstance().loadDLT(new DataLoadingCallback<List<HistoryItem>>() {
            @Override
            public void onLoaded(final List<HistoryItem> result) {
                listView.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return result.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return result.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final TextView textView = new TextView(DLTHistoryActivity.this);
                        textView.setText(result.get(position)
                                .toString());
                        return textView;
                    }
                });
            }

            @Override
            public void onLoadFailed(String err) {
                Snackbar.make(fab, "auto load failed: " + err, Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }

            @Override
            public void onBusy() {
                Snackbar.make(fab, "auto load, it's busy!!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }

            @Override
            public void onProgressUpdate(Object... progress) {

            }
        });
    }

}
