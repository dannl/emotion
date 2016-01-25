package com.qqdd.lottery.test;

import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.data.NumberTable;

import java.util.HashSet;
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

    private NumberProducer() {
    }

    public Lottery calculateSync( final NumberTable normals,  final NumberTable specials,
                                  final LotteryConfiguration lotteryConfiguration)  {
        Set<Integer> normalValues = calculateValues(normals, lotteryConfiguration.getNormalSize());
        Set<Integer> specialValues = calculateValues(specials, lotteryConfiguration.getSpecialSize());
        final Lottery result = Lottery.newLotteryWithConfiguration(lotteryConfiguration);
        assert result != null;
        result.addNormals(normalValues);
        result.addSpecials(specialValues);
        return result;
    }

    private Set<Integer> calculateValues(NumberTable table, int size) {
        float total = 0;
        for (int i = 0; i < table.size(); i++) {
            final Number number = table.get(i);
            total += number.getWeight();
        }
        final Set<Integer> result = new HashSet<>(size);
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
