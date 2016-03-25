package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.data.HistoryDetail;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.KeyValuePair;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.UserSelection;
import com.qqdd.lottery.data.management.DataSource;
import com.qqdd.lottery.data.management.History;
import com.qqdd.lottery.data.management.UserSelections;
import com.qqdd.lottery.utils.NumUtils;
import com.qqdd.lottery.utils.Random;
import com.qqdd.lottery.utils.SimpleIOUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by danliu on 3/24/16.
 */
public class SumPicker {

    private File mRoot;

    public SumPicker(File root) {
        mRoot = root;
    }

    public void listUniverse(Lottery.Type type) {
        System.out.println("get all started");
        List<SimpleLottery> all = getAllLotteries(type);
        System.out.println("get all finished");
        HashMap<Integer, List<SimpleLottery>> sumDistribution = new HashMap<>();
        for (int i = 0; i < all.size(); i++) {
            SimpleLottery lottery = all.get(i);
            int sum = sum(lottery);
            List<SimpleLottery> value = sumDistribution.get(sum);
            if (value == null) {
                value = new ArrayList<>();
                sumDistribution.put(sum, value);
            }
            value.add(lottery);
        }
        System.out.println("map finished");

        Set<Map.Entry<Integer, List<SimpleLottery>>> entries = sumDistribution.entrySet();
        List<KeyValuePair> pairs = new ArrayList<>();
        for (Map.Entry<Integer, List<SimpleLottery>> entry : entries) {
            pairs.add(new KeyValuePair(String.valueOf(entry.getKey()), entry.getValue()
                    .size()));
        }
        Collections.sort(pairs, new Comparator<KeyValuePair>() {
            @Override
            public int compare(KeyValuePair o1, KeyValuePair o2) {
                return Integer.parseInt(o1.getKey()) - Integer.parseInt(o2.getKey());
            }
        });
        try {
            SimpleIOUtils.saveToFile(new File(mRoot, type + "_all_sums"),
                    KeyValuePair.toArray(pairs)
                            .toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void calculateAndSave(Lottery.Type type, int[] range, int[] ignore, int count) {
        if (range == null || range.length != 2 || range[0] > range[1] || range[0] < 0) {
            throw new IllegalArgumentException("bad range!");
        }
        if (count < 1) {
            throw new IllegalArgumentException("bad count " + count);
        }
        System.out.println("get all started");
        List<SimpleLottery> all = getAllLotteries(type);
        System.out.println("get all finished");
        HashMap<Integer, List<SimpleLottery>> sumDistribution = new HashMap<>();
        for (int i = 0; i < all.size(); i++) {
            SimpleLottery lottery = all.get(i);
            int sum = sum(lottery);
            List<SimpleLottery> value = sumDistribution.get(sum);
            if (value == null) {
                value = new ArrayList<>();
                sumDistribution.put(sum, value);
            }
            value.add(lottery);
        }
        System.out.println("map finished");
        List<SimpleLottery> filtered = new ArrayList<>();
        for (int i = range[0]; i < range[1]; i++) {
            boolean ignoreIndex = false;
            if (ignore != null) {
                for (int j = 0; j < ignore.length; j++) {
                    if (ignore[j] == i) {
                        ignoreIndex = true;
                        break;
                    }
                }
            }
            if (ignoreIndex) {
                continue;
            }
            final List<SimpleLottery> values = sumDistribution.get(i);
            if (values != null) {
                filtered.addAll(values);
            }
        }
        System.out.println(
                "select " + count + " from filtered " + filtered.size() + " items with all item size : " + all.size());

        Random.getInstance()
                .init();
        final List<Lottery> result = new ArrayList<>();
        NumberList selectedIndex = new NumberList(count);
        while (selectedIndex.size() < count) {
            int index = Random.getInstance()
                    .nextInt(filtered.size());
            selectedIndex.add(index);
        }

        final LotteryConfiguration config = LotteryConfiguration.getWithType(type);
        for (int i = 0; i < selectedIndex.size(); i++) {
            SimpleLottery item = filtered.get(selectedIndex.get(i));
            Lottery ltr = Lottery.newLotteryWithConfiguration(config);
            ltr.replaceAllNormals(item.normal);
            ltr.replaceAllSpecials(item.special);
            result.add(ltr);
        }

        final List<UserSelection> userSelections = new ArrayList<>(result.size());
        for (int i = 0; i < result.size(); i++) {
            final UserSelection useSelection = new UserSelection(result.get(i));
            useSelection.sort();
            userSelections.add(useSelection);
            System.out.println(useSelection);
        }


        UserSelections manager = new UserSelections(
                new File(mRoot, UserSelections.getCacheFolderWithType(type)));
        manager.addUserSelection(userSelections);

    }

    public void historySumDistribution(Lottery.Type type) {
        try {
            List<KeyValuePair> sum = new ArrayList<>();
            List<KeyValuePair> av = new ArrayList<>();
            List<KeyValuePair> av5 = new ArrayList<>();
            List<KeyValuePair> av10 = new ArrayList<>();
            List<HistoryItem> items = new History(mRoot).load(type);
            Collections.reverse(items);
            double total = 0;
            for (int i = items.size() * 4 / 5; i < items.size(); i++) {
                final HistoryItem item = items.get(i);
                sum.add(new KeyValuePair(item.getDateDisplay(), sum(item)));
                total += sum(item);
                av.add(new KeyValuePair(item.getDateDisplay(), (float) (total / sum.size())));
                double total5 = 0;
                for (int j = i; j > i - 5; j--) {
                    total5 += sum(items.get(j));
                }
                av5.add(new KeyValuePair(item.getDateDisplay(), (float) (total5 / 5)));
                double total10 = 0;
                for (int j = i; j > i - 10; j--) {
                    total10 += sum(items.get(j));
                }
                av10.add(new KeyValuePair(item.getDateDisplay(), (float) (total10 / 10)));
            }
            SimpleIOUtils.saveToFile(new File(mRoot, type + "_sum_line"), KeyValuePair.toArray(sum)
                    .toString());
            SimpleIOUtils.saveToFile(new File(mRoot, type + "_sum_line_av"),
                    KeyValuePair.toArray(av)
                            .toString());
            SimpleIOUtils.saveToFile(new File(mRoot, type + "_sum_line_av5"),
                    KeyValuePair.toArray(av5)
                            .toString());
            SimpleIOUtils.saveToFile(new File(mRoot, type + "_sum_line_av10"),
                    KeyValuePair.toArray(av10)
                            .toString());
        } catch (DataSource.DataLoadingException | IOException e) {
            e.printStackTrace();
        }
    }

    public void calculateHistoryDistribution(Lottery.Type type) {
        try {
            listUniverse(type);
            List<KeyValuePair> all = KeyValuePair.parseArray(new JSONArray(
                    SimpleIOUtils.loadContent(
                            new FileInputStream(new File(mRoot, type + "_all_sums")))));
            HistoryDetail detail = HistoryDetail.calculate(new History(mRoot).load(type));
            List<KeyValuePair> historyResult = new ArrayList<>();
            for (int i = 0; i < all.size(); i++) {
                KeyValuePair pair = all.get(i);
                historyResult.add(new KeyValuePair(pair.getKey(),
                        detail.getTotalCounts()[Integer.parseInt(pair.getKey())]));
            }
            SimpleIOUtils.saveToFile(new File(mRoot, type + "_history_sum"),
                    KeyValuePair.toArray(historyResult)
                            .toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataSource.DataLoadingException e) {
            e.printStackTrace();
        }
    }

    private int sum(HistoryItem lottery) {
        int result = 0;
        final List<Integer> normals = lottery.getNormals();
        for (int i = 0; i < normals.size(); i++) {
            result += normals.get(i);
        }
        final List<Integer> specials = lottery.getSpecials();
        for (int i = 0; i < specials.size(); i++) {
            result += specials.get(i);
        }
        return result;
    }

    private int sum(SimpleLottery lottery) {
        int result = 0;
        final List<Integer> normals = lottery.normal;
        for (int i = 0; i < normals.size(); i++) {
            result += normals.get(i);
        }
        final List<Integer> specials = lottery.special;
        for (int i = 0; i < specials.size(); i++) {
            result += specials.get(i);
        }
        return result;
    }

    private List<SimpleLottery> getAllLotteries(Lottery.Type type) {
        final LotteryConfiguration configuration = LotteryConfiguration.getWithType(type);
        List<List<Integer>> normals = NumUtils.exhaustC(configuration.getNormalRange(),
                configuration.getNormalSize());
        List<List<Integer>> specials = NumUtils.exhaustC(configuration.getSpecialRange(),
                configuration.getSpecialSize());
        List<SimpleLottery> allLotteries = new ArrayList<>(normals.size() * specials.size());
        System.out.println(
                "calculateAndSave integers finished! all size: " + normals.size() * specials.size());
        for (int i = 0; i < specials.size(); i++) {
            for (int j = 0; j < normals.size(); j++) {
                if (!hasTooMuchSequence(normals.get(j), type)) {
                    final SimpleLottery simpleLottery = new SimpleLottery(normals.get(j),
                            specials.get(i));
                    int sum = sum(simpleLottery);
                    int odd = odd(normals.get(j));
                    if (type == Lottery.Type.DLT) {
                        //dlt 从57 < sum < 157
                        if (sum >= 157 || sum <= 57) {
                            continue;
                        }
                    } else {
                        //ssq 从63 < sum < 161
                        if (sum >= 161 || sum <= 63) {
                            continue;
                        }
                    }


                    allLotteries.add(simpleLottery);
                }
            }
        }
        System.out.println("all lotteries ready: size: " + allLotteries.size());
        return allLotteries;
    }

    private int odd(final List<Integer> normals) {
        int result = 0;
        for (int i = 0; i < normals.size(); i++) {
            if (normals.get(i) % 2 > 0) {
                result++;
            }
        }
        return result;
    }

    private boolean hasTooMuchSequence(List<Integer> normals, Lottery.Type type) {
        int continuous = 0;
        int continuousMax = 0;
        for (int j = 1; j < normals.size(); j++) {
            if (normals.get(j - 1) - normals.get(j) == 1) {
                continuous++;
            } else {
                if (continuous > 0) {
                    if (continuousMax < continuous) {
                        continuousMax = continuous;
                    }
                }
                continuous = 0;
            }
        }
        if (continuous > 0) {
            continuousMax = continuous;
        }
        if (type == Lottery.Type.DLT) {
            return continuousMax >= 2;
        } else {
            return continuousMax >= 3;
        }
    }

    private class SimpleLottery {

        List<Integer> normal;
        List<Integer> special;

        public SimpleLottery(List<Integer> integers, List<Integer> integers1) {
            normal = integers;
            special = integers1;
        }
    }

}
