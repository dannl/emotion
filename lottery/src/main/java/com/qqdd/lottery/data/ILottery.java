package com.qqdd.lottery.data;

import java.util.Set;

/**
 * Created by danliu on 1/19/16.
 */
public interface ILottery {

    public Set<Integer> getNormals();

    public Set<Integer> getSpecials();

    Lottery.Type getType();

}
