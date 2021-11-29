package com.ericlam.qqbot.valbot.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.SuperChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BLiveTestService {

    private static final String TEST_IMAGE_URL = "https://cdn.pixabay.com/photo/2013/07/18/20/26/sea-164989_960_720.jpg";
    private static final String TEST_AVATAR = "https://upload.wikimedia.org/wikipedia/commons/a/a0/Arh-avatar.jpg";

    @Autowired
    private ObjectMapper mapper;

    public BLiveWebSocketData generateBroadcastData() {
        BLiveWebSocketData ws = new BLiveWebSocketData();
        ws.command = BLiveWebSocketData.CommandType.BOT_TESTING;
        var data = new BLiveWebSocketData.BLiveInfo();
        data.uid = 123456;
        data.name = "测试用户_Official";
        data.title = "测试直播";
        data.room_id = 123456789;
        data.cover = TEST_IMAGE_URL;
        ws.live_info = data;
        return ws;
    }

    public BLiveWebSocketData generateSuperChatData() {
        BLiveWebSocketData ws = this.generateBroadcastData();
        var sc = new SuperChatMessage();
        sc.message = "这是一个测试的醒目留言";
        sc.price = 999;
        sc.uid = 123;
        sc.start_time = System.currentTimeMillis();
        sc.user_info = new SuperChatMessage.UserInfo();
        sc.user_info.uname = "某个石油佬";
        sc.user_info.face = TEST_AVATAR;
        sc.user_info.name_color = "#d40b0b";
        ws.content = new JSONObject();
        ws.content.put("data", JSON.toJSON(sc));
        return ws;
    }

    public BLiveWebSocketData generateDanmuData() {
        BLiveWebSocketData ws = this.generateBroadcastData();
        var danmuContent = new JSONObject();
        var info = new JSONArray();
        info.set(0, "UNKNOWN");
        info.set(1, "这是一条测试弹幕"); // 弹幕内容
        var userInfo = new JSONArray();
        userInfo.set(0, 123456); // uid
        userInfo.set(1, "臭DD"); // 用户名称
        info.set(2, userInfo);
        danmuContent.put("cmd", ws.command);
        danmuContent.put("info", info);
        ws.content = danmuContent;
        return ws;
    }

    public BLiveWebSocketData generateInteractWordData() {
        BLiveWebSocketData ws = this.generateBroadcastData();
        var interactWordContent = new JSONObject();
        var data = new JSONObject();
        data.put("uname", "臭DD"); // 用户名称
        data.put("uid", 123456); // uid
        interactWordContent.put("data", data);
        ws.content = interactWordContent;
        return ws;
    }
}
