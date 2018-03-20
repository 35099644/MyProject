package com.tensynchina.hook;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.llx278.uimocker2.ActivityUtils;
import com.llx278.uimocker2.Clicker;
import com.llx278.uimocker2.DialogUtils;
import com.llx278.uimocker2.Gesture;
import com.llx278.uimocker2.ISolo;
import com.llx278.uimocker2.Scroller;
import com.llx278.uimocker2.Searcher;
import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.ViewGetter;
import com.llx278.uimocker2.Waiter;
import com.llx278.uimocker2.WebUtils;

/**
 *对{@link Solo}的一个装饰类
 * Created by llx on 2018/3/20.
 */

public class SoloDecorator implements ISolo {

    private ISolo mSolo;


    public SoloDecorator(ISolo solo) {
        mSolo = solo;
    }

    @Override
    public Clicker getClicker() {
        return mSolo.getClicker();
    }

    @Override
    public Scroller getScroller() {
        return mSolo.getScroller();
    }

    @Override
    public Searcher getSearcher() {
        return mSolo.getSearcher();
    }

    @Override
    public ViewGetter getViewGetter() {
        return mSolo.getViewGetter();
    }

    @Override
    public Waiter getWaiter() {
        return mSolo.getWaiter();
    }

    @Override
    public Gesture getGesture() {
        return mSolo.getGesture();
    }

    @Override
    public ActivityUtils getActivityUtils() {
        return mSolo.getActivityUtils();
    }

    @Override
    public DialogUtils getDialogUtils() {
        return mSolo.getDialogUtils();
    }

    @Override
    public WebUtils getWebUtils() {
        return mSolo.getWebUtils();
    }

    @Override
    public View findViewById(int id) {
        return mSolo.findViewById(id);
    }

    @Override
    public View findViewById(int id, long timeout) {
        return mSolo.findViewById(id, timeout);
    }

    @Override
    public View findViewById(int id, View parent) {
        return mSolo.findViewById(id, parent);
    }

    @Override
    public View findViewById(int id, View parent, long timeout) {
        return mSolo.findViewById(id, parent, timeout);
    }

    @Override
    public void mockSoftKeyBordSearchButton(EditText editText) throws Exception {
        mSolo.mockSoftKeyBordSearchButton(editText);
    }

    @Override
    public Context getContext() {
        return mSolo.getContext();
    }

    @Override
    public void runOnMainSync(Runnable runnable) {
        mSolo.runOnMainSync(runnable);
    }

    @Override
    public void sleep(long time) {
        mSolo.sleep(time);
    }

    @Override
    public void littleSleep() {
        mSolo.littleSleep();
    }

    @Override
    public void littleSleep(int multiple) {
        mSolo.littleSleep(multiple);
    }

    @Override
    public boolean waitForTextAndClick(String regex) {
        return mSolo.waitForTextAndClick(regex);
    }
}
