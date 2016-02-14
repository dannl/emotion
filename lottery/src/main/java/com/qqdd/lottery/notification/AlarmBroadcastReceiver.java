package com.qqdd.lottery.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;

import com.qqdd.lottery.R;
import com.qqdd.lottery.activities.SelectionRewardHistoryActivity;
import com.qqdd.lottery.data.Constants;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.RewardRule;

import java.util.List;

/**
 * handle the alarm to update data and notify the reward result.
 * Created by danliu on 2/14/16.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE_DLT = "com.qqdd.lottery.ACTION_UPDATE_DLT";
    public static final String ACTION_UPDATE_SSQ = "com.qqdd.lottery.ACTION_UPDATE_SSQ";

    private static final int DLT_NOTIFICATION_ID = 2141025;
    private static final int SSQ_NOTIFICATION_ID = 2141026;

    private static final long[] VIB = new long[] {
            200,
            200,
            200,
            200,
            200,
            200
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (TextUtils.equals(ACTION_UPDATE_DLT, action)) {
            updateDLT(context);
        } else if (TextUtils.equals(ACTION_UPDATE_SSQ, action)) {
            updateSSQ(context);
        } else {
            //DO NOTHING.
        }
    }

    private void updateDLT(final Context context) {
        sendNotification(context, Lottery.Type.DLT, null);
        AlarmHelper.registerNextAlarm(context, Lottery.Type.DLT);
    }

    private void updateSSQ(final Context context) {
        sendNotification(context, Lottery.Type.SSQ, null);
        AlarmHelper.registerNextAlarm(context, Lottery.Type.SSQ);
    }

    private void sendNotification(final Context context, final Lottery.Type type,
                                  final List<RewardRule.RewardDetail> rewards) {
        NotificationManager manager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        final Notification.Builder builder = new Notification.Builder(context);
        builder.setVibrate(VIB);
        //FIXME
        if (rewards == null || rewards.isEmpty()) {
//            builder.setTicker("妈蛋...没中啊");
//            builder.setContentText("妈蛋...又浪费钱了...");
        } else {
//            builder.setTicker("测试ticker.");
//            builder.setContentText("测试内容...");
        }
        builder.setTicker(type.getName() + "应该已经有结果了！");
        builder.setContentText("点击查看" + type.getName() + "开奖结果!");
        builder.setContentTitle(type.getName());
        builder.setSmallIcon(R.drawable.ic_add_black);
        final Intent intent = new Intent(context, SelectionRewardHistoryActivity.class);
        intent.putExtra(Constants.KEY_TYPE, type);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        if (type == Lottery.Type.DLT) {
            manager.notify(DLT_NOTIFICATION_ID, builder.build());
        } else if (type == Lottery.Type.SSQ) {
            manager.notify(SSQ_NOTIFICATION_ID, builder.build());
        }
    }

}
