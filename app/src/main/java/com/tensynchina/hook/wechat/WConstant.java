package com.tensynchina.hook.wechat;

import java.util.ArrayList;

/**
 * Created by llx on 2018/3/19.
 */

public class WConstant {

    public static final ArrayList<String> NICK_NAME_LIST = new ArrayList<>();


    static final String TOOLS_TAG = "com.tencent.mm:tools_TAG";
    static final String TOOLS_REPLACE_URL = "com.tencent.mm:tools_replace_url";

    static {
        NICK_NAME_LIST.add("^少帮主$");
    }

    public static final String ACTIVITY_LAUNCH_UI = "com.tencent.mm.ui.LauncherUI";
    public static final String ACTIVITY_CONTACT_UI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
    public static final String ACTIVITY_CHATTING_UI = "com.tencent.mm.ui.chatting.ChattingUI";
    public static final String ACTIVITY_BRAND_SERVICE_INDEX_UI = "com.tencent.mm.plugin.brandservice.ui.BrandServiceIndexUI";
    public static final String ACTIVITY_BRAND_SERVICE_LOCAL_SEARCH_UI = "com.tencent.mm.plugin.brandservice.ui.BrandServiceLocalSearchUI";
}
