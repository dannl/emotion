package com.qqdd.lottery.data;

import com.qqdd.lottery.utils.NumUtils;
import com.qqdd.lottery.utils.SimpleIOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by danliu on 3/24/16.
 */
public class HistoryDetail {

    private int[] mOddCounts;
    private int[] mSequenceCounts;
    private int[] mTotalCounts;
    private Lottery.Type mType;

    public HistoryDetail(Lottery.Type type) {
        mType = type;
        LotteryConfiguration configuration = LotteryConfiguration.getWithType(type);
        mOddCounts = NumUtils.newEmptyIntArray(configuration.getNormalSize() + 1);
        mSequenceCounts = NumUtils.newEmptyIntArray(configuration.getNormalSize());
        int maxTotal = 0;
        for (int i = 0; i < configuration.getNormalSize(); i++) {
            maxTotal += configuration.getNormalRange() - i;
        }
        for (int i = 0; i < configuration.getSpecialSize(); i++) {
            maxTotal += configuration.getSpecialRange() - i;
        }
        mTotalCounts = NumUtils.newEmptyIntArray(maxTotal + 1);
    }

    public int[] getOddCounts() {
        return mOddCounts;
    }

    public int[] getSequenceCounts() {
        return mSequenceCounts;
    }

    public int[] getTotalCounts() {
        return mTotalCounts;
    }

    public Lottery.Type getType() {
        return mType;
    }

    public JSONObject toJson() {
        final JSONObject result = new JSONObject();
        try {
            JSONArray temp = new JSONArray();
            for (int i = 0; i < mOddCounts.length; i++) {
                temp.put(mOddCounts[i]);
            }
            result.put("odd", temp);
            temp = new JSONArray();
            for (int i = 0; i < mSequenceCounts.length; i++) {
                temp.put(mSequenceCounts[i]);
            }
            result.put("seq", temp);
            temp = new JSONArray();
            for (int i = 0; i < mTotalCounts.length; i++) {
                temp.put(mTotalCounts[i]);
            }
            result.put("total", temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(mType.getName() + " historical data: \n");
        builder.append(" 奇数: \n");
        for (int i = 0; i < mOddCounts.length; i++) {
            builder.append(" 个数: " + i + " 次数: " + mOddCounts[i] + "\n");
        }
        builder.append(" 连续: \n");
        for (int i = 0; i < mSequenceCounts.length; i++) {
            builder.append(" 连续个数: " + i + " 次数: " + mSequenceCounts[i] + "\n");
        }
        builder.append(" 和: \n");
        for (int i = 0; i < mTotalCounts.length; i++) {
            if (mTotalCounts[i] > 0) {
                builder.append(" 和值: " + i + " 次数: " + mTotalCounts[i] + "\n");
            }
        }
        return builder.toString();
    }

    public static void save(HistoryDetail item, File root) {
        final File file = new File(root, item.mType.getName() + "_historical");
        try {
            SimpleIOUtils.saveToFile(file, item.toJson().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HistoryDetail load(Lottery.Type type, File root) {
        final File file = new File(root, type.getName() + "_historical");
        if (!file.exists()) {
            throw new IllegalStateException("no cache!");
        }
        try {
            HistoryDetail result = new HistoryDetail(type);
            final JSONObject json = new JSONObject(
                    SimpleIOUtils.loadContent(new FileInputStream(file)));
            JSONArray temp = json.getJSONArray("odd");
            for (int i = 0; i < temp.length(); i++) {
                result.mOddCounts[i] = temp.getInt(i);
            }
            temp = json.getJSONArray("seq");
            for (int i = 0; i < temp.length(); i++) {
                result.mSequenceCounts[i] = temp.getInt(i);
            }
            temp = json.getJSONArray("total");
            for (int i = 0; i < temp.length(); i++) {
                result.mTotalCounts[i] = temp.getInt(i);
            }
            return result;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("no cache!");
    }

    public static HistoryDetail calculate(List<HistoryItem> history) {
        Lottery.Type type = history.get(0)
                .getType();
        HistoryDetail result = new HistoryDetail(type);
        for (int i = 0; i < history.size(); i++) {
            final HistoryItem item = history.get(i);
            item.sort();
            final NumberList normals = item.getNormals();
            int odd = 0;
            int sequence = 0;
            int total = 0;
            for (int j = 0; j < normals.size(); j++) {
                if (normals.get(j) % 2 > 0) {
                    odd++;
                }
                total += normals.get(j);
            }
            result.mOddCounts[odd]++;

            for (int j = 1; j < normals.size(); j++) {
                if (normals.get(j) - normals.get(j - 1) == 1) {
                    sequence ++;
                } else {
                    if (sequence > 0) {
                        result.mSequenceCounts[sequence] ++;
                    }
                    sequence = 0;
                }
            }
            if (sequence > 0) {
                result.mSequenceCounts[sequence] ++;
            }

            for (int j = 0; j < item.getSpecials()
                    .size(); j++) {
                total += item.getSpecials().get(j);
            }

            result.mTotalCounts[total] ++;
        }
        return result;
    }

}
