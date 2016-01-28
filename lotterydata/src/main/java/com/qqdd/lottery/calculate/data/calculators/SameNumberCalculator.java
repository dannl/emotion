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

/**
 * Created by danliu on 1/25/16.
 */
public class SameNumberCalculator extends CalculatorImpl {

    private static final HashMap<Integer, Probability> PROBABILITY_CACHE = new HashMap<>();

    public SameNumberCalculator(String title, String desc) {
        super(title, desc);
    }

    @Override
    public void calculate(List<LotteryRecord> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        final LotteryRecord record = lts.get(0);
        Probability probabilities = PROBABILITY_CACHE.get(record.hashCode());
        if (probabilities == null) {
            probabilities = calculateDuplicateProbability(lts, record.getLottery()
                    .getConfiguration());
            PROBABILITY_CACHE.put(record.hashCode(), probabilities);
        }

        final NumberList recordNormals = record.getNormals();

        final NumberList recordSpecials = record.getSpecials();

        for (int i = 0; i < recordNormals.size(); i++) {
            final Number number = normalTable.getWithNumber(recordNormals.get(i));
            number.setWeight(
                    number.getWeight() * probabilities.normalProbability[number.getValue()]);
        }

        for (int i = 0; i < recordSpecials.size(); i++) {
            final Number number = specialTable.getWithNumber(recordSpecials.get(i));
            number.setWeight(
                    number.getWeight() * probabilities.specialProbability[number.getValue()]);
        }
    }

    private Probability calculateDuplicateProbability(List<LotteryRecord> lts,
                                                      LotteryConfiguration lotteryConfiguration) {
        Probability result = new Probability();
        int[] normalSameCountRecord = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getNormalRange() + 1);
        int[] specialSameCountRecord = NumUtils.newEmptyIntArray(
                lotteryConfiguration.getSpecialRange() + 1);
        int normalHasSame = 0;
        int specialHasSame = 0;
        for (int i = 1; i < lts.size() - 1; i++) {
            //                        final NumberList normalList = lts.get(i).getNormals();
            //                        final NumberList specialList = lts.get(i).getSpecials();
            //                        final NumberList lastNormalList = lts.get(i + 1).getNormals();
            //                        final NumberList lastSpecialList = lts.get(i + 1).getSpecials();
            //                        final int normalSame = NumUtils.calculateSameCount(normalList, lastNormalList);
            //                        final int specialSame = NumUtils.calculateSameCount(specialList, lastSpecialList);
            LotteryRecord record = lts.get(i);
            LotteryRecord recordOther = lts.get(i + 1);
            RewardRule.RewardDetail detail = record.calculateRewardDetail(recordOther);
            final NumberList normalSames = detail.getNormals();
            final NumberList specialSames = detail.getSpecials();
            for (int j = 0; j < normalSames.size(); j++) {
                normalSameCountRecord[normalSames.get(j)]++;
            }
            for (int j = 0; j < specialSames.size(); j++) {
                specialSameCountRecord[specialSames.get(j)]++;
            }
            normalHasSame += normalSames.size() > 0 ?
                    1 :
                    0;
            specialHasSame += specialSames.size() > 0 ?
                    1 :
                    0;
        }
        result.normalProbability = NumUtils.calculateProbability(normalSameCountRecord);
        result.specialProbability = NumUtils.calculateProbability(specialSameCountRecord);
        LotteryRecord record = lts.get(0);
        float normalHasSameRate = ((float) normalHasSame) / lts.size();
        float specialHasSameRate = ((float) specialHasSame) / lts.size();
        final NumberList normals = record.getNormals();
        float totalRate = 0;
        for (int i = 0; i < normals.size(); i++) {
            totalRate += result.normalProbability[normals.get(i)];
        }
        for (int i = 0; i < normals.size(); i++) {
            result.normalProbability[normals.get(i)] = 1 + normalHasSameRate * result.normalProbability[normals.get(i)] / totalRate;
        }
        totalRate = 0;
        final NumberList specials = record.getSpecials();
        for (int i = 0; i < specials.size(); i++) {
            totalRate += result.specialProbability[specials.get(i)];
        }
        for (int i = 0; i < specials.size(); i++) {
            result.specialProbability[specials.get(i)] = 1 + specialHasSameRate * result.specialProbability[specials.get(i)] / totalRate;
        }
        return result;
    }

    private static class Probability {
        float[] normalProbability;
        float[] specialProbability;
    }

}
