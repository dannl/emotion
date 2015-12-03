package com.danliu.zjddsigner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (getIntent().hasExtra(Intent.EXTRA_TITLE)) {
            ((TextView) findViewById(R.id.msg)).setText(getIntent().getStringExtra(Intent.EXTRA_TITLE) + " 刷刷刷出问题了, 重新点一下试试");
        }
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void startDiandian(View view) {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        intent.putExtra(Intent.EXTRA_TITLE, "在家点点");
        intent.setData(Uri.parse("http://app.zaijiadd.com/user-sign.php?token=864103022617481%60ksQqFrOG4Rr9dydGUWSEJAAb1Whw2efT%601356425"));
        PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 3 * 1000, alarmIntent);
        finish();
        Log.d("MainActivity", "alarm set!");
        Toast.makeText(this, "在家点点每日刷单已经开始...!", Toast.LENGTH_SHORT).show();
    }

    public void startNGA(View view) {
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        final Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
//        intent.putExtra(Intent.EXTRA_TITLE, "NGA");
//        intent.setData(Uri.parse("http://app.zaijiadd.com/user-sign.php?token=864103022617481%60ksQqFrOG4Rr9dydGUWSEJAAb1Whw2efT%601356425"));
//        PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        manager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 3 * 1000, alarmIntent);
//        finish();
//        Log.d("MainActivity", "alarm set!");
//        Toast.makeText(this, "NGA每日刷单已经开始...!", Toast.LENGTH_SHORT).show();
    }
}
