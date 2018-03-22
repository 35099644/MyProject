package com.tensynchina.hook.wechat;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.IOUtils;
import com.tensynchina.hook.utils.XLogger;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XC_MethodHook;

/**
 *
 * Created by llx on 2018/3/22.
 */

public class WXDatabase extends XC_MethodHook {

    private static List<WeakReference<Object>> sWXDataBaseObjList = new ArrayList<>();
    private PushSender mSender;

    public WXDatabase() {
        mSender = new PushSender();
        mSender.start();
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Object dbObj = param.thisObject;
        checkDbRef(dbObj);
        String className = param.thisObject.getClass().getName();
        String expected = "com.tencent.wcdb.database.SQLiteDatabase";
        if (expected.equals(className)) {
            // 公众号消息的推送应该从这里独立出来，
            // 不应该在这里写死，但短期来看应该不会有其他的变化
            // 先这样吧。
            String methodName = param.method.getName();
            if ("insertWithOnConflict".equals(methodName)) {
                String table = (String) param.args[0];
                String nullColumnHack = (String) param.args[1];
                ContentValues contentValues = (ContentValues) param.args[2];
                int conflictAlgorithm = (int) param.args[3];
                checkIsAPushMessage(table,nullColumnHack,contentValues,conflictAlgorithm);
            }
        }
    }

    private void checkIsAPushMessage(String table, String nullColumnHack, ContentValues initiaValues,
                                     int conflictAlgorithm) {
        if (table.equals(Table.Message.tableName)) {
            String inputStr = initiaValues.getAsString(Table.Message.CONTENT);
            if (TextUtils.isEmpty(inputStr)) {
                return;
            }
            String talker = initiaValues.getAsString(Table.Message.TALKER);
            XLogger.d("currentTalker : " + talker);
            if (inputStr.startsWith("~SEMI_XML~")) {
                if (TextUtils.isEmpty(talker)) {
                    XLogger.d("message tab 中的talker列为空！");
                }
                String nickname = findNickName(getAvailableDBRef(),talker);
                List<String> urlList = findWeixinUrls(inputStr);
                if (urlList.isEmpty()) {
                    XLogger.d("没有过滤到合适的url!!!");
                    XLogger.d("此时的content: " + inputStr);
                    return;
                }
                packageTaskAndSend(urlList,nickname);
            }
        }
    }

    private void packageTaskAndSend(List<String> urlList, String nickname) {
        XLogger.d("抓取到了公众号: "+ nickname + " url : " + urlList);
        WeChatTask5 weChatTask5 = new WeChatTask5();
        weChatTask5.setUrlList(urlList);
        weChatTask5.setNickname(nickname);
        String data = JSON.toJSONString(weChatTask5);
        Result result = new Result();
        result.setPackageName("com.tencent.mm");
        result.setTaskTag(5);
        result.setTaskId("-1");
        result.setData(data);
        mSender.addPushMessage(result);
    }

    private List<String> findWeixinUrls(String inputStr) {

        String regex = "(http|https){1}://mp.weixin.qq.com/[a-zA-Z0-9&=?_]*#rd";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(inputStr);
        List<String> urlList = new ArrayList<>();
        while (m.find()) {
            String url = m.group(0);
            if (urlList.contains(url)) {
                continue;
            }
            urlList.add(url);
        }
        return urlList;
    }

    private String findNickName(Object sqliteDataBase,String username) {
        Cursor cursor = rawQuery(sqliteDataBase, "select nickname from rcontact where username = ?", new String[]{username});
        String nickname = null;
        if (cursor != null) {
            try {
                cursor.moveToNext();
                nickname = cursor.getString(0);
            } catch (Exception e) {
                XLogger.e(e);
            }
        }
        return nickname;
    }


    /**
     * 检查一下这个数据库对象的引用是不是已经存在了，如果不存在，那么久加进入
     * @param dbObj 微信的数据库对象
     */
    private void checkDbRef(Object dbObj) {

        Iterator<WeakReference<Object>> iterator = sWXDataBaseObjList.iterator();
        while (iterator.hasNext()) {
            WeakReference<Object> next = iterator.next();
            Object current = next.get();
            if (current == null) {
                iterator.remove();
            }
        }
        boolean foundEqual = false;
        for (WeakReference<Object> ref : sWXDataBaseObjList) {
            Object thisDbObj = ref.get();
            if (thisDbObj == dbObj) {
                foundEqual = true;
                break;
            }
        }
        if (!foundEqual) {
            WeakReference<Object> newObjRef = new WeakReference<>(dbObj);
            sWXDataBaseObjList.add(newObjRef);
        }
    }

