package com.tensynchina.hook.wechat;

import android.database.Cursor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.llx278.uimocker2.ISolo;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by llx on 2018/3/23.
 */

public class Task7 extends BaseTask {

    private static final String sql = "select bizinfo.brandIconURL" +
            ", bizinfo.type" +
            ", bizinfo.status" +
            ", rcontact.username" +
            ", rcontact.conRemark" +
            ", rcontact.nickname" +
            ", rcontact.alias" +
            ", rcontact.conRemarkPYFull" +
            ", rcontact.conRemarkPYShort" +
            ", rcontact.showHead" +
            ", rcontact.pyInitial" +
            ", rcontact.quanPin" +
            " from rcontact, bizinfo" +
            " where rcontact.username" + " = bizinfo.username" +
            " and (rcontact.verifyFlag" + " & " + 8 + ") != 0 " +
            " and (rcontact.type" + " & 1) != 0 " +
            " order by showHead asc, " +
            " case when length(conRemarkPYFull) > 0 then upper(conRemarkPYFull) " +
            " else upper(quanPin) end asc, " +
            " case when length(conRemark) > 0 then upper(conRemark) " +
            " else upper(quanPin) end asc, " +
            " upper(quanPin) asc, " +
            " upper(nickname) asc";

    @Override
    public Result execute(ISolo solo, Param param) {
        XLogger.d("进入task7");
        Result result = new Result();
        result.setPackageName(param.getPackageName());
        result.setTaskId(param.getTaskId());
        result.setTaskTag(param.getTaskTag());
        result.setUuid(param.getAddressUuid());

        try {
            List<String> nicknameList = new ArrayList<>();
            Cursor cursor = WXDatabase.rawQuery(sql,null);
            if (cursor != null) {
                XLogger.d("公众号表的名字大小：" + cursor.getCount() + "");
                while (cursor.moveToNext()) {
                    String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                    XLogger.d("nickname = " + nickname);
                    if (!TextUtils.isEmpty(nickname)) {
                        nicknameList.add(nickname);
                    }
                }

            }
            String data = JSON.toJSONString(nicknameList);
            result.setData(data);
            Thread.sleep(2000);
            return result;
        } catch (Exception e) {
            XLogger.e(e);
            result.setError(new Error(Error.ACCESS_DB_ERROR,"进入微信的数据库失败"));
            return result;
        } finally {
            solo.getActivityUtils().finishOpenedActivities();
        }
    }
}
