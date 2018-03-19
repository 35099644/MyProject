package com.tensynchina.hook.task;

import java.util.ArrayList;

/**
 * 对拿到的任务进行分类。
 * 目前的任务可以分为操作ui的和非操作ui的，操作ui的操作无法并发执行，只能以任务队列的形式一个接一个的执行。
 * 而非操作ui的由于大部分是读写数据库之类，操作速度比较快，不能与操作ui的操作放入一个队列中，需要单独管理
 * Created by llx on 2018/3/16.
 */

class Category {

    private static final ArrayList<BasePackageTask> sPackageTaskList;
    static {
        sPackageTaskList = new ArrayList<>();
        sPackageTaskList.add(new WXTask());
    }

    private Category() {
    }

    static boolean isUITask(Task task) {
        Param param = task.getParam();
        BasePackageTask packageTask = createPackageTask(param);
        return packageTask != null && packageTask.isUIBlock(param.getTaskTag());
    }

    static boolean isNoUITask(Task task) {
        Param param = task.getParam();
        BasePackageTask packageTask = createPackageTask(param);
        return packageTask != null && packageTask.isNoUIBlock(param.getTaskTag());
    }



    private static BasePackageTask createPackageTask(Param param) {

        for (BasePackageTask packageTask : sPackageTaskList) {
            if (param.getPackageName().equals(packageTask.getPackageName())) {
                return packageTask;
            }
        }
        return null;
    }

    private static abstract class BasePackageTask {
        abstract String getPackageName();
        abstract boolean isUIBlock(int taskTag);
        abstract boolean isNoUIBlock(int taskTag);
    }

    private static class WXTask extends BasePackageTask {
        private ArrayList<Integer> mNoBlockList = new ArrayList<>();
        private ArrayList<Integer> mBlockList = new ArrayList<>();

        WXTask() {
            // 添加非UI任务
            // 查询已经关注的公众号
            mNoBlockList.add(7);

            // 添加UI任务
            // 搜索关注公众号
            mBlockList.add(1);
            // 搜索文章
            mBlockList.add(2);
            // 搜索一个公众号的历史文章
            mBlockList.add(3);
            // 抓取一个公众号文章的内容
            mBlockList.add(4);
            // 取消关注指定公众号
            mBlockList.add(8);
            // 通过一个url关注公众号
            mBlockList.add(10);

        }

        @Override
        String getPackageName() {
            return "com.tencent.mm";
        }

        @Override
        boolean isUIBlock(int taskTag) {
            return mBlockList.contains(taskTag);
        }

        @Override
        boolean isNoUIBlock(int taskTag) {
            return mNoBlockList.contains(taskTag);
        }
    }
}
