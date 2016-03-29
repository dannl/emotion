package com.qqdd.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qqdd.lottery.BaseActivity;
import com.qqdd.lottery.R;
import com.qqdd.lottery.data.Constants;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.management.DataLoadingCallback;
import com.qqdd.lottery.data.management.HistoryDelegate;
import com.qqdd.lottery.ui.view.NumberLineView;

import java.util.List;

public class HistoryActivity extends BaseActivity {

    private Lottery.Type mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dlt);
        mType = (Lottery.Type) getIntent().getSerializableExtra(Constants.KEY_TYPE);
        if (mType == null) {
            mType = Lottery.Type.DLT;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView listView = (ListView) findViewById(R.id.list);
        showProgress(R.string.loading);
        HistoryDelegate.getInstance().load(mType, new DataLoadingCallback<List<HistoryItem>>() {
            @Override
            public void onLoaded(final List<HistoryItem> result) {
                dismissProgress();
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
                        if (convertView == null) {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
                        }
                        TextView date = (TextView) convertView.findViewById(R.id.date);
                        NumberLineView lineView = (NumberLineView) convertView.findViewById(R.id.number);
                        final HistoryItem item = result.get(position);
                        item.sort();
                        date.setText(item.getDateDisplay());
                        lineView.setLottery(item);
                        return convertView;
                    }
                });
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

}
