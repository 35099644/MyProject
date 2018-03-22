package com.tensynchina.hook.utils;

import java.util.List;

/**
 * Created by llx on 2018/3/22.
 */

public class RegexUtils {

    /**
     * 将regexlist转换为一个正则表达式
     * @param regexList
     * @return 拼接后的正则表达式
     */
    public static String toRegexStr(List<String> regexList) {
        StringBuilder sb = new StringBuilder();
        for (String regex : regexList) {
            sb.append(regex).append("|");
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }

}
