package com.llx278.utils;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 处理按两次返回退出
 * Created by llx on 16-1-25.
 */
public class HandleKeyBack {

    private static HandleKeyBack mInstance;
    private final static int SHIELD_BACK = 500;  // 屏蔽back键的事件
    private final static int DELAY_BACK = 5000;  // 重置时间

    private List<Long> mBackTime = new ArrayList<>();
    private boolean mIsFirstPressed = true;

    private HandleKeyBack() {

    }

    public static HandleKeyBack getInstance() {
        if (mInstance == null) {
            mInstance = new HandleKeyBack();
        }
        return mInstance;
    }

    public boolean onKeyBack(int keyCode, KeyEvent event, Context context) {

        if(keyCode == KeyEvent.KEYCODE_BACK){  // 按下返回键

            /*boolean b = FragmentFirstHandlerBackKeyCode();
            if (b) {
                return true;
            }*/
            long currentTime = System.currentTimeMillis();
            if(mIsFirstPressed){
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // 重置所有事件
                        mIsFirstPressed = true;
                        mBackTime.removeAll(mBackTime);
                    }
                }, DELAY_BACK);
                mBackTime.add(currentTime);
                mIsFirstPressed = false;
                Toast.makeText(context, "再按一次退出", Toast.LENGTH_SHORT).show();
                return true;
            }
            mBackTime.add(currentTime);
            if(mBackTime.size() >= 1){  // 其他情况
                long delayTime = mBackTime.get(mBackTime.size()-1) - mBackTime.get(0);
                if( delayTime<= SHIELD_BACK){  // 500ms内重复按下
                    return true;
                }
                if(delayTime > SHIELD_BACK || delayTime <= DELAY_BACK ){  // 真正退出
                    return false;
                }
            }
        }
        return false;
    }

}
