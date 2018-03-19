package com.llx278.utils;

/**
 * Created by llx on 16-5-7.
 */
public class FloatUtils {

    /**
     * 浮点型保留两位小数
     * */
    public static float formatFloat(float f) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        return Float.parseFloat(df.format(f));
    }

    public static double formatDouble(double d) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        return Double.parseDouble(df.format(d));
    }
}
