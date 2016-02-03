package com.qqdd.lottery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.qqdd.lottery.BaseActivity;
import com.qqdd.lottery.LTPrefs;
import com.qqdd.lottery.R;
import com.qqdd.lottery.data.Constants;
import com.qqdd.lottery.data.Lottery;

public class MainActivity extends BaseActivity {

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
        if (id == R.id.action_dlt_history) {
            final Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_ssq_history) {
            final Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putExtra(Constants.KEY_TYPE, Lottery.Type.SSQ);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleDLTClicked(View view) {
        final Intent intent = new Intent(MainActivity.this, OccurrenceProbabilityActivity.class);
        startActivity(intent);
    }

    public void handleSSQClicked(View view) {
        final Intent intent = new Intent(MainActivity.this, OccurrenceProbabilityActivity.class);
        intent.putExtra(Constants.KEY_TYPE, Lottery.Type.SSQ);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mClickCount = 0;
    }

    private int mClickCount = 0;
    private Toast mToast;

    public void handleTestTogglerClicked(View view) {
        mClickCount ++;
        if (mClickCount == 18) {
            showTestSwitchHint(2);
        } else if (mClickCount == 19) {
            showTestSwitchHint(1);
        } else if (mClickCount >= 20) {
            showTestSwitchHint(0);
            LTPrefs.getInstance().setShowTestEntrance(true);
        }
    }

    private void showTestSwitchHint(int count) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        if (count == 0) {
            mToast.setText("已打开测试工具，点大乐透或者双色球就可以看到了！！");
        } else {
            mToast.setText("再点击" + count + "次打开测试工具！");
        }
        mToast.show();
    }
}
