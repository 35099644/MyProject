package com.llx278.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public final class IOUtils {
    public static String getStringFromRes(Context ctx, int resId) {
        return ctx.getResources().getString(resId);
    }

    /**
     * 把String 写入到 file目录下的fileName
     *
     * @param str
     * @param fileName
     */
    public static void stringToFile(boolean append,String str, String fileName, Context context) throws IOException {

        File f = new File(context.getCacheDir(),fileName);

        stringToFile(append,str, f.getAbsolutePath());
    }

    /**
     * 将指定的str写入到指定的文件(尚未测试是否线程安全)
     * @param str
     * @param path
     */
    public static void stringToFile(boolean append,String str,String path) throws IOException {
        if (TextUtils.isEmpty(str)) {
            return;
        }

        BufferedWriter writer = null;
        BufferedReader reader = null;

        reader = new BufferedReader(new StringReader(str));
        char[] buffer = new char[2048];

        File f = new File(path);

        if (!f.exists()) {
            f.createNewFile();
        }
        writer = new BufferedWriter(new FileWriter(f,append));
        int len;
        while ((len = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, len);
        }
        writer.close();
        reader.close();
    }

    /**
     * 把file读到String中
     *
     * @param file
     */
    public static String fileToString(String file) throws IOException {

        File f = new File(file);
        FileReader reader = new FileReader(f);
        char[] buffer = new char[1024];
        int length ;
        StringBuilder builder = new StringBuilder();
        while ( (length = reader.read(buffer)) != -1 ) {
            builder.append(buffer, 0, length);
        }
        reader.close();
        return builder.toString();
    }

    public static boolean copyAssetsToFilesystem(String assetsSrcFile, File destFile, Context ctx) {
        if (destFile.exists()) {
            return true;
        }
        InputStream istream = null;
        OutputStream ostream = null;
        try {
            istream = ctx.getAssets().open(assetsSrcFile); //从assets目录下复制
            ostream = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = istream.read(buffer)) > 0) {
                ostream.write(buffer, 0, length);
            }
            istream.close();
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (istream != null)
                    istream.close();
                if (ostream != null)
                    ostream.close();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            return false;
        }
        return true;
    }


    /**
     * 从ips中读取数据
     *
     * @param ips
     * @return
     */
    public static String readStringFromIps(InputStream ips) throws IOException {

        StringBuffer strBuilder = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(ips));
        char[] buffer = new char[1024];
        int len;
        while ((len = reader.read(buffer)) != -1) {
            strBuilder.append(buffer, 0, len);
        }
        reader.close();
        return strBuilder.toString();
    }

    /**
     * 发送错误信息到邮件
     * @param ex
     */
    public static String getErrorStr(Throwable ex){
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = info.toString();
        printWriter.close();
        return result;
    }
}
