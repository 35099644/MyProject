package com.llx278.utils;

import android.os.Bundle;
import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by llx on 16-4-5.
 */
public class CommonUtils {

    private static final String KEY = "common_utils_unique_key_";

    /**
     先将参数拼接到url上，这样的目的是为了可以用url作为key，此处不会对params做encode，因为HttpConnection
     会做这一部分的工作
     * @param urll
     * @param params
     * @return
     */
    public static String generateKeyUrl(String urll, Map<String, String> params) {

        String urlStr = urll;

        if (params == null || params.size() == 0) {
            return urlStr;
        }

        try {
            URL url = new URL(urlStr);
            String query = url.getQuery();
            if (!TextUtils.isEmpty(query)) {
                urlStr = urlStr + "&" + createQuery(params);
            } else {
                urlStr = urlStr + "?" + createQuery(params);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return urlStr;
    }

    private static String createQuery(Map<String, String> params) {

        Set<String> strings = params.keySet();
        StringBuffer sb = new StringBuffer();
        for (String key : strings) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }

    /**
     * 对map类型的参数进行打包,增加一个List用于存储所有参数的key
     * @param params
     */
    public static void pack(Map<String,String> params, Bundle args) {
        if (params == null || params.size() == 0 || args == null) {
            return;
        }
        String[] paramsKeys = params.keySet().toArray(new String[]{});
        args.putStringArray(KEY,paramsKeys);
        for (String key : paramsKeys) {
            args.putString(key,params.get(key));
        }
    }

    /**
     * 取出args中所有的map，类型的参数
     * @param args
     * @return
     */
    public static Map<String,String> unPack(Bundle args) {

        if (args == null ) {
            return null;
        }
        String[] keys = args.getStringArray(KEY);
        if (keys == null || keys.length == 0 ) {
            return null;
        }
        Map<String,String> params = new HashMap<>();
        for (String key : keys) {
            params.put(key,args.getString(key));
        }
        return params;
    }
}
