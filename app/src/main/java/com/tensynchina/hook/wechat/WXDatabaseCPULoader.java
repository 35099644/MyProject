package com.tensynchina.hook.wechat;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Process;

import com.tensynchina.hook.utils.XLogger;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;

/**
 *
 * Created by llx on 2018/3/22.
 */

public class WXDatabaseCPULoader extends XC_MethodHook {

    private static List<WeakReference<Object>> sWXDataBaseObjList = new ArrayList<>();
    private PushSender mSender;

    public WXDatabaseCPULoader() {
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
                XLogger.d("cpuloader进程收到了插入消息!!!!!!!!111111111111111111111");
                String table = (String) param.args[0];
                String nullColumnHack = (String) param.args[1];
                ContentValues contentValues = (ContentValues) param.args[2];
                int conflictAlgorithm = (int) param.args[3];
                printParam(table,contentValues);
            }
        }
    }

    private void printParam(String table, ContentValues initiaValues) {
        XLogger.d("table : " + table + " currentProcess : " + Process.myPid());
        Set<String> strings = initiaValues.keySet();
        for (String str : strings) {
            Object o = initiaValues.get(str);
            try {
                XLogger.d("key : " + str);
                XLogger.d("value : " + o.toString());
            } catch (Exception ignore) {
            }
        }
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
