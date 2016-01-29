package com.qqdd.lottery.calculate.data;

import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.utils.NumUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by danliu on 1/21/16.
 */
public class NumberProducer {

    private static final class SingletonHolder {
        private static final NumberProducer INSTANCE = new NumberProducer();
    }

    public static NumberProducer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final Random RANDOM = new Random();
    private static final int RANGE_DIVIDER = 100;


    private NumberProducer() {
    }

    public Lottery calculate(List<HistoryItem> history, final NumberTable normals, final NumberTable specials,
                             final LotteryConfiguration lotteryConfiguration)  {
        Lottery result = Lottery.newLotteryWithConfiguration(lotteryConfiguration);
        Set<Integer> normalValues = calculateValues(normals, result.getNormals(),
                lotteryConfiguration.getNormalSize());
        Set<Integer> specialValues = calculateValues(specials, result.getSpecials(),
                lotteryConfiguration.getSpecialSize());
        assert result != null;
        result.replaceAllNormals(normalValues);
        result.replaceAllSpecials(specialValues);
        return result;
    }
    
    private float[] calculateTimeToHomeRate(TimeToGoHome timeToGoHome) {
        if (timeToGoHome == null) {
            return NumUtils.newEmptyFloatArray(RANGE_DIVIDER);
        }
        final int testCount = timeToGoHome.getTestCount();
        final int range = testCount / RANGE_DIVIDER;
        final int[] occ = NumUtils.newEmptyIntArray(RANGE_DIVIDER);
        for (int i = 0; i < timeToGoHome.size(); i++) {
            final int index = timeToGoHome.get(i) / range;
            occ[index] ++;
        }
        return NumUtils.calculateProbability(occ);
    }

    public List<Lottery> select(List<Lottery> tempBuffer, int count, TimeToGoHome timeToGoHome) {
        if (tempBuffer == null || count < 0 || count > tempBuffer.size()) {
            return null;
        }
        float[] rate = calculateTimeToHomeRate(timeToGoHome);
        final List<Lottery> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final int startIndex = NumUtils.calculateIndexWithWeight(rate, RANDOM);
            final int range = tempBuffer.size() / rate.length;
            final int startFrom = startIndex * range;
            final int index = RANDOM.nextInt(range) + startFrom;
            result.add(tempBuffer.get(index));
        }
        return result;
    }


    private Set<Integer> calculateValues(NumberTable table, NumberList alreadySelected, int size) {
        float total = 0;
        for (int i = 0; i < table.size(); i++) {
            final Number number = table.get(i);
            total += number.getWeight();
        }
        final Set<Integer> result = new HashSet<>(size);
        result.addAll(alreadySelected);
        while (result.size() < size) {
            float calculated = RANDOM.nextFloat() * total;
            float indexer = 0f;
            for (int i = 0; i < table.size(); i++) {
                Number number = table.get(i);
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

    private boolean isTableValid(final NumberTable table, int size) {
        int valuedNumberCount = 0;
        for (int i = 0; i < table.size(); i++) {
            final Number number = table.get(i);
            if (number.getWeight() > 0f){
                valuedNumberCount ++;
            }
        }
        return valuedNumberCount > size;
    }



}
