package com.tensynchina.hook.wechat;

/**
 * Created by liu on 2017/12/21.
 */

public class Table {

    public static final class Message {

        public static final String tableName = "message";
        // 下面只列出有用的列
        public static final String MSGID = "msgId";
        public static final String MSGSVRID = "msgSvrId";
        public static final String TYPE = "type";
        public static final String STATUS = "status";
        public static final String ISSEND = "isSend";
        public static final String ISSHOWTIMER = "isShowTimer";
        public static final String CREATETIME = "createTime";
        public static final String TALKER = "talker";
        public static final String CONTENT = "content";
        public static final String IMGPATH = "imgPath";
        public static final String RESERVED = "reserved";
        public static final String LVBUFFER = "lvbuffer";
        public static final String TRANSCONTENT = "transContent";
        public static final String TRANSBRANDWORDING = "transBrandWording";
        public static final String TALKERID = "talkerId";
        public static final String BIZCLIENTMSGID = "bizClientMsgId";
        public static final String BIZCHATID = "bizChatId";
        public static final String BIZCHATUSERID = "bizChatUserId";
        public static final String MSGSEQ = "msgSeq";
        public static final String FLAG = "flag";
    }

    public static final class Rconversation {
        public static final String tableName = "rconversation";
        // 下面只列出有用的列
        public static final String MSG_TYPE = "msgType";
        public static final String CUSTOM_NOTIFY = "customNotify";
        public static final String SHOW_TIPS = "showTips";
        public static final String FLAG = "flag";
        public static final String DIGEST = "digest";
        public static final String DIGESTUSER="digestUser";
        public static final String HASTRUNC = "hasTrunc";
        public static final String PARENTREF = "parentRef";
        public static final String ATTRFLAG = "attrflag";
        public static final String EDITINGMSG ="editingMsg";
        public static final String ATCOUNT = "atCount";
        public static final String SIGHTTIME = "sightTime";
        public static final String UNREADMUTECOUNT = "unReadMuteCount";
        public static final String LASTSEQ = "lastSeq";
        public static final String UNDELIVERCOUNT = "UnDeliverCount";
        public static final String UNREADINVITE = "UnReadInvite";
        public static final String MSGCOUNT = "msgCount";
        public static final String USERNAME = "username";
        public static final String UNREADCOUNT = "unReadCount";
        public static final String CHATMODE = "chatmode";
        public static final String STATUS = "status";
        public static final String ISSEND = "isSend";
        public static final String CONVERSATIONTIME = "conversationTime";
        public static final String CONTENT = "content";
    }

}
