package com.qqdd.lottery.calculate.data.calculator;

import android.util.SparseArray;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.*;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.utils.NumUtils;
import com.qqdd.lottery.utils.data.NumberList;

import java.util.List;
import java.util.Random;

/**
 * Created by danliu on 1/25/16.
 */
public class SameNumberCalculator extends CalculatorImpl {

    private static final SparseArray<ProbabilityCache> PROBABILITY_CACHE = new SparseArray<>();
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
            probabilities = calculateDuplicateProbability(lts, record.getLottery().getLotteryConfiguration());
        }
        int normalSameCount = NumUtils.calculateIndexWithWeight(probabilities.normalProbability,
                RANDOM);
        int specialSameCount = NumUtils.calculateIndexWithWeight(probabilities.specialProbability,
                RANDOM);
        final NumberList sameNormal = new NumberList(normalSameCount);
        final NumberList sameSpecial = new NumberList(specialSameCount);
        while(sameNormal.size() < normalSameCount) {
            int index = RANDOM.nextInt(record.getLottery().getLotteryConfiguration().getNormalSize());
            sameNormal.add(record.getNormals().get(index));
        }

        while(sameSpecial.size() < specialSameCount) {
            int index = RANDOM.nextInt(record.getLottery().getLotteryConfiguration().getSpecialSize());
            sameSpecial.add(record.getSpecials().get(index));
        }

        for (int i = 0; i < sameNormal.size(); i++) {
            final Number number = normalTable.getWithNumber(sameNormal.get(i));
            number.setWeight(number.getWeight() * 5);
        }

        for (int i = 0; i < sameSpecial.size(); i++) {
            final com.qqdd.lottery.data.Number number = specialTable.getWithNumber(sameSpecial.get(i));
            number.setWeight(number.getWeight() * 5);
        }
        //TODO minus weight of the left numbers?
    }

    private ProbabilityCache calculateDuplicateProbability(List<LotteryRecord> lts,
                                                           LotteryConfiguration lotteryConfiguration) {
        ProbabilityCache result = new ProbabilityCache();
        int[] normalSameCountRecord = NumUtils.newEmptyIntArray(lotteryConfiguration.getNormalSize() + 1);
        int[] specialSameCountRecord = NumUtils.newEmptyIntArray(lotteryConfiguration.getSpecialSize() + 1);
        for (int i = 1; i < lts.size() - 1; i++) {
            final NumberList normalList = lts.get(i).getNormals();
            final NumberList specialList = lts.get(i).getSpecials();
            final NumberList lastNormalList = lts.get(i + 1).getNormals();
            final NumberList lastSpecialList = lts.get(i + 1).getSpecials();
            final int normalSame = NumUtils.calculateSameCount(normalList, lastNormalList);
            final int specialSame = NumUtils.calculateSameCount(specialList, lastSpecialList);
            normalSameCountRecord[normalSame] ++;
            specialSameCountRecord[specialSame] ++;
        }
        result.normalProbability = NumUtils.calculateProbability(normalSameCountRecord);
        result.specialProbability = NumUtils.calculateProbability(specialSameCountRecord);
        return result;
    }

    private static final class ProbabilityCache {
        float[] normalProbability;
        float[] specialProbability;
    }
}
