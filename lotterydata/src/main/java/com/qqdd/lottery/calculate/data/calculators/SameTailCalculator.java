package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.calculate.data.NumberPicker;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.LotteryConfiguration;
import com.qqdd.lottery.data.LotteryRecord;
import com.qqdd.lottery.data.NumberList;
import com.qqdd.lottery.data.NumberTable;
import com.qqdd.lottery.utils.NumUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by danliu on 2016/1/26.
 */
public class SameTailCalculator extends CalculatorImpl implements NumberPicker {

    public SameTailCalculator(String title, String desc) {
        super(title, desc);
    }

    private static HashMap<LotteryRecord, Probabilities> CACHE = new HashMap<>();
    private static final Random RANDOM = new Random();
    private static final float POWER = 2;

    @Override
    public void pick(List<LotteryRecord> history, Lottery picked) {
//        if (picked.getNormals().size() == 0 || picked.getSpecials().size() == 0) {
//            return;
//        }
//        final LotteryRecord record = history.get(0);
//        final LotteryConfiguration configuration = record.getLottery().getLotteryConfiguration();
//        Probabilities cache = CACHE.get(record);
//        if (cache == null) {
//            cache = calculateProbability(history);
//            CACHE.put(record, cache);
//        }
    }

    @Override
    public void calculate(List<LotteryRecord> lts, NumberTable normalTable, NumberTable specialTable) {
        final LotteryRecord record = lts.get(0);
        final LotteryConfiguration configuration = record.getLottery().getLotteryConfiguration();
        Probabilities cache = CACHE.get(record);
        if (cache == null) {
            cache = calculateProbability(lts);
            CACHE.put(record, cache);
        }
        int normalOccTime = NumUtils.calculateIndexWithWeight(cache.normalOccTimeRate, RANDOM);
        int specialOccTime = NumUtils.calculateIndexWithWeight(cache.specialOccTimeRate, RANDOM);
        for (int i = 0; i < normalOccTime; i++) {
            int tailToAddRate = NumUtils.calculateIndexWithWeight(cache.normalNumberOccProb, RANDOM);
            int range = configuration.getNormalRange();
            for (int j = tailToAddRate; j <= range; j+= 10) {
                if (j == 0) {
                    continue;
                }
                normalTable.getWithNumber(j).setWeight(normalTable.getWithNumber(j).getWeight() * POWER);
            }
        }
        for (int i = 0; i < specialOccTime; i++) {
            int tailToAddRate = NumUtils.calculateIndexWithWeight(cache.specialNumberOccProb, RANDOM);
            int range = configuration.getSpecialRange();
            for (int j = tailToAddRate; j <= range; j+= 10) {
                if (j == 0) {
                    continue;
                }
                specialTable.getWithNumber(j).setWeight(specialTable.getWithNumber(j).getWeight() * POWER);
            }
        }
    }

    private Probabilities calculateProbability(List<LotteryRecord> lts) {
        Probabilities result = new Probabilities();
        LotteryConfiguration configuration = lts.get(0).getLottery().getLotteryConfiguration();
        result.normalOccTimeRate = NumUtils.newEmptyFloatArray(configuration.getNormalSize() / 2 + 1);
        result.specialOccTimeRate = NumUtils.newEmptyFloatArray(configuration.getSpecialSize() / 2 + 1);
        int[] normalSameCount = NumUtils.newEmptyIntArray(configuration.getNormalSize() / 2 + 1);
        int[] specialSameCount = NumUtils.newEmptyIntArray(configuration.getSpecialSize() / 2 + 1);
        int[] normalNumberOccCount = NumUtils.newEmptyIntArray(10);
        int[] specialNumberOccCount = NumUtils.newEmptyIntArray(10);
        for (int i = 0; i < lts.size(); i++) {
            LotteryRecord record = lts.get(i);
            NumberList normals = record.getNormals();
            NumberList specials = record.getSpecials();
            //TODO 是否需要一起计算?
            int sameTailCount = 0;
            NumberList recorded = new NumberList(normalSameCount.length);
            for (int j = 0; j < normals.size() - 1; j++) {
                int number = normals.get(j);
                if (recorded.contains(number % 10)) {
                    continue;
                }
                for (int k = j + 1; k < normals.size(); k++) {
                    int numberDest = normals.get(k);
                    if (number % 10 == numberDest % 10) {
                        final int tail = number % 10;
                        recorded.add(tail);
                        sameTailCount ++;
                        normalNumberOccCount[tail] ++;
                        break;
                    }
                }
            }
            normalSameCount[sameTailCount] ++;

            sameTailCount = 0;
            recorded.clear();
            for (int j = 0; j < specials.size() - 1; j++) {
                int number = specials.get(j);
                if (recorded.contains(number % 10)) {
                    continue;
                }
                for (int k = j + 1; k < specials.size(); k++) {
                    int numberDest = specials.get(k);
                    if (number % 10 == numberDest % 10) {
                        final int tail = number % 10;
                        recorded.add(tail);
                        sameTailCount ++;
                        specialNumberOccCount[tail] ++;
                        break;
                    }
                }
            }
            specialSameCount[sameTailCount] ++;
        }
        //出现次数的概率
        result.normalOccTimeRate = NumUtils.calculateProbability(normalSameCount);
        result.specialOccTimeRate = NumUtils.calculateProbability(specialSameCount);
        //具体到每个数字出现的概率
        result.normalNumberOccProb = NumUtils.calculateProbability(normalNumberOccCount);
        result.specialNumberOccProb = NumUtils.calculateProbability(specialNumberOccCount);
        return result;
    }

    private static class Probabilities {
        float[] normalOccTimeRate;
        float[] specialOccTimeRate;
        float[] normalNumberOccProb = new float[10];
        float[] specialNumberOccProb = new float[10];
    }


}
