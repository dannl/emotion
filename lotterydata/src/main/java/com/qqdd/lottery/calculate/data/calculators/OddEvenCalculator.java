package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.*;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.data.management.NumberPicker;
import com.qqdd.lottery.utils.NumUtils;
import com.qqdd.lottery.utils.Random;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by danliu on 2/3/16.
 */
public class OddEvenCalculator extends CalculatorImpl implements NumberPicker {

    public OddEvenCalculator() {
        super("oddEven");
    }

    @Override
    public void calculate(List<HistoryItem> lts, NumberTable normalTable,
                          NumberTable specialTable) {

    }

    private static final HashMap<LotteryRecord, Probability> CACHE = new HashMap<>();

    @Override
    public void pick(List<HistoryItem> history, NumberTable normals, NumberTable specials,
                     Lottery picked) {
        if (history == null || history.isEmpty()) {
            return;
        }
        final HistoryItem record = history.get(0);
        Probability probability = CACHE.get(record);
        if (probability == null) {
            probability = calculateProbability(history);
            CACHE.put(record, probability);
        }
        final int normalOddCount = NumUtils.calculateIndexWithWeight(probability.normalProb,
                Random.getInstance());
        final int specialOddCount = NumUtils.calculateIndexWithWeight(probability.specialProb,
                Random.getInstance());

        Set<Integer> normalNumbers = calculateValues(normals, normalOddCount,
                picked.getConfiguration()
                        .getNormalSize());
        Set<Integer> specialNumbers = calculateValues(specials, specialOddCount,
                picked.getConfiguration()
                        .getSpecialSize());
        picked.addNormals(normalNumbers);
        picked.addSpecials(specialNumbers);
        if (!picked.isValid()) {
            throw new IllegalStateException("we did not calculate right at OddEvenCalculator!");
        }
    }


    private Set<Integer> calculateValues(NumberTable table, int oddCount, int size) {
        if (oddCount > size) {
            throw new IllegalStateException("odd count is larger than size!");
        }
        final Set<Integer> result = calculateOddEven(table, 1, oddCount);
        result.addAll(calculateOddEven(table, 0, size - oddCount));
        return result;
    }

    private Set<Integer> calculateOddEven(NumberTable table, int odd, int size) {
        float total = 0;
        for (int i = 0; i < table.size(); i++) {
            final Number number = table.get(i);
            if (number.getValue() % 2 == odd) {
                total += number.getWeight();
            }
        }
        final Set<Integer> result = new HashSet<>(size);
        while (result.size() < size) {
            float calculated = Random.getInstance()
                    .nextFloat() * total;
            float indexer = 0f;
            for (int i = 0; i < table.size(); i++) {
                Number number = table.get(i);
                if (number.getValue() % 2 != odd) {
                    continue;
                }
                float occ = number.getWeight();
                if (calculated >= indexer && calculated < indexer + occ) {
                    result.add(number.getValue());
                    break;
                }
                indexer += occ;
            }
        }
        return result;
    }

    private Probability calculateProbability(List<HistoryItem> history) {
        final LotteryConfiguration configuration = history.get(0)
                .getConfiguration();
        int[] normalOcc = NumUtils.newEmptyIntArray(configuration.getNormalSize() + 1);
        int[] specialOcc = NumUtils.newEmptyIntArray(configuration.getSpecialSize() + 1);
        for (int i = 0; i < history.size(); i++) {
            final HistoryItem item = history.get(i);
            final NumberList normals = item.getNormals();
            final NumberList specials = item.getSpecials();
            int normalOdd = 0;
            for (int j = 0; j < normals.size(); j++) {
                if (normals.get(j) % 2 == 1) {
                    normalOdd++;
                }
            }
            normalOcc[normalOdd]++;

            int specialOdd = 0;
            for (int j = 0; j < specials.size(); j++) {
                if (specials.get(j) % 2 == 1) {
                    specialOdd++;
                }
            }
            specialOcc[specialOdd]++;
        }
        Probability result = new Probability();
        result.normalProb = NumUtils.calculateProbability(normalOcc);
        result.specialProb = NumUtils.calculateProbability(specialOcc);
        return result;
    }

    private static class Probability {
        float[] normalProb;
        float[] specialProb;
    }
}
