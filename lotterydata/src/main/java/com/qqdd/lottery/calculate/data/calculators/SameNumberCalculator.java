package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.utils.NumUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by danliu on 1/25/16.
 */
public class SameNumberCalculator extends CalculatorImpl {

    private static final HashMap<Integer, ProbabilityCache> PROBABILITY_CACHE = new HashMap<>();
    private static final Random RANDOM = new Random();

    public SameNumberCalculator(String title, String desc) {
        super(title, desc);
    }

    @Override
    public void calculate(List<LotteryRecord> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        final LotteryRecord record = lts.get(0);
        ProbabilityCache probabilities = PROBABILITY_CACHE.get(record.hashCode());
        if (probabilities == null) {
            probabilities = calculateDuplicateProbability(lts, record.getLottery()
                    .getLotteryConfiguration());
        }
        int normalSameCount = NumUtils.calculateIndexWithWeight(probabilities.normalProbability,
                RANDOM);
        int specialSameCount = NumUtils.calculateIndexWithWeight(probabilities.specialProbability,
                RANDOM);

        final NumberList recordNormals = record.getNormals();

        final NumberList recordSpecials = record.getSpecials();

        final NumberList sameNormal = new NumberList(normalSameCount);
        final NumberList sameSpecial = new NumberList(specialSameCount);
        while (sameNormal.size() < normalSameCount) {
            int index = recordNormals.get(RANDOM.nextInt(recordNormals.size()));
            sameNormal.add(index);
        }

        while (sameSpecial.size() < specialSameCount) {
            int index = recordSpecials.get(RANDOM.nextInt(recordSpecials.size()));
            sameSpecial.add(index);
        }

        for (int i = 0; i < recordNormals.size(); i++) {
            final Number number = normalTable.getWithNumber(recordNormals.get(i));
            if (sameNormal.contains(number.getValue())) {
                number.setWeight(number.getWeight() * 5);
            } else {
                number.setWeight(number.getWeight() * 2);
            }
        }

        for (int i = 0; i < recordSpecials.size(); i++) {
            final Number number = specialTable.getWithNumber(recordSpecials.get(i));
            if (sameSpecial.contains(number.getValue())) {
                number.setWeight(number.getWeight() * 5);
            } else {
                number.setWeight(number.getWeight() * 2);
            }
        }
    }

    private ProbabilityCache calculateDuplicateProbability(List<LotteryRecord> lts,
                                                           LotteryConfiguration lotteryConfiguration) {
        ProbabilityCache result = new ProbabilityCache();
        int[] normalSameCountRecord = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getNormalSize() + 1);
        int[] specialSameCountRecord = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getSpecialSize() + 1);
        for (int i = 1; i < lts.size() - 1; i++) {
                        final NumberList normalList = lts.get(i).getNormals();
                        final NumberList specialList = lts.get(i).getSpecials();
                        final NumberList lastNormalList = lts.get(i + 1).getNormals();
                        final NumberList lastSpecialList = lts.get(i + 1).getSpecials();
                        final int normalSame = NumUtils.calculateSameCount(normalList, lastNormalList);
                        final int specialSame = NumUtils.calculateSameCount(specialList, lastSpecialList);
            normalSameCountRecord[normalSame]++;
            specialSameCountRecord[specialSame]++;
        }
        result.normalProbability = NumUtils.calculateProbability(normalSameCountRecord);
        result.specialProbability = NumUtils.calculateProbability(specialSameCountRecord);
        return result;
    }

    public static final class ProbabilityCache {
        float[] normalProbability;
        float[] specialProbability;
    }
}
