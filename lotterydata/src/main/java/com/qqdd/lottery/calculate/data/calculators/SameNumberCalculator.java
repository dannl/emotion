package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.*;
import com.qqdd.lottery.data.Number;
import com.qqdd.lottery.utils.NumUtils;
import com.qqdd.lottery.utils.Random;

import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 1/25/16.
 */
public class SameNumberCalculator extends CalculatorImpl {

    private static final HashMap<Integer, Probability> PROBABILITY_CACHE = new HashMap<>();

    public SameNumberCalculator() {
        super("sameNum");
    }

    @Override
    public void calculate(List<HistoryItem> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        final LotteryRecord record = lts.get(0);
        Probability probabilities = PROBABILITY_CACHE.get(record.hashCode());
        if (probabilities == null) {
            probabilities = calculateDuplicateProbability(lts, record.getLottery()
                    .getConfiguration());
            PROBABILITY_CACHE.put(record.hashCode(), probabilities);
        }
        final int normalSameCount = NumUtils.calculateIndexWithWeight(
                probabilities.normalSameCountProb, Random.getInstance());
        final int specialSameCount = NumUtils.calculateIndexWithWeight(
                probabilities.specialSameCountProb, Random.getInstance());
        final List<Integer> normalNumbers = NumUtils.calculateIndexesWithWeight(
                probabilities.normalNumberProbability, Random.getInstance(), normalSameCount);
        final List<Integer> specialNumbers = NumUtils.calculateIndexesWithWeight(
                probabilities.specialNumberProbability, Random.getInstance(), specialSameCount);
        final NumberList recordNormals = record.getNormals();
        for (int i = 0; i < recordNormals.size(); i++) {
            final Integer value = recordNormals.get(i);
            final com.qqdd.lottery.data.Number number = normalTable.getWithNumber(value);
            if (normalNumbers.contains(value)) {
                number.setWeight(
                        number.getWeight() * probabilities.normalNumberProbability[number.getValue()]);
            } else {
                number.setWeight(
                        number.getWeight() / probabilities.normalNumberProbability[number.getValue()]);
            }
        }
        final NumberList recordSpecials = record.getSpecials();
        for (int i = 0; i < recordSpecials.size(); i++) {
            final Integer value = recordSpecials.get(i);
            Number number = specialTable.getWithNumber(value);
            if (specialNumbers.contains(value)) {
                number.setWeight(
                        number.getWeight() * probabilities.specialNumberProbability[number.getValue()]);
            } else {
                number.setWeight(
                        number.getWeight() / probabilities.specialNumberProbability[number.getValue()]);
            }
        }
    }

    private Probability calculateDuplicateProbability(List<HistoryItem> lts,
                                                      LotteryConfiguration lotteryConfiguration) {
        Probability result = new Probability();
        int[] normalNumberWasSameRecord = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getNormalRange() + 1);
        int[] specialNumberWasSameRecord = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getSpecialRange() + 1);
        int[] normalSameCountRecord = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getNormalSize() + 1);
        int[] specialSameCountRecord = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getSpecialSize() + 1);
        for (int i = 1; i < lts.size() - 1; i++) {
            HistoryItem record = lts.get(i);
            HistoryItem recordOther = lts.get(i + 1);
            RewardRule.RewardDetail detail = record.calculateRewardDetail(recordOther);
            final NumberList normalSames = detail.getNormals();
            final NumberList specialSames = detail.getSpecials();
            for (int j = 0; j < normalSames.size(); j++) {
                normalNumberWasSameRecord[normalSames.get(j)]++;
            }
            normalSameCountRecord[normalSames.size()]++;
            for (int j = 0; j < specialSames.size(); j++) {
                specialNumberWasSameRecord[specialSames.get(j)]++;
            }
            specialSameCountRecord[specialSames.size()]++;
        }
        result.normalNumberProbability = NumUtils.calculateProbability(normalNumberWasSameRecord);
        result.specialNumberProbability = NumUtils.calculateProbability(specialNumberWasSameRecord);
        result.normalSameCountProb = NumUtils.calculateProbability(normalSameCountRecord);
        result.specialSameCountProb = NumUtils.calculateProbability(specialSameCountRecord);

        LotteryRecord record = lts.get(0);
        final NumberList normals = record.getNormals();
        for (int i = 0; i < result.normalNumberProbability.length; i++) {
            if (!normals.contains(i)) {
                result.normalNumberProbability[i] = 0;
            }
        }
        float totalRate = 0;
        for (int i = 0; i < normals.size(); i++) {
            totalRate += result.normalNumberProbability[normals.get(i)];
        }
        final float[] normalizedProbability = NumUtils.getNormalizedProbability(
                lotteryConfiguration.getType());
        for (int i = 0; i < normals.size(); i++) {
            result.normalNumberProbability[normals.get(
                    i)] = result.normalNumberProbability[normals.get(
                    i)] / totalRate / normalizedProbability[0];
        }
        totalRate = 0;
        final NumberList specials = record.getSpecials();
        for (int i = 0; i < result.specialNumberProbability.length; i++) {
            if (!specials.contains(i)) {
                result.specialNumberProbability[i] = 0;
            }
        }
        for (int i = 0; i < specials.size(); i++) {
            totalRate += result.specialNumberProbability[specials.get(i)];
        }
        for (int i = 0; i < specials.size(); i++) {
            result.specialNumberProbability[specials.get(
                    i)] = result.specialNumberProbability[specials.get(
                    i)] / totalRate / normalizedProbability[1];
        }
        return result;
    }

    private static class Probability {
        float[] normalNumberProbability;
        float[] specialNumberProbability;
        float[] normalSameCountProb;
        float[] specialSameCountProb;
    }

}
