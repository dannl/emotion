package com.qqdd.lottery.data.management;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.RewardRule;
import com.qqdd.lottery.data.SSQRewardRule;
import com.qqdd.lottery.utils.SimpleHttpGetter;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by danliu on 1/19/16.
 */
public class SSQSource extends DataSource {

    public static final String SSQ_REQUEST_URL_FORMAT = "http://www.cjcp.com.cn/ajax_kj.php?jsoncallback=jsonp%s&ssq_type=page&pagenum=%s";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("###,###");

    private static final Pattern CONTENT_PATTERN = Pattern.compile("jsonp\\d+\\(\"(.+)\"\\)");
    private static final String RECORD_START = "<tr";
    private static final String RECORD_END = "<\\/tr>";
    private static final String TABLE_LINE_START = "<td>";
    private static final String TABLE_LINE_END = "<\\/td>";
    private static final Pattern X_YUAN_PATTERN = Pattern.compile(
            "<td>(.+)\\\\u5143");//WITH ###,###,### format
    private static final int GO_HOME_INDEX = 2;
    private static final int BUY_HOUSE_INDEX = 3;
    private static final Pattern DATE_PATTERN = Pattern.compile("<td>(\\d+-\\d+-\\d+)<\\\\/td>");
    private static final Pattern NUMBER_LINE_PATTERN = Pattern.compile("<td><div.+<\\\\/div><\\\\/td>");
    private static final Pattern NORMAL_NUMBER_PATTERN = Pattern.compile(
            "<div class=\\\\\"hm_bg\\\\\">(\\d+)<\\\\/div>");
    private static final Pattern SPECIAL_NUMBER_PATTERN = Pattern.compile(
            "<div class=\\\\\"lqhm_bg\\\\\">(\\d+)<\\\\/div>");


    @Override
    public List<HistoryItem> getAll() {
        final LoadTask task = new LoadTask(null);
        return task.doInBackground();
    }

    @Override
    public List<HistoryItem> getNewSince(@NotNull HistoryItem since) {
        final LoadTask task = new LoadTask(since);
        return task.doInBackground();
    }


    private class LoadTask {

        int mIndex = 1;
        boolean mEnded = false;
        private int mRetry = 0;
        private LotteryRecord mSince;

        public LoadTask(LotteryRecord since) {
            mSince = since;
        }


