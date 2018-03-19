package com.tensynchina.hook.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DELL on 2016/3/9.
 */
public class DateUtils {

    /**
     * 当调用timeStampToDateStr("1252639886", "yyyy-MM-dd HH:mm:ss");
     * 返回值：2009-11-09 11:31:26
     *
     * @param timestamp Unix
     * @param formats "yyyy-MM-dd HH:mm:ss"
     * @return formats string
     */
    public static String timeStamp2DateStr(long timestamp, String formats) {
        return new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp * 1000));
    }

    public static String timeStamp2DateStr(long timestamp) {
        return timeStamp2DateStr(timestamp,"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将格式化的dateStr("yyyy-MM-dd HH:mm:ss"),返回timestamp精确到ms
     * @return Unix timestamp
     */
    public static long dateStr2TimeStamp(String str,String formats) throws ParseException {
        return new SimpleDateFormat(formats,Locale.CHINA).parse(str).getTime();
    }

    public static long dateStr2TimeStamp(String str) throws ParseException {
        return dateStr2TimeStamp(str,"yyyy-MM-dd HH:mm:ss");
    }

}
