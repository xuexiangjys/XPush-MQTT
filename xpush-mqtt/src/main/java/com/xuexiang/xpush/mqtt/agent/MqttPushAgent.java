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

package com.xuexiang.xpush.mqtt.agent;

import android.content.Context;
import android.text.TextUtils;

import com.xuexiang.xpush.mqtt.core.MqttCore;
import com.xuexiang.xpush.mqtt.core.callback.OnMqttEventListener;

/**
 * MQTT 推送代理
 *
 * @author xuexiang
 * @since 2019-12-15 22:41
 */
public final class MqttPushAgent {

    private static volatile MqttPushAgent sInstance = null;

    /**
     * MQTT核心类
     */
    private MqttCore mMqttCore;

    private Context mContext;

    /**
     * 服务器地址
     */
    private String mHost;
    /**
     * 端口号
     */
    private int mPort;

    private MqttPushAgent() {

    }

    /**
     * 获取单例
     *
     * @return
     */
    public static MqttPushAgent getInstance() {
        if (sInstance == null) {
            synchronized (MqttPushAgent.class) {
                if (sInstance == null) {
                    sInstance = new MqttPushAgent();
                }
            }
        }
        return sInstance;
    }


    /**
     * 初始化
     *
     * @param builder
     */
    public void init(MqttCore.Builder builder) {
        mMqttCore = builder.build();
        mContext = mMqttCore.getContext();
        MqttPersistence.init(mContext);
    }

    /**
     * 初始化
     *
     * @param mqttCore
     */
    public void init(MqttCore mqttCore) {
        mMqttCore = mqttCore;
        mContext = mMqttCore.getContext();
        MqttPersistence.init(mContext);
    }

    /**
     * 初始化
     *
     * @param context
     * @param host    mqtt服务器地址
     * @param port    端口
     */
    public void init(Context context, String host, int port) {
        mContext = context.getApplicationContext();
        mHost = host;
        mPort = port;
        MqttPersistence.init(mContext);
    }

    /**
     * @return 是否初始化过
     */
    public static boolean isInitialized() {
        return getInstance().mMqttCore != null;
    }

    /**
     * 注册
     */
    public void register() {
        if (mMqttCore == null) {
            if (TextUtils.isEmpty(mHost)) {
                throw new IllegalArgumentException("Mqtt push host is not init," +
                        "please call MqttPushAgent.getInstance().init to set host.");
            }
            mMqttCore = MqttCore.Builder(mContext, mHost)
                    .setPort(mPort)
                    .build();
        }

        if (!mMqttCore.isConnected()) {
            mMqttCore.connect();
        }
    }

    /**
     * 设置MQTT事件监听器
     *
     * @param listener
     * @return
     */
    public MqttPushAgent setOnMqttEventListener(OnMqttEventListener listener) {
        if (mMqttCore != null) {
            mMqttCore.setOnMqttEventListener(listener);
        }
        return this;
    }


    public void unRegister() {

    }


    public static Context getContext() {
        return getInstance().mContext;
    }

    public MqttCore getMqttCore() {
        return mMqttCore;
    }
}
