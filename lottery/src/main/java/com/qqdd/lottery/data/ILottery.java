package com.qqdd.lottery.data;

import com.qqdd.lottery.utils.data.NumberList;

/**
 * Created by danliu on 1/19/16.
 */
public interface ILottery {

    public NumberList getNormals();

    public NumberList getSpecials();

    Lottery.Type getType();

}