    private static Object getAvailableDBRef() {

        for (WeakReference<Object> obj : sWXDataBaseObjList) {
            Object db = obj.get();
            if (db != null) {
                return db;
            }
        }
        return null;
    }

    public static Cursor rawQuery(Object sqliteDatabase,String sql, String[] selectionArgs) {
        if (sqliteDatabase == null) {
            XLogger.d("没有找到数据库连接，返回!!");
            return null;
        }
        Class<?> sqliteDataBaseClass = sqliteDatabase.getClass();
        try {
            Method query = sqliteDataBaseClass.getMethod("rawQuery",String.class,String[].class);
            return (Cursor) query.invoke(sqliteDatabase, sql, selectionArgs);
        } catch (Exception e) {
            XLogger.e(e);
            return null;
        }
    }

    public static int delete(Object sqliteDatabase,String table,String whereClause,String[] whereArgs) {
        if (sqliteDatabase == null) {
            XLogger.d("没有找到数据库连接，返回!");
            return -1;
        }
        Class<?> sqliteDataBaseClass = sqliteDatabase.getClass();
        try {
            Method query = sqliteDataBaseClass.getMethod("delete",String.class,String.class,String[].class);
            return (int) query.invoke(sqliteDatabase, table, whereClause,whereArgs);
        } catch (Exception e) {
            XLogger.e(e);
            return -1;
        }
    }

    public static void execSQL (Object sqliteDatabase,String sql) {
        if (sqliteDatabase == null) {
            XLogger.d("没有找到数据库连接，返回!!");
            return;
        }
        Class<?> sqliteDataBaseClass = sqliteDatabase.getClass();
        try {
            Method execSQL = sqliteDataBaseClass.getMethod("execSQL",String.class);
            execSQL.invoke(sqliteDatabase,sql);
        } catch (Exception e) {
            XLogger.e(e);
        }
    }

    public static long insert (Object sqliteDatabase,String table,String nullColumnHack,ContentValues values) {
        if (sqliteDatabase == null) {
            XLogger.d("没有找到数据库连接，返回!");
            return -1;
        }
        Class<?> sqliteDatabaseClass = sqliteDatabase.getClass();
        try {
            Method insert = sqliteDatabaseClass.getMethod("insert", String.class, String.class, ContentValues.class);
            return (long) insert.invoke(sqliteDatabase,table,nullColumnHack,values);
        } catch (Exception e) {
            XLogger.e(e);
            return -1;
        }
    }

    public static int update (Object sqliteDatabase,String table,
                              ContentValues values,
                              String whereClause,
                              String[] whereArgs) {
        if (sqliteDatabase == null) {
            XLogger.d("没有找到数据库连接，返回!!");
            return -1;
        }
        Class<?> sqliteDataBaseClass = sqliteDatabase.getClass();
        try {
            Method execSQL = sqliteDataBaseClass.getMethod("update",String.class,ContentValues.class,String.class,String[].class);
            return (int) execSQL.invoke(sqliteDatabase,table,values,whereClause,whereArgs);
        } catch (Exception e) {
            XLogger.e(e);
            return -1;
        }
    }

    public static Cursor query(Object sqliteDatabase,String table,
                               String[] columns,
                               String selection,
                               String[] selectionArgs,
                               String groupBy,
                               String having,
                               String orderBy,
                               String limit) {
        if (sqliteDatabase == null) {
            XLogger.d("没有找到数据库连接，返回!!");
            return null;
        }
        Class<?> sqliteDataBaseClass = sqliteDatabase.getClass();
        try {
            Method query = sqliteDataBaseClass.getMethod("query",
                    String.class,
                    String[].class,
                    String.class,
                    String[].class,
                    String.class,
                    String.class,
                    String.class,
                    String.class);
            return (Cursor) query.invoke(sqliteDatabase, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        } catch (Exception e) {
            XLogger.e(e);
            return null;
        }
    }
}
