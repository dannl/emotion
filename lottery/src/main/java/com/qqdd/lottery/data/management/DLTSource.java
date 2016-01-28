package com.qqdd.lottery.data.management;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
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

    private static final String RECORD_RECOGNIZER_HEAD_0 = "<tr align=\"center\" bgcolor=\"#ffffff\">";
    private static final String RECORD_RECOGNIZER_HEAD_1 = "<tr align=\"center\" bgcolor=\"#f4f4f4\">";
    private static final String RECORD_RECOGNIZER_TAIL = "</tr>";
    private static final Pattern NUMBER_PATTERN = Pattern.compile(
            "<FONT class='FontRed'>([0-9 ]+)</FONT> \\+ <FONT class='FontBlue'>([0-9 ]+)</FONT>");
    private static final Pattern COLUMN_PATTERN = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})");
    private static final Pattern TOTAL_PAGE_PATTERN = Pattern.compile("<span id=\"ContentPlaceHolderDefault_LabelTotalPages\">(\\d+)</span>");
    private LoadTask mRequestTask;

    @Override
    public void getAll(@NonNull DataLoadingCallback callback) {
        if (mRequestTask != null) {
            callback.onBusy();
            return;
        }
        mRequestTask = new LoadTask(null, callback);
        mRequestTask.execute();
    }

    @Override
    public void getNewSince(@NonNull LotteryRecord since, @NonNull DataLoadingCallback callback) {
        if (mRequestTask != null) {
            callback.onBusy();
            return;
        }
        mRequestTask = new LoadTask(since, callback);
        mRequestTask.execute();
    }

    private class LoadTask extends AsyncTask<Void, Void, List<LotteryRecord>> {

        int mIndex = 1;
        boolean mEnded = false;
        private int mRetry = 0;
        private DataLoadingCallback mCallback;
        private LotteryRecord mSince;

        public LoadTask(LotteryRecord since, DataLoadingCallback callback) {
            mCallback = callback;
            mSince = since;
        }


        @Override
        protected void onPostExecute(List<LotteryRecord> lotteryRecords) {
            if (mRetry == 5) {
                mCallback.onLoadFailed("failed to load from server");
            } else {
                mCallback.onLoaded(lotteryRecords);
            }
            mRequestTask = null;
        }

        @Override
        protected List<LotteryRecord> doInBackground(Void... params) {
            SyncHttpClient client = new SyncHttpClient();
            final List<LotteryRecord> result = new ArrayList<>();
            while (!mEnded) {
                final String url = String.format(DLT_REPO_URL_FORMAT, mIndex);
                client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString,
                                          Throwable throwable) {
                        Log.e("DLTSource", "failed: " + responseString);
                        if (mRetry < 5) {
                            mEnded = true;
                        }
                        mRetry ++;
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        List<LotteryRecord> temp = new ArrayList<>();
                        responseString = responseString.replaceAll("\r\n", "");
                        int headIndexer = 0;
                        int head = responseString.indexOf(RECORD_RECOGNIZER_HEAD_0);
                        int tail = -1;
                        if (head >= 0) {
                            tail = responseString.indexOf(RECORD_RECOGNIZER_TAIL, head);
                        }
                        headIndexer ++;
                        while (head >= 0 && tail >= 0) {
                            final String line = responseString.substring(head, tail);
                            final LotteryRecord record = parseLine(line);
                            if (record != null) {
                                temp.add(record);
                            }
                            String headRec = headIndexer % 2 == 0 ? RECORD_RECOGNIZER_HEAD_0 : RECORD_RECOGNIZER_HEAD_1;
                            head = responseString.indexOf(headRec, tail);
                            tail = responseString.indexOf(RECORD_RECOGNIZER_TAIL, head);
                            headIndexer ++;
                        }
                        if (mSince != null) {
                            int indexToRemove = -1;
                            for (int i = 0; i < temp.size(); i++) {
                                if (temp.get(i).equals(mSince)) {
                                    indexToRemove = i;
                                    break;
                                }
                            }
                            if (indexToRemove >= 0) {
                                final List<LotteryRecord> sub = temp.subList(0, indexToRemove);
                                temp = sub;
                                mEnded = true;
                            }
                        }
                        result.addAll(temp);
                        final Matcher totalPageMatcher = TOTAL_PAGE_PATTERN.matcher(responseString);
                        int max = Integer.MAX_VALUE;
                        if (!totalPageMatcher.find()) {
                            mEnded = true;
                        } else {
                            max = Integer.parseInt(totalPageMatcher.group(1));
                        }
                        mIndex++;
                        if (mIndex > max) {
                            mEnded = true;
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
        Lottery lt = Lottery.newDLT();
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
        //FIXME we should
        final LotteryRecord record = new HistoryItem(lt);
        record.setDate(date);
        return record;
    }
}
