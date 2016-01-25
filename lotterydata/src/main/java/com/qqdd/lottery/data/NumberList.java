package com.qqdd.lottery.data;

import java.util.ArrayList;

/**
 * Created by danliu on 1/25/16.
 */
public class NumberList extends ArrayList<Integer> {

    public NumberList(int normalSameCount) {
        super(normalSameCount);
    }

    public NumberList() {
        super();
    }

    @Override
    public boolean add(Integer object) {
        if (object == null) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (get(i) == object.intValue()) {
                return false;
            }
        }
        return super.add(object);
    }
}
