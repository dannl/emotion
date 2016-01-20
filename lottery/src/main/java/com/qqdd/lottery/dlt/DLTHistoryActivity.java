package com.qqdd.lottery.dlt;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.management.DataLoadingCallback;
import com.qqdd.lottery.data.management.DataProvider;

import java.util.List;

public class DLTHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dlt);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null)
//                        .show();
//            }
//        });
        final ListView listView = new ListView(this);
        setContentView(listView);
        DataProvider.getInstance().loadDLT(new DataLoadingCallback() {
            @Override
            public void onLoaded(final List<LotteryRecord> result) {
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

            }

            @Override
            public void onBusy() {

            }
        });
    }

}
