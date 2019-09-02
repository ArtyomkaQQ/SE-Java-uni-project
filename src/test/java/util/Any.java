package util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

public class Any {

    public static String alphabetic(int min, int max){
        int i = max - min;
        return RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(i, max + 1));
    }

    public static Long randomId(){
        return Math.abs(RandomUtils.nextLong());
    }
    public static String randomName(){
        return alphabetic(2,18);
    }

    public static int randomInt(){
        return Math.abs(RandomUtils.nextInt(2, 100));
    }

    public static double randomDouble(){
        return Math.abs(Math.round(RandomUtils.nextDouble(0, 75) * 100.0) / 100.0);
    }



}
