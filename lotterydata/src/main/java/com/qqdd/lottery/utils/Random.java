package com.qqdd.lottery.utils;

import java.security.SecureRandom;

/**
 * Created by danliu on 2/2/16.
 */
public class Random extends SecureRandom {

    private static final String SEED_URL = "https://www.random.org/cgi-bin/randbyte?nbytes=20&format=h";

    private static class SingletonHolder {
        private static final Random INSTANCE = new Random();
    }

    public static Random getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * call this method before using this class. optional.
     */
    public void init() {
        final String seed = SimpleHttpGetter.request(SEED_URL);
        try {
            if (seed != null) {
                final String[] split = seed.split("\\s+");
                byte[] seedBytes = new byte[split.length];
                for (int i = 0; i < split.length; i++) {
                    System.out.println(String.valueOf(i));
                    System.out.println(split[i]);
                    final int parsedInt = Integer.parseInt(split[i], 16);
                    final int byteValue = parsedInt & 0x7f;

                    seedBytes[i] = (byte) (parsedInt > 0x7f ?
                            -byteValue :
                            byteValue);
                }
                setSeed(seedBytes);
            }
        } catch (Exception ignored) {
            //ignore this exception.
        }
    }

    private Random() {
        super();
    }

}
