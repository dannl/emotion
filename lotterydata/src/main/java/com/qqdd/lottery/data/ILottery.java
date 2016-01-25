package com.qqdd.lottery.data;


/**
 * Created by danliu on 1/19/16.
 */
public interface ILottery {

    public NumberList getNormals();

    public NumberList getSpecials();

    Lottery.Type getType();

}
