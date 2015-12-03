package com.danliu.zjddsigner;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by danliu on 12/2/15.
 */
public class BackgroundService extends IntentService {

    public static final String TAG = "BackgroundService";

    private int mRetry;

    public BackgroundService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent called");
        mRetry = 0;
        execute(intent);
    }

    private void execute(Intent intent) {
        if (mRetry > 2) {
            notifyErr(intent);
//            final Intent lIntent = new Intent(this, MainActivity.class);
//            lIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            lIntent.putExtra(Intent.EXTRA_TITLE, intent.getStringExtra(Intent.EXTRA_TITLE));
//            startActivity(lIntent);
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            final Intent a = new Intent(getApplicationContext(), BackgroundService.class);
            PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, a, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY, alarmIntent);
            Log.d("MainActivity", "alarm set!");
            return;
        }
        mRetry ++;
//        execute(intent);
//        if (true) {
//            execute(intent);
//            return;
//        }
        Request.Builder request = new Request.Builder();
        request.url(intent.getData().toString())
                .get()
                ;
        Request rq = request.build();
        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(rq).execute();
            if (response != null && response.isSuccessful()) {
                Log.d(TAG, response.body()
                        .string());
                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                final Intent a = new Intent(getApplicationContext(), BackgroundService.class);
                PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0, a, PendingIntent.FLAG_UPDATE_CURRENT);
                manager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY, alarmIntent);
                Log.d("MainActivity", "alarm set!");
            } else {
                execute(intent);
                Log.e(TAG, "requst err!!!!!");
            }
        } catch (IOException e) {
            Log.e(TAG, "io exception!" + e);
            execute(intent);
        }
    }

    private void notifyErr(Intent intent) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setTicker("签到没有成功...")
                .setContentTitle("签到失败")
                .setContentText(intent.getStringExtra(Intent.EXTRA_TITLE) + "刷签到失败了！..")
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setContentIntent(pendingIntent).build();
        manager.notify(R.layout.activity_main, notification);
    }
}
