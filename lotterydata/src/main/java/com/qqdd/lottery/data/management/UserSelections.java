package com.qqdd.lottery.data.management;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.RewardRule;
import com.qqdd.lottery.data.UserSelection;
import com.qqdd.lottery.data.management.UserSelectionOperationResult.ResultType;
import com.qqdd.lottery.utils.SimpleIOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by danliu on 1/29/16.
 */
public class UserSelections {

    private static final String NOT_REDEEMED_FILE = "not_redeemed";
    private static final String SUMMARY_FILE = "summary";
    private static final int LOADING_CAPACITY = 5;
    public static final String getCacheFolderWithType(Lottery.Type type) {
        return type.toString() + "_US";
    }

    private File mRootFile;
    private UserSelectionOperationResult mCache;
    private UserSelectionSummary mSummary;

    public UserSelections(final File root) {
        mRootFile = root;
        assert root != null;
        if (!root.exists()) {
            root.mkdirs();
        }
    }


    public UserSelectionOperationResult getNotRedeemed(List<HistoryItem> history) {
        final UserSelectionOperationResult result = getUserSelectionList(history);
        List<LotteryRecord> remove = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            final LotteryRecord record = result.get(i);
            if (record instanceof  HistoryItem) {
                remove.add(record);
            } else if (record instanceof  UserSelection) {
                if (((UserSelection) record).getParentTime() > 0) {
                    remove.add(record);
                }
            }
        }
        result.removeAll(remove);
        return result;
    }


    public UserSelectionOperationResult getUserSelectionList(List<HistoryItem> history) {
        loadSummaryIfNeeded();
        if (mCache == null) {
            mCache = getLatest(history, mRootFile);
        }
        mCache.setSummary(mSummary);
        return new UserSelectionOperationResult(mCache, ResultType.SUCCESS);
    }

    public UserSelectionOperationResult loadMore(List<HistoryItem> history, final long since) {
        if (mCache == null) {
            return new UserSelectionOperationResult(new UserSelectionOperationResult(),
                    ResultType.FAILED);
        }
        final UserSelectionOperationResult more = loadMoreImpl(history, since);
        mCache.addAll(more);
        mCache.setHasMore(more.hasMore());
        mCache.setSummary(mSummary);
        return new UserSelectionOperationResult(mCache, ResultType.SUCCESS);
    }

    public UserSelectionOperationResult addUserSelection(UserSelection userSelection) {
        if (userSelection == null) {
            return new UserSelectionOperationResult(mCache, ResultType.FAILED);
        }
        final ArrayList<UserSelection> userSelections = new ArrayList<>(1);
        userSelections.add(userSelection);
        return addUserSelection(userSelections);
    }

    public UserSelectionOperationResult addUserSelection(List<UserSelection> userSelections) {
        loadSummaryIfNeeded();
        final File notRedeemedFile = new File(mRootFile, NOT_REDEEMED_FILE);
        final List<UserSelection> alreadyAdded = new ArrayList<>(userSelections);
        if (notRedeemedFile.exists()) {
            alreadyAdded.addAll(parseUserSelection(notRedeemedFile));
        }
        saveUserSelection(notRedeemedFile, alreadyAdded);
        for (int i = 0; i < userSelections.size(); i++) {
            final UserSelection userSelection = userSelections.get(i);
            if (mCache != null) {
                mCache.add(0, userSelection);
            }
            mSummary.totalBoughtCount++;
            mSummary.totalCost += userSelection.getConfiguration()
                    .getCost();
        }
        saveSummary(mSummary);
        return new UserSelectionOperationResult(mCache, ResultType.SUCCESS);
    }

    public void flushCache(final List<HistoryItem> historyItems) {
        mCache = getLatest(historyItems, mRootFile);
    }

    public UserSelectionOperationResult delete(final UserSelection userSelection) {
        if (userSelection == null) {
            return new UserSelectionOperationResult(mCache, ResultType.FAILED);
        }
        final LotteryRecord result = mCache.remove(mCache.indexOf(userSelection));
        if (result == null) {
            return new UserSelectionOperationResult(mCache, ResultType.FAILED);
        }
        File fileToSave;
        if (userSelection.getParentTime() > 0) {
            fileToSave = new File(mRootFile, String.valueOf(userSelection.getParentTime()));
        } else {
            fileToSave = new File(mRootFile, NOT_REDEEMED_FILE);
        }
        List<UserSelection> userSelections = parseUserSelection(fileToSave);
        userSelections.remove(userSelections.indexOf(userSelection));
        saveUserSelection(fileToSave, userSelections);
        final RewardRule.RewardDetail rewardDetail = userSelection.getRewardDetail();
        if (rewardDetail != null) {
            final int money = rewardDetail.getReward()
                    .getMoney();
            if (money > 0) {
                mSummary.totalReward -= money;
                mSummary.totalRewardTime--;
            }
        }
        mSummary.totalBoughtCount--;
        mSummary.totalCost -= userSelection.getConfiguration()
                .getCost();
        saveSummary(mSummary);
        mCache.setSummary(mSummary);
        return new UserSelectionOperationResult(mCache, ResultType.SUCCESS);
    }

    private void loadSummaryIfNeeded() {
        if (mSummary != null) {
            return;
        }
        final File summaryFile = new File(mRootFile, SUMMARY_FILE);
        if (summaryFile.exists()) {
            try {
                final String s = SimpleIOUtils.loadContent(new FileInputStream(summaryFile),
                        "UTF-8");
                mSummary = UserSelectionSummary.fromJson(new JSONObject(s));
            } catch (IOException | JSONException ignored) {
            }
        }
        if (mSummary == null) {
            UserSelectionSummary summary = new UserSelectionSummary();
            //FIXME what to do if user deleted the file...
//            UserSelectionOperationResult list = getLatest(history, mRootFile);
//            do {
//                if (list.isEmpty()) {
//                    break;
//                }
//                for (int i = 0; i < list.size(); i++) {
//                    LotteryRecord record = list.get(i);
//                    if (record instanceof UserSelection) {
//                        final RewardRule.RewardDetail detail = ((UserSelection) record).getRewardDetail();
//                        if (detail != null) {
//                            final int money = detail.getReward()
//                                    .getMoney();
//                            if (money > 0) {
//                                summary.totalReward += money;
//                                summary.totalRewardTime++;
//                            }
//                        }
//                        summary.totalCost += record.getLottery()
//                                .getConfiguration()
//                                .getCost();
//                        summary.totalBoughtCount++;
//                    }
//                }
//                list = loadMoreImpl(history, list.get(list.size() - 1)
//                        .getDate()
//                        .getTime());
//            } while (list.hasMore());
            saveSummary(summary);
            mSummary = summary;
        }
    }

    private UserSelectionOperationResult getLatest(List<HistoryItem> history, final File fromDir) {
        final UserSelectionOperationResult result = new UserSelectionOperationResult();
        final File notRedeemedFile = new File(fromDir, NOT_REDEEMED_FILE);
        if (notRedeemedFile.exists()) {
            final List<UserSelection> notRedeemedSelection = loadNotRedeemed(history,
                    notRedeemedFile);
            result.addAll(notRedeemedSelection);
        }
        final String[] files = fromDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                try {
                    return Long.parseLong(name) > 0;
                } catch (Exception e) {
                    return false;
                }
            }
        });
        final List<Long> fileNameValues = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            fileNameValues.add(Long.parseLong(files[i]));
        }
        if (fileNameValues.isEmpty()) {
            return result;
        }
        Collections.sort(fileNameValues, new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return (int) (o2 - o1);
            }
        });

        for (int i = 0; i < fileNameValues.size() && i < LOADING_CAPACITY - 1; i++) {
            final long value = fileNameValues.get(i);
            final HistoryItem historyItem = getHistoryItemWithExactTime(history, value);
            if (historyItem == null) {
                continue;
            }
            result.add(historyItem);
            final List<UserSelection> parsed = parseUserSelection(
                    new File(mRootFile, String.valueOf(value)));
            for (int j = 0; j < parsed.size(); j++) {
                parsed.get(j)
                        .setParent(historyItem);
            }
            result.addAll(parsed);
        }
        result.setHasMore(fileNameValues.size() > LOADING_CAPACITY - 1);
        return result;
    }

    private List<UserSelection> loadNotRedeemed(List<HistoryItem> history, File notRedeemedFile) {
        final List<UserSelection> notRedeemedSelection = parseUserSelection(notRedeemedFile);
        boolean needUpdateNotRedeemedCache = false;
        //try to calculate and reset cache.
        HashMap<HistoryItem, List<UserSelection>> neededToUpdate = new HashMap<>();
        for (int i = 0; i < notRedeemedSelection.size(); i++) {
            final UserSelection userSelection = notRedeemedSelection.get(i);
            final HistoryItem historyItem = getHistoryItemWithUserSelection(history, userSelection);
            if (historyItem != null) {
                needUpdateNotRedeemedCache = true;
                List<UserSelection> neededToUpdateItems = neededToUpdate.get(historyItem);
                if (neededToUpdateItems == null) {
                    neededToUpdateItems = new ArrayList<>();
                    neededToUpdate.put(historyItem, neededToUpdateItems);
                }
                userSelection.setParent(historyItem);
                neededToUpdateItems.add(userSelection);
                final RewardRule.RewardDetail detail = userSelection.getRewardDetail();
                if (detail != null) {
                    final int money = detail.getReward()
                            .getMoney();
                    if (money > 0) {
                        mSummary.totalReward += money;
                        mSummary.totalRewardTime++;
                    }
                }
            }
        }
        for (Map.Entry<HistoryItem, List<UserSelection>> entry : neededToUpdate.entrySet()) {
            HistoryItem historyItem = entry.getKey();
            List<UserSelection> items = entry.getValue();
            notRedeemedSelection.removeAll(items);
            final long time = historyItem.getDate()
                    .getTime();
            final File cacheFile = new File(mRootFile, String.valueOf(time));
            final List<UserSelection> cachedSelection = parseUserSelection(cacheFile);
            cachedSelection.addAll(items);
            saveUserSelection(cacheFile, cachedSelection);
        }
        if (needUpdateNotRedeemedCache) {
            saveUserSelection(notRedeemedFile, notRedeemedSelection);
            saveSummary(mSummary);
        }
        return notRedeemedSelection;
    }

    private void saveSummary(UserSelectionSummary summary) {
        try {
            SimpleIOUtils.saveToFile(new File(mRootFile, SUMMARY_FILE), summary.toJson()
                    .toString(), "UTF-8");
        } catch (IOException ignored) {
        }
    }

    private HistoryItem getHistoryItemWithUserSelection(final List<HistoryItem> history,
                                                        final UserSelection userSelection) {
        if (history == null || userSelection == null) {
            return null;
        }
        final long time = userSelection.getDate()
                .getTime();
        for (int i = 0; i < history.size() - 1; i++) {
            HistoryItem item = history.get(i);
            HistoryItem item_older = history.get(i + 1);
            if (item.getDate()
                    .getTime() > time && item_older.getDate()
                    .getTime() < time) {
                return item;
            }
        }
        if (time < history.get(history.size() - 1)
                .getDate()
                .getTime()) {
            return history.get(history.size() - 1);
        }
        return null;
    }

    private HistoryItem getHistoryItemWithExactTime(final List<HistoryItem> history,
                                                    final long time) {
        if (history == null) {
            return null;
        }
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i)
                    .getDate()
                    .getTime() == time) {
                return history.get(i);
            }
        }
        return null;
    }

    private List<UserSelection> parseUserSelection(final File file) {
        if (file == null || !file.exists()) {
            return new ArrayList<>(0);
        }
        final List<UserSelection> result = new ArrayList<>();
        try {
            final String content = SimpleIOUtils.loadContent(new FileInputStream(file), "UTF-8");
            final JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                final UserSelection userSelection = UserSelection.fromJson(
                        jsonArray.getJSONObject(i));
                if (userSelection != null) {
                    result.add(userSelection);
                }
            }
        } catch (IOException | JSONException ignored) {
        }
        return result;
    }

    private void saveUserSelection(final File file, final List<UserSelection> userSelections) {
        if (userSelections == null || file == null) {
            return;
        }
        if (file.exists()) {
            file.delete();
        }
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < userSelections.size(); i++) {
            jsonArray.put(userSelections.get(i)
                    .toJson());
        }
        try {
            SimpleIOUtils.saveToFile(file, jsonArray.toString(), "UTF-8");
        } catch (IOException ignored) {
        }
    }

    private UserSelectionOperationResult loadMoreImpl(List<HistoryItem> history, final long since) {
        final UserSelectionOperationResult result = new UserSelectionOperationResult();
        final String[] files = mRootFile.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                try {
                    final long l = Long.parseLong(name);
                    return l > 0 && l < since;
                } catch (Exception e) {
                    return false;
                }
            }
        });
        final List<Long> fileNameValues = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            fileNameValues.add(Long.parseLong(files[i]));
        }
        if (fileNameValues.isEmpty()) {
            return result;
        }
        Collections.sort(fileNameValues, new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return (int) (o2 - o1);
            }
        });

        for (int i = 0; i < fileNameValues.size() && i < LOADING_CAPACITY; i++) {
            final long value = fileNameValues.get(i);
            final HistoryItem historyItem = getHistoryItemWithExactTime(history, value);
            if (historyItem == null) {
                continue;
            }
            result.add(historyItem);
            final List<UserSelection> parsed = parseUserSelection(
                    new File(mRootFile, String.valueOf(value)));
            for (int j = 0; j < parsed.size(); j++) {
                parsed.get(j)
                        .setParent(historyItem);
            }
            result.addAll(parsed);
        }
        result.setHasMore(fileNameValues.size() > LOADING_CAPACITY);
        return result;
    }

    public static final class UserSelectionSummary {
        private int totalBoughtCount;
        private long totalCost;
        private long totalReward;
        private int totalRewardTime;

        private UserSelectionSummary() {
        }

        public int getTotalBoughtCount() {
            return totalBoughtCount;
        }

        public long getTotalCost() {
            return totalCost;
        }

        public long getTotalReward() {
            return totalReward;
        }

        public int getTotalRewardTime() {
            return totalRewardTime;
        }

        private JSONObject toJson() {
            final JSONObject result = new JSONObject();
            try {
                result.put("bought", totalBoughtCount);
                result.put("cost", totalCost);
                result.put("reward", totalReward);
                result.put("rewardTime", totalRewardTime);
            } catch (JSONException ignored) {
            }
            return result;
        }

        private static UserSelectionSummary fromJson(final JSONObject json) {
            final UserSelectionSummary result = new UserSelectionSummary();
            result.totalBoughtCount = json.optInt("bought");
            result.totalCost = json.optLong("cost");
            result.totalReward = json.optLong("reward");
            result.totalRewardTime = json.optInt("rewardTime");
            return result;
        }

        @Override
        public String toString() {
            return "购买个数：" + totalBoughtCount + "\t总花费：" + totalCost + "\n获得奖金：" + totalReward + "\t中奖次数：" + totalRewardTime;
        }
    }
}
