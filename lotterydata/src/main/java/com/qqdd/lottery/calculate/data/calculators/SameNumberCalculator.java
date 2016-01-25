package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.data.RewardRule;
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
        final NumberList sameNormal = new NumberList(normalSameCount);
        final NumberList sameSpecial = new NumberList(specialSameCount);
        while (sameNormal.size() < normalSameCount) {
            int index = RANDOM.nextInt(record.getLottery()
                    .getLotteryConfiguration()
                    .getNormalSize());
            sameNormal.add(record.getNormals()
                    .get(index));
        }

        while (sameSpecial.size() < specialSameCount) {
            int index = RANDOM.nextInt(record.getLottery()
                    .getLotteryConfiguration()
                    .getSpecialSize());
            sameSpecial.add(record.getSpecials()
                    .get(index));
        }

        for (int i = 0; i < sameNormal.size(); i++) {
            final Number number = normalTable.getWithNumber(sameNormal.get(i));
            number.setWeight(number.getWeight() * 5);
        }

        for (int i = 0; i < sameSpecial.size(); i++) {
            final Number number = specialTable.getWithNumber(sameSpecial.get(i));
            number.setWeight(number.getWeight() * 5);
        }
        //TODO minus weight of the left numbers?
    }

    private ProbabilityCache calculateDuplicateProbability(List<LotteryRecord> lts,
                                                           LotteryConfiguration lotteryConfiguration) {
        ProbabilityCache result = new ProbabilityCache();
        int[] normalSameCountRecord = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getNormalSize() + 1);
        int[] specialSameCountRecord = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getSpecialSize() + 1);
        int[] normalSameFrc = NumUtils.newEmptyIntArray(lotteryConfiguration.getNormalRange() + 1);
        int[] specialSameFrc = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getSpecialRange() + 1);
        for (int i = 1; i < lts.size() - 1; i++) {
            final LotteryRecord src = lts.get(i);
            final LotteryRecord dest = lts.get(i + 1);
            final RewardRule.RewardDetail detail = src.getRewardDetail(dest);
            for (
                    int j = 0; i < detail.getNormals()
                    .size(); j++
                    ) {
                normalSameFrc[detail.getNormals()
                        .get(j)]++;
            }
            for (
                    int j = 0; i < detail.getSpecials()
                    .size(); j++
                    ) {
                specialSameFrc[detail.getSpecials()
                        .get(j)]++;
            }
            normalSameCountRecord[detail.getNormals()
                    .size()]++;
            specialSameCountRecord[detail.getSpecials()
                    .size()]++;
        }
        result.normalProbability = NumUtils.calculateProbability(normalSameCountRecord);
        result.specialProbability = NumUtils.calculateProbability(specialSameCountRecord);
        result.normalNumberOccProbability = NumUtils.calculateProbability(normalSameFrc);
        result.specialNumberOccProbability = NumUtils.calculateProbability(specialSameFrc);
        return result;
    }

    private static final class ProbabilityCache {
        float[] normalProbability;
        float[] specialProbability;
        float[] normalNumberOccProbability;
        float[] specialNumberOccProbability;
    }
}
