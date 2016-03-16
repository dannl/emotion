package com.qqdd.lottery.calculate.data.calculators;

import com.qqdd.lottery.calculate.data.CalculatorImpl;
import com.qqdd.lottery.data.*;
import com.qqdd.lottery.utils.NumUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by danliu on 2/26/16.
 */
public class KillDuplicatedNumberCalculator extends CalculatorImpl {

    private int mRange = 3;

    public KillDuplicatedNumberCalculator(int range) {
        super("killDuplicate");
        mRange = range;
    }

    private static final HashMap<HistoryItem, HashMap<Integer, Probability>> CACHE = new HashMap<>();

    @Override
    public void calculate(List<HistoryItem> history, NumberTable normalTable,
                          NumberTable specialTable) {
        final HistoryItem record = history.get(0);
        HashMap<Integer, Probability> map = CACHE.get(record);
        if (map == null) {
            map = new HashMap<>();
            CACHE.put(record, map);
        }
        Probability probability = map.get(mRange);
        if (probability == null) {
            probability = calculateProb(history);
            map.put(mRange, probability);
        }
        NumberList normalToUpdate = new NumberList();
        NumberList specialToUpdate = new NumberList();

        final NumberList normals = record.getNormals();
        for (
                int i = 0; i < normals.size(); i++
                ) {
            final int number = normals.get(i);
            boolean hasNumber = true;
            for (int j = 1; j < mRange; j++) {
                if (!history.get(j)
                        .getNormals()
                        .contains(number)) {
                    hasNumber = false;
                }
                if (!hasNumber) {
                    break;
                }
            }
            if (hasNumber) {
                normalToUpdate.add(number);
            }
        }

        NumberList specials = record.getSpecials();
        for (
                int i = 0; i < specials.size(); i++
                ) {
            final int number = specials.get(i);
            boolean hasNumber = true;
            for (int j = 1; j < mRange; j++) {
                if (!history.get(j)
                        .getSpecials()
                        .contains(number)) {
                    hasNumber = false;
                }
                if (!hasNumber) {
                    break;
                }
            }
            if (hasNumber) {
                specialToUpdate.add(number);
            }
        }

//        for (int i = 0; i < normalToUpdate.size(); i++) {
//            final com.qqdd.lottery.data.Number number = normalTable.getWithNumber(
//                    normalToUpdate.get(i));
//            number.setWeight(number.getWeight() * probability.normalProb);
//        }

        for (int i = 0; i < specialToUpdate.size(); i++) {
            final com.qqdd.lottery.data.Number number = specialTable.getWithNumber(
                    specialToUpdate.get(i));
            number.setWeight(number.getWeight() * probability.specialProb);
        }

    }

    public Probability calculateProb(List<HistoryItem> history) {
        int totalNormals = 0;
        int totalSpecials = 0;
        int normalOcc = 0;
        int specialOcc = 0;
        final LotteryConfiguration configuration = history.get(0)
                .getConfiguration();
        float normalizedProbNormal = (float) (NumUtils.C(configuration.getNormalRange() - 1,
                configuration.getNormalSize() - 1) / NumUtils.C(configuration.getNormalRange(),
                configuration.getNormalSize()));
        float normalizedProbSpecial = (float) (NumUtils.C(configuration.getSpecialRange() - 1,
                configuration.getSpecialSize() - 1) / NumUtils.C(configuration.getSpecialRange(),
                configuration.getSpecialSize()));
        for (int i = 0; i < history.size() - mRange; i++) {
            final NumberList normal = history.get(i)
                    .getNormals();
            final NumberList special = history.get(i)
                    .getSpecials();
            totalNormals += normal.size();
            totalSpecials += special.size();

            for (int j = 0; j < normal.size(); j++) {
                final int number = normal.get(j);
                boolean hasNumber = true;
                for (int k = 1; k < mRange; k++) {
                    hasNumber = history.get(i + k)
                            .getNormals()
                            .contains(number);
                    if (!hasNumber) {
                        break;
                    }
                }
                if (hasNumber) {
                    normalOcc++;
                    System.out.println("=======normal has " + mRange + " duplicated!");
                    for (int k = 0; k < mRange; k++) {
                        System.out.println(history.get(i + k)
                                .toString());
                    }
                }
            }
            for (int j = 0; j < special.size(); j++) {
                final int number = special.get(j);
                boolean hasNumber = true;
                for (int k = 1; k < mRange; k++) {
                    hasNumber = history.get(i + k)
                            .getSpecials()
                            .contains(number);
                    if (!hasNumber) {
                        break;
                    }
                }
                if (hasNumber) {
                    specialOcc++;
                    System.out.println("=======special has " + mRange + " duplicated!");
                    for (int k = 0; k < mRange; k++) {
                        System.out.println(history.get(i + k)
                                .toString());
                    }
                }
            }
        }
        System.out.println("normal occ: " + normalOcc + " total normals: " + totalNormals);
        System.out.println("special occ: " + specialOcc + " total special: " + totalSpecials);
        float normalProbability = ((float) normalOcc) / totalNormals;
        float specialProbability = ((float) specialOcc) / totalSpecials;
        System.out.println("real normal: " + normalProbability + " special: " + specialProbability);
        Probability result = new Probability();
        result.normalProb = normalProbability / normalizedProbNormal;
        result.specialProb = specialProbability / normalizedProbSpecial;
        System.out.println(
                " related normal : " + result.normalProb + "   special: " + result.specialProb);
        return result;
    }

    private static class Probability {
        float normalProb;
        float specialProb;

        @Override
        public String toString() {
            return " related normal : " + normalProb + "   special: " + specialProb;
        }
    }
}