        protected List<HistoryItem> doInBackground(Void... params) {
            final List<HistoryItem> result = new ArrayList<>();
            final long current = System.currentTimeMillis();
            while (!mEnded) {
                final String url = String.format(SSQ_REQUEST_URL_FORMAT, current, mIndex);
                String responseString = SimpleHttpGetter.request(url);
                if (TextUtils.isEmpty(responseString)) {
                    mRetry++;
                    if (mRetry > 5) {
                        mEnded = true;
                    }
                    continue;
                }
                List<HistoryItem> temp = new ArrayList<>();
                responseString = responseString.replaceAll("\\\\r\\\\n", "");
                int head = responseString.indexOf(RECORD_START);
                int tail;
                if (head >= 0) {
                    tail = responseString.indexOf(RECORD_END, head);
                } else {
                    mEnded = true;
                    continue;
                }
                while (head >= 0 && tail >= 0) {
                    final String line = responseString.substring(head, tail);
                    final HistoryItem record = parseLine(line);
                    if (record != null) {
                        temp.add(record);
                    }
                    head = responseString.indexOf(RECORD_START, tail);
                    if (head >= 0) {
                        tail = responseString.indexOf(RECORD_END, head);
                    }
                }
                if (mSince != null) {
                    int indexToRemove = -1;
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i)
                                .equals(mSince)) {
                            indexToRemove = i;
                            break;
                        }
                    }
                    if (indexToRemove >= 0) {
                        final List<HistoryItem> sub = temp.subList(0, indexToRemove);
                        temp = sub;
                        mEnded = true;
                    }
                }
                result.addAll(temp);
                mIndex ++;
            }
            Collections.sort(result, new Comparator<HistoryItem>() {
                @Override
                public int compare(HistoryItem lhs, HistoryItem rhs) {
                    return (int) (rhs.getDate()
                            .getTime() - lhs.getDate()
                            .getTime());
                }
            });
            return result;
        }

    }

    private static HistoryItem parseLine(final String line) {
        if (TextUtils.isEmpty(line)) {
            return null;
        }
        final Matcher dateMatcher = DATE_PATTERN.matcher(line);
        if (!dateMatcher.find()) {
            return null;
        }
        final String dateStr = dateMatcher.group(1);
        Date date = null;
        try {
            date = DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
        }
        if (date == null) {
            return null;
        }
        date.setHours(20);
        date.setMinutes(30);
        final Matcher numLineMatcher = NUMBER_LINE_PATTERN.matcher(line);
        if (!numLineMatcher.find()) {
            return null;
        }
        final String numberLine = numLineMatcher.group();
        final Matcher normalNumberMatcher = NORMAL_NUMBER_PATTERN.matcher(numberLine);
        final NumberList normalList = new NumberList();
        while (normalNumberMatcher.find()) {
            normalList.add(Integer.parseInt(normalNumberMatcher.group(1)));
        }
        if (normalList.isEmpty()) {
            return null;
        }
        final Matcher specialNumberMatcher = SPECIAL_NUMBER_PATTERN.matcher(numberLine);
        final NumberList specialList = new NumberList();
        while (specialNumberMatcher.find()) {
            specialList.add(Integer.parseInt(specialNumberMatcher.group(1)));
        }
        if (specialList.isEmpty()) {
            return null;
        }

        int tdStart = line.indexOf(TABLE_LINE_START);
        if (tdStart < 0) {
            return null;
        }
        int tdEnd = line.indexOf(TABLE_LINE_END, tdStart);

        int index = 0;
        String goHomeMoneyStr = null;
        String buyHouseMoneyStr = null;
        while (tdStart >= 0 && tdEnd >= 0) {
            final String tableLine = line.substring(tdStart, tdEnd + TABLE_LINE_END.length());
            final Matcher matcher = X_YUAN_PATTERN.matcher(tableLine);
            if (matcher.find()) {
                if (index == GO_HOME_INDEX) {
                    goHomeMoneyStr = matcher.group(1);
                } else if (index == BUY_HOUSE_INDEX) {
                    buyHouseMoneyStr = matcher.group(1);
                }
                index++;
            }
            tdStart = line.indexOf(TABLE_LINE_START, tdEnd);
            if (tdStart >= 0) {
                tdEnd = line.indexOf(TABLE_LINE_END, tdStart);
            }
        }

        try {
            final SSQRewardRule rewardRule = new SSQRewardRule();
            if (!TextUtils.isEmpty(goHomeMoneyStr)) {
                Number goHomeNumber = NUMBER_FORMAT.parse(goHomeMoneyStr);
                final RewardRule.Reward reward = new RewardRule.Reward("一等", "6+1",
                        goHomeNumber.intValue());
                reward.setGoHome(true);
                rewardRule.putReward(6 << 2 | 1, reward);
            }
            if (!TextUtils.isEmpty(buyHouseMoneyStr)) {
                Number buyHouseNumber = NUMBER_FORMAT.parse(buyHouseMoneyStr);
                final RewardRule.Reward rewardBuyHouse = new RewardRule.Reward("二等", "5+0",
                        buyHouseNumber.intValue());
                rewardBuyHouse.setBuyHouse(true);
                rewardRule.putReward(6 << 2, rewardBuyHouse);
            }
            final Lottery lottery = Lottery.newLotteryWithConfiguration(LotteryConfiguration.SSQConfiguration());
            lottery.addNormals(new HashSet<>(normalList));
            lottery.addSpecials(new HashSet<>(specialList));
            final HistoryItem result = new HistoryItem(lottery, rewardRule);
            result.setDate(date);
            return result;
        } catch (ParseException e) {
            return null;
        }

    }
}
