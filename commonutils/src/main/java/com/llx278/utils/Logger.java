package com.llx278.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志输出
 * Created by llx on 16-1-15.
 */
public class Logger {

    private static final String DEFAULT_LOGGER_FILE_NAME = "logger";
    private static final String DEFAULT_LOGGER_DIR_NAME= ".llx278";

    /**最大的cache大小*/
    private static final long MAX_OF_LOG_FILE_SIZE = 1024*1024*2;
    /**
     * 写入文件
     */
    private static final int WRITE_TO_FILE = 0;
    private static final int CHECK_FILE_SIZE = 1;
    private static final int VERBOSE = 1;
    private static final int INFO = 2;
    private static final int WARING = 3;
    private static final int DEBUG = 4;
    private static final int ERROR = 5;

    private static boolean mToggle = false;
    private static final String mDefaultTag = "ee";
    private static boolean mWriteToFile = false;
    private static LoggerHandler mHandler;
    private static String mFileDir;
    private static String mLogName = DEFAULT_LOGGER_FILE_NAME;

    /**
     * @param f true 写入本地文件 false 不写入
     * @Param t true 开启log false 关闭log
     * @Param fileDir file目录的位置
     */
    public static void init(boolean f, boolean t, String fileDir) {
        mWriteToFile = f;
        mToggle = t;
        mFileDir = fileDir;
        initWriteFileThread();
    }

    public static void init(boolean f, boolean t, String fileDir,String logName) {
        init(f, t, fileDir);
        mLogName = logName;
    }

    private static void initWriteFileThread() {
        if (!mToggle || !mWriteToFile) {
            return;
        }

        HandlerThread handlerThread = new HandlerThread("LoggerThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        mHandler = new LoggerHandler(looper);
    }

    /**
     * verbose
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        addToLog(VERBOSE, tag, msg);
    }

    public static void v(String msg) {
        v(mDefaultTag, msg);
    }

    /**
     * info
     */
    public static void i(String tag, String msg) {
        addToLog(INFO, tag, msg);
    }

    public static void i(String msg) {
        i(mDefaultTag, msg);
    }

    public static void w(String tag, String msg) {
        addToLog(WARING, tag, msg);
    }

    public static void w(String msg) {
        w(mDefaultTag, msg);
    }

    /**
     * debug
     */
    public static void d(String tag, String msg) {
        addToLog(DEBUG, tag, msg);
    }

    public static void d(String msg) {
        d(mDefaultTag, msg);
    }

    /**
     * error
     */
    public static void e(String tag, String msg) {
        addToLog(ERROR, tag, msg);
    }

    public static void e(String msg) {
        e(mDefaultTag, msg);
    }

    public static void e(Exception e) {
        e(mDefaultTag,Log.getStackTraceString(e));
    }
    public static void e(String tag,Exception e) {
        e(tag,Log.getStackTraceString(e));
    }

    /**
     * 清除log的缓存
     */
    public static void clean() {
        sendMessage(CHECK_FILE_SIZE);
    }

    private static void addToLog(int level, String tag, String msg) {

        if (!mToggle) {
            return;
        }

        switch (level) {
            case VERBOSE:
                Log.v(tag, msg + "");
                break;
            case INFO:
                Log.i(tag, msg + "");
                break;
            case WARING:
                Log.w(tag, msg + "");
                break;
            case DEBUG:
                Log.d(tag, msg + "");
                break;
            case ERROR:
                Log.e(tag, msg + "");
                break;
        }

        saveToFile(level, tag, msg + "");
    }

    private static void saveToFile(int level, String tag, String msg) {
        if (mWriteToFile && mHandler != null) {
            sendMessage(WRITE_TO_FILE,level, tag, msg);
        }
    }

    private static void sendMessage(int what,int level, String tag, String msg) {
        Message m = Message.obtain();
        Bundle b = new Bundle();
        b.putString("tag",tag);
        b.putString("msg", msg);
        b.putString("level", levelToStr(level));
        m.what = what;
        m.setData(b);
        mHandler.sendMessage(m);
    }
    private static void sendMessage(int what) {
        sendMessage(what,0,"","");
    }

    private static String levelToStr(int level) {

        if (level == VERBOSE) {
            return "V";
        } else if (level == INFO) {
            return "I";
        } else if (level == WARING) {
            return "W";
        } else if (level == DEBUG) {
            return "D";
        } else if (level == ERROR) {
            return "E";
        }

        return null;
    }

    private static class LoggerHandler extends Handler {

        public LoggerHandler (Looper l) {
            super(l);
        }

        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case WRITE_TO_FILE:
                    startWriteToFile(msg.getData());
                    break;
                case CHECK_FILE_SIZE:
                    checkFileSize(msg.getData());
                    break;
            }
        }

        // TODO 尚未测试。。。。。
        private void checkFileSize(Bundle data) {
            try {
                File logFile = openFile();
                if (logFile.length() > MAX_OF_LOG_FILE_SIZE) {
                    logFile.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void startWriteToFile(Bundle data) {
            String tag = data.getString("tag");
            String msg = data.getString("msg");
            String levelStr = data.getString("level");

            String formatData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").format(new Date(System.currentTimeMillis()));
            StringBuilder sb = new StringBuilder();
            sb.append(formatData).append("\t").append(levelStr).
                    append("\t").append(tag).append("\t").append(msg).append("\n");
            File f = null;
            BufferedWriter bw = null;
            try {
                f = openFile();
                bw = new BufferedWriter(new FileWriter(f,true));
                bw.write(sb.toString());
            } catch (IOException e) {
                Log.e("Logger", "openFile failed  \n" + e.getMessage());
                f = null;
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException e) {
                    Log.e("Logger", "dos close failed: \n" + e.getMessage());
                }
            }
        }

        private File openFile() throws IOException {
            File loggerDir = null;
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                loggerDir = createInnerDir();
            } else {
                loggerDir = createExternalDir();
            }

            File loggerFile = new File(loggerDir, DEFAULT_LOGGER_FILE_NAME);
            if (!loggerFile.exists()) {
                loggerFile.createNewFile();
            }
            return loggerFile;
        }

        private File createExternalDir() {
            File loggerDir = new File(Environment.getExternalStorageDirectory(),DEFAULT_LOGGER_DIR_NAME);
            if (!loggerDir.exists()) {
                loggerDir.mkdirs();
            }
            return loggerDir;
        }

        private File createInnerDir() {

            File loggerDir = new File(new File(mFileDir),DEFAULT_LOGGER_DIR_NAME);
            if (!loggerDir.exists()) {
                loggerDir.mkdirs();
            }

            return loggerDir;
        }
    }
}
