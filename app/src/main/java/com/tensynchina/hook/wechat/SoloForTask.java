package com.tensynchina.hook.wechat;

import android.widget.EditText;

import com.llx278.uimocker2.ActivityUtils;
import com.llx278.uimocker2.Clicker;
import com.llx278.uimocker2.DialogUtils;
import com.llx278.uimocker2.Gesture;
import com.llx278.uimocker2.ISolo;
import com.llx278.uimocker2.Scroller;
import com.llx278.uimocker2.Searcher;
import com.llx278.uimocker2.ViewGetter;
import com.llx278.uimocker2.Waiter;
import com.llx278.uimocker2.WebUtils;
import com.tensynchina.hook.SoloDecorator;

/**
 * 对task1的solo执行进行代理,目的就是对一些可能出现的异常状态
 * 进行处理
 * Created by llx on 2018/3/20.
 */

public class SoloForTask extends SoloDecorator {

    SoloForTask(ISolo solo) {
        super(solo);
    }

    @Override
    public Clicker getClicker() {
        doSomeWork();
        return super.getClicker();
    }

    @Override
    public Scroller getScroller() {
        doSomeWork();
        return super.getScroller();
    }

    @Override
    public Searcher getSearcher() {
        doSomeWork();
        return super.getSearcher();
    }

    @Override
    public ViewGetter getViewGetter() {
        doSomeWork();
        return super.getViewGetter();
    }

    @Override
    public Waiter getWaiter() {
        doSomeWork();
        return super.getWaiter();
    }

    @Override
    public Gesture getGesture() {
        doSomeWork();
        return super.getGesture();
    }

    @Override
    public ActivityUtils getActivityUtils() {
        doSomeWork();
        return super.getActivityUtils();
    }

    @Override
    public DialogUtils getDialogUtils() {
        doSomeWork();
        return super.getDialogUtils();
    }

    @Override
    public WebUtils getWebUtils() {
        doSomeWork();
        return super.getWebUtils();
    }

    @Override
    public void mockSoftKeyBordSearchButton(EditText editText) throws Exception {
        doSomeWork();
        super.mockSoftKeyBordSearchButton(editText);
    }

    @Override
    public boolean waitForTextAndClick(String regex) {
        doSomeWork();
        return super.waitForTextAndClick(regex);
    }

    /**
     * 在Solo执行所有的动作之前对当前的页面进行
     * 一些检查，所有新建的事情
     */
    private void doSomeWork() {
        //XLogger.d("doSomeWork!");
    }
}
