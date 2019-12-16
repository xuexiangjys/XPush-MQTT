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
import android.content.SharedPreferences;

import com.xuexiang.xpush.mqtt.agent.entity.MqttOptions;
import com.xuexiang.xpush.mqtt.core.MqttCore;
import com.xuexiang.xpush.mqtt.core.entity.Subscription;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MQTT 参数本地持久化，使用SharedPreferences进行存储
 *
 * @author xuexiang
 * @since 2019-12-16 00:18
 */
public final class MqttPersistence {

    private MqttPersistence() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static final String MQTT_NAME = "mqtt";

    private static final String KEY_CLIENT_ID = "key_client_id";
    private static final String KEY_SERVER_HOST = "key_server_host";
    private static final String KEY_SERVER_PORT = "key_server_port";
    private static final String KEY_USER_NAME = "key_user_name";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_KEEP_ALIVE = "key_keep_alive";
    private static final String KEY_TIMEOUT = "key_timeout";

    private static final String KEY_SUBSCRIPTION_TOPIC = "key_subscription_topic";

    private static SharedPreferences sSP;

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        sSP = context.getSharedPreferences(MQTT_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存客户端ID
     *
     * @param clientId
     */
    public static void saveClientId(String clientId) {
        sSP.edit().putString(KEY_CLIENT_ID, clientId).apply();
    }

    /**
     * 保存服务ip地址
     *
     * @param host
     */
    public static void saveServerHost(String host) {
        sSP.edit().putString(KEY_SERVER_HOST, host).apply();
    }

    /**
     * 保存服务端口
     *
     * @param port
     */
    public static void saveServerPort(int port) {
        sSP.edit().putInt(KEY_SERVER_PORT, port).apply();
    }

    /**
     * 保存用户名
     *
     * @param userName
     */
    public static void saveUserName(String userName) {
        sSP.edit().putString(KEY_USER_NAME, userName).apply();
    }

    /**
     * 保存密码
     *
     * @param password
     */
    public static void savePassword(String password) {
        sSP.edit().putString(KEY_PASSWORD, password).apply();
    }

    /**
     * 保存心跳保持的时间间隔设置
     *
     * @param keepAlive
     */
    public static void saveKeepAlive(int keepAlive) {
        sSP.edit().putInt(KEY_KEEP_ALIVE, keepAlive).apply();
    }

    /**
     * 保存连接超时时间设置
     *
     * @param timeout
     */
    public static void saveTimeout(int timeout) {
        sSP.edit().putInt(KEY_TIMEOUT, timeout).apply();
    }

    /**
     * 保存订阅主题的信息
     *
     * @param subscriptions
     */
    public static void saveSubscriptions(List<Subscription> subscriptions) {
        sSP.edit().putStringSet(KEY_SUBSCRIPTION_TOPIC, getSubscriptionSet(subscriptions)).apply();
    }

    public static Set<String> getSubscriptionSet(List<Subscription> subscriptions) {
        Set<String> topics = new HashSet<>();
        if (subscriptions != null && !subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                topics.add(subscription.getTopic());
            }
        }
        return topics;
    }

    //==============================================//

    public static String getClientId() {
        return sSP.getString(KEY_CLIENT_ID, "");
    }

    public static String getServerHost() {
        return sSP.getString(KEY_SERVER_HOST, "");
    }

    public static int getServerPort() {
        return sSP.getInt(KEY_SERVER_PORT, MqttCore.DEFAULT_MQTT_PORT);
    }

    public static String getUserName() {
        return sSP.getString(KEY_USER_NAME, "");
    }

    public static String getPassword() {
        return sSP.getString(KEY_PASSWORD, "");
    }

    public static int getKeepAlive() {
        return sSP.getInt(KEY_PASSWORD, 0);
    }

    public static int getTimeout() {
        return sSP.getInt(KEY_TIMEOUT, 0);
    }

    public static Set<String> getSubscriptionSet() {
        return sSP.getStringSet(KEY_SUBSCRIPTION_TOPIC, null);
    }

    public static List<Subscription> getSubscriptions() {
        Set<String> topics = sSP.getStringSet(KEY_SUBSCRIPTION_TOPIC, null);
        if (topics == null) {
            return null;
        }

        List<Subscription> subscriptions = new ArrayList<>();
        for (String topic : topics) {
            subscriptions.add(Subscription.wrap(topic));
        }
        return subscriptions;
    }


    /**
     * 获取mqtt连接配置信息
     *
     * @return
     */
    public static MqttOptions getMqttOptions() {
        return MqttOptions.create(getServerHost())
                .setPort(getServerPort())
                .setClientId(getClientId())
                .setUserName(getUserName())
                .setPassword(getPassword())
                .setKeepAlive(getKeepAlive())
                .setTimeout(getTimeout())
                .setSubscriptions(getSubscriptions());
    }

}
