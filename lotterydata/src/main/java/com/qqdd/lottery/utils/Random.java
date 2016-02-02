package com.qqdd.lottery.utils;

import java.security.SecureRandom;

/**
 * Created by danliu on 2/2/16.
 */
public class Random extends SecureRandom {

    private static class SingletonHolder {
        private static final Random INSTANCE = new Random();
    }

    public static Random getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Random() {
        super();
    }

}
