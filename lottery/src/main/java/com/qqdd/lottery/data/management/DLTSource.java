package com.qqdd.lottery.data.management;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qqdd.lottery.data.LotteryData;
import com.qqdd.lottery.data.LotteryRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by danliu on 1/19/16.
 */
public class DLTSource extends DataSource {

    public static final String DLT_REPO_URL_FORMAT = "http://www.lottery.gov.cn/lottery/dlt/History.aspx?p=%s";

    private static final String RECORD_RECOGNIZER_HEAD = "<tr align=\"center\" bgcolor=\"#ffffff\">";
    private static final String RECORD_RECOGNIZER_TAIL = "</tr>";
    private static final Pattern NUMBER_PATTERN = Pattern.compile(
            "<FONT class='FontRed'>([0-9 ]+)</FONT> \\+ <FONT class='FontBlue'>([0-9 ]+)</FONT>");
    private static final Pattern COLUMN_PATTERN = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})");
    private LoadTask mRequestTask;

    @Override
    public void getAll(@NonNull DataLoadingCallback callback) {
        if (mRequestTask != null) {
            callback.onBusy();
            return;
        }
        mRequestTask = new LoadTask(callback);
        mRequestTask.execute();
    }

    @Override
    public void getNewSince(@NonNull LotteryRecord since, @NonNull DataLoadingCallback callback) {

    }

    private class LoadTask extends AsyncTask<Void, Void, List<LotteryRecord>> {

        int mIndex = 1;
        boolean mEnded = false;
        private DataLoadingCallback mCallback;

        public LoadTask(DataLoadingCallback callback) {
            mCallback = callback;
        }

        @Override
        protected void onPostExecute(List<LotteryRecord> lotteryRecords) {
            if (mCallback != null) {
                mCallback.onLoaded(lotteryRecords);
            }
            mRequestTask = null;
        }

        @Override
        protected List<LotteryRecord> doInBackground(Void... params) {
            SyncHttpClient client = new SyncHttpClient();
            final List<LotteryRecord> result = new ArrayList<LotteryRecord>();
            while (!mEnded) {
                final String url = String.format(DLT_REPO_URL_FORMAT, mIndex);
                client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                                          Throwable throwable) {
                        Log.e("DLTSource", "failed: " + responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        mIndex++;
                        if (mIndex == 2) {
                            mEnded = true;
                        }
                        responseString = responseString.replaceAll("\r\n", "");
                        int head = responseString.indexOf(RECORD_RECOGNIZER_HEAD);
                        int tail = -1;
                        if (head >= 0) {
                            tail = responseString.indexOf(RECORD_RECOGNIZER_TAIL, head);
                        }
                        while (head >= 0 && tail >= 0) {
                            final String line = responseString.substring(head, tail);
                            final LotteryRecord record = parseLine(line);
                            if (record != null) {
                                result.add(record);
                            }
                            head = responseString.indexOf(RECORD_RECOGNIZER_HEAD, tail);
                            tail = responseString.indexOf(RECORD_RECOGNIZER_TAIL, head);
                        }
                    }
                });
            }
            return result;
        }
    }

    private static final Calendar CALENDAR = Calendar.getInstance();

    private static LotteryRecord parseLine(final String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }
        final Matcher matcher = NUMBER_PATTERN.matcher(line);
        LotteryData lt = LotteryData.newDLT();
        if (!matcher.find()) {
            return null;
        }
        final String normalLine = matcher.group(1);
        final String specialLine = matcher.group(2);
        final String[] normalNums = normalLine.split(" +");
        final String[] specialNums = specialLine.split(" +");
        for (String n : normalNums) {
            final int value = Integer.parseInt(n);
            lt.addNormal(value);
        }
        for (String n : specialNums) {
            final int value = Integer.parseInt(n);
            lt.addSpecial(value);
        }
        final Matcher dateMatcher = COLUMN_PATTERN.matcher(line);
        if (!dateMatcher.find()) {
            return null;
        }
        int year = Integer.parseInt(dateMatcher.group(1));
        int month = Integer.parseInt(dateMatcher.group(2));
        int day = Integer.parseInt(dateMatcher.group(3));
        CALENDAR.set(year, month - 1, day, 23, 59, 59);
        Date date = CALENDAR.getTime();
        final LotteryRecord record = new LotteryRecord(lt);
        record.setDate(date);
        return record;
    }
}
