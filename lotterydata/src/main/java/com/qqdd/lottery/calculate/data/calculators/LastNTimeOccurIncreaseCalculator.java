package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.utils.NumUtils;

import java.util.HashMap;
import java.util.List;

public class LastNTimeOccurIncreaseCalculator extends CalculatorImpl {

    public LastNTimeOccurIncreaseCalculator(String title, String desc) {
        super(title, desc);
    }

    private static final int N = 3;
    private static final float POWER = 1f;

    private static HashMap<LotteryRecord, Probability> CACHE = new HashMap<>();

    @Override
    public void calculate(List<LotteryRecord> lts, NumberTable normalTable,
                          NumberTable specialTable) {
        final LotteryRecord record = lts.get(0);
        Probability cache = CACHE.get(record);
        if (cache == null) {
            cache = calculateProbability(lts);
            CACHE.put(record, cache);
        }
        final float[] normalProbability = cache.normalProbability;
        for (int i = 1; i < normalProbability.length; i++) {
            final com.qqdd.lottery.data.Number withNumber = normalTable.getWithNumber(i);
            withNumber.setWeight(withNumber.getWeight() * normalProbability[i] * POWER);
        }
        final float[] specialProbability = cache.specialProbability;
        for (int i = 1; i < specialProbability.length; i++) {
            final com.qqdd.lottery.data.Number withNumber = specialTable.getWithNumber(i);
            withNumber.setWeight(withNumber.getWeight() * specialProbability[i] * POWER);
        }
    }

    private Probability calculateProbability(List<LotteryRecord> lts) {
        final Probability result = new Probability();
        float[] normalOccurrence = NumUtils.newEmptyFloatArray(lts.get(0)
                .getLottery()
                .getLotteryConfiguration()
                .getNormalRange() + 1);
        float[] specialOccurrence = NumUtils.newEmptyFloatArray(lts.get(0)
                .getLottery()
                .getLotteryConfiguration()
                .getSpecialRange() + 1);
        for (int i = 0; i < N; i++) {
            final NumberList normalList = lts.get(i)
                    .getNormals();
            final NumberList specialList = lts.get(i)
                    .getSpecials();
            for (int j = 0; j < normalList.size(); j++) {
                normalOccurrence[normalList.get(j)]++;
            }
            for (int j = 0; j < specialList.size(); j++) {
                specialOccurrence[specialList.get(j)]++;
            }
        }
        result.normalProbability = normalOccurrence;
        result.specialProbability = specialOccurrence;
        return result;
    }

    private static class Probability {
        float[] normalProbability;
        float[] specialProbability;
    }


}
