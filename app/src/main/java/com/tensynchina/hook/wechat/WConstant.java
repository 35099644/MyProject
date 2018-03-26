package com.tensynchina.hook.wechat;

import android.annotation.SuppressLint;

import java.util.ArrayList;

/**
 * Created by llx on 2018/3/19.
 */

public class WConstant {

    static final ArrayList<String> NICK_NAME_LIST = new ArrayList<>();


    static final String TOOLS_TAG = "com.tencent.mm:tools_TAG";
    static final String TOOLS_REPLACE_URL = "com.tencent.mm:tools_replace_url";

    @SuppressLint("SdCardPath")
    static final String REPLACEABLE_URL = "/mnt/sdcard/.llx278/replaceable_url";


    static {
        NICK_NAME_LIST.add("^少帮主$");
    }

    static final String ACTIVITY_LAUNCH_UI = "com.tencent.mm.ui.LauncherUI";
    static final String ACTIVITY_CONTACT_UI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
    static final String ACTIVITY_CHATTING_UI = "com.tencent.mm.ui.chatting.ChattingUI";
    static final String ACTIVITY_BRAND_SERVICE_INDEX_UI = "com.tencent.mm.plugin.brandservice.ui.BrandServiceIndexUI";
    static final String ACTIVITY_BRAND_SERVICE_LOCAL_SEARCH_UI = "com.tencent.mm.plugin.brandservice.ui.BrandServiceLocalSearchUI";

    public static final String WX_MAIN_PROCESS = "com.tencent.mm";
    public static final String WX_TOOLS_PROCESS = "com.tencent.mm:tools";
    public static final String WX_PUSH_PROCESS = "com.tencent.mm:push";
    public static final String WX_SUPPORT_PROCESS = "com.tencent.mm:support";
    public static final String WX_LOADER_PROCESS = "com.tencent.mm:cuploader";
    public static final String WX_SAND_BOX_PROCESS = "com.tencent.mm:sandbox";


}
