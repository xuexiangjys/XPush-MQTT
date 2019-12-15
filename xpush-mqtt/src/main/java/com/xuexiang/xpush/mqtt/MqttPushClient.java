/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.xpush.mqtt;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.xuexiang.xpush.core.IPushClient;
import com.xuexiang.xpush.logs.PushLog;
import com.xuexiang.xpush.mqtt.agent.MqttPushAgent;
import com.xuexiang.xpush.mqtt.core.MqttCore;

/**
 * MQTT实现的消息推送
 *
 * @author xuexiang
 * @since 2019-12-11 22:38
 */
public class MqttPushClient implements IPushClient {

    public static final String MQTT_PUSH_PLATFORM_NAME = "MqttPush";
    public static final int MQTT_PUSH_PLATFORM_CODE = 1010;

    private static final String MQTT_HOST = "MQTT_HOST";
    private static final String MQTT_PORT = "MQTT_PORT";

    /**
     * 反射构造方法
     */
    public MqttPushClient() {
    }


    /**
     * 主动构造方法
     *
     * @param builder
     */
    public MqttPushClient(MqttCore.Builder builder) {
        MqttPushAgent.getInstance().init(builder);
    }

    @Override
    public void init(Context context) {
        if (!MqttPushAgent.isInitialized()) {
            //读取MQTT连接服务器对应的host和port
            try {
                Bundle metaData = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
                String host = metaData.getString(MQTT_HOST).trim();
                int port = metaData.getInt(MQTT_PORT, MqttCore.DEFAULT_MQTT_PORT);

                MqttPushAgent.getInstance().init(context, host, port);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                PushLog.e("can't find MQTT_HOST or MQTT_PORT in AndroidManifest.xml");
            } catch (NullPointerException e) {
                e.printStackTrace();
                PushLog.e("can't find MQTT_HOST or MQTT_PORT in AndroidManifest.xml");
            }
        }
    }

    @Override
    public void register() {
        MqttPushAgent.getInstance().register();
    }

    @Override
    public void unRegister() {
        MqttPushAgent.getInstance().unRegister();
    }

    @Override
    public void bindAlias(String alias) {

    }

    @Override
    public void unBindAlias(String alias) {

    }

    @Override
    public void getAlias() {

    }

    @Override
    public void addTags(String... tag) {

    }

    @Override
    public void deleteTags(String... tag) {

    }

    @Override
    public void getTags() {

    }

    @Override
    public String getPushToken() {
        return null;
    }

    @Override
    public int getPlatformCode() {
        return MQTT_PUSH_PLATFORM_CODE;
    }

    @Override
    public String getPlatformName() {
        return MQTT_PUSH_PLATFORM_NAME;
    }
}
