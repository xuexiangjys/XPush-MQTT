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

package com.xuexiang.xpush.mqtt.core;

import android.content.Context;
import android.text.TextUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * MQTT API
 *
 * @author xuexiang
 * @since 2019-12-11 22:41
 */
public class MqttCore implements IMqttActionListener, MqttCallbackExtended {

    /**
     * 客户端
     */
    private MqttAndroidClient mClient;
    /**
     * 连接参数
     */
    private MqttConnectOptions mOptions;

    /**
     * 连接状态
     */
    private ConnectionStatus mConnectionStatus = ConnectionStatus.NONE;
    /**
     * 客户端操作
     */
    private MqttAction mAction;

    /**
     * 订阅主题
     */
    private final Map<String, Subscription> mSubscriptions = new HashMap<>();

    private OnMqttListener mOnMqttListener;

    /**
     * 构建
     *
     * @param builder
     */
    private MqttCore(Builder builder) {
        String uri;
        if (builder.isUseTls) {
            uri = "ssl://" + builder.host + ":" + builder.port;
        } else {
            uri = "tcp://" + builder.host + ":" + builder.port;
        }
        mClient = new MqttAndroidClient(builder.context, uri, builder.clientId);
        mClient.setCallback(this);
        mClient.setTraceCallback(new XPushTraceCallback());
        mClient.setTraceEnabled(true);

        mOptions = new MqttConnectOptions();
        mOptions.setAutomaticReconnect(builder.automaticReconnect);
        if (!TextUtils.isEmpty(builder.userName)) {
            mOptions.setUserName(builder.userName);
        }
        if (!TextUtils.isEmpty(builder.passWord)) {
            mOptions.setPassword(builder.passWord.toCharArray());
        }
        if (builder.timeout != 0) {
            mOptions.setConnectionTimeout(builder.timeout);
        }
        if (builder.keepAlive != 0) {
            mOptions.setKeepAliveInterval(builder.keepAlive);
        }
        if (builder.willMessage != null) {
            mOptions.setWill(builder.willMessage.getTopic(), builder.willMessage.getMessage().getBytes(), builder.willMessage.getQos(), builder.willMessage.isRetain());
        }

    }

    //==========================初始化================================//

    /**
     * 初始化订阅
     *
     * @param subscriptions
     * @return
     */
    public MqttCore setSubscriptions(Subscription... subscriptions) {
        if (subscriptions != null && subscriptions.length > 0) {
            for (Subscription subscription : subscriptions) {
                mSubscriptions.put(subscription.getTopic(), subscription);
            }
        }
        return this;
    }

    /**
     * 初始化订阅
     *
     * @param subscriptions
     * @return
     */
    public MqttCore setSubscriptions(Collection<Subscription> subscriptions) {
        if (subscriptions != null && subscriptions.size() > 0) {
            for (Subscription subscription : subscriptions) {
                mSubscriptions.put(subscription.getTopic(), subscription);
            }
        }
        return this;
    }

    /**
     * 清除现有所有订阅的主题
     */
    public void clearSubscriptions() {
        mSubscriptions.clear();
    }

    /**
     * 修改连接参数
     *
     * @param options
     * @return
     */
    public MqttCore setConnectOptions(MqttConnectOptions options) {
        if (options != null) {
            mOptions = options;
        }
        return this;
    }

    public MqttCore setTraceEnabled(boolean traceEnabled) {
        if (mClient != null) {
            mClient.setTraceEnabled(traceEnabled);
        }
        return this;
    }

    public MqttCore setOnMqttListener(OnMqttListener onMqttListener) {
        mOnMqttListener = onMqttListener;
        return this;
    }

    //==========================主题订阅================================//

    /**
     * 订阅新的主题
     *
     * @param subscription
     */
    public void registerSubscription(Subscription subscription) {
        if (subscription == null || mSubscriptions.containsKey(subscription.getTopic())) {
            return;
        }

        if (subscribe(subscription)) {
            mSubscriptions.put(subscription.getTopic(), subscription);
        }
    }

    /**
     * 取消订阅主题
     *
     * @param subscription
     */
    public void unregisterSubscription(Subscription subscription) {
        if (subscription == null || !mSubscriptions.containsKey(subscription.getTopic())) {
            return;
        }

        if (unsubscribe(subscription)) {
            mSubscriptions.remove(subscription.getTopic());
        }
    }

    /**
     * 订阅所有主题
     */
    public void registerAllSubscriptions() {
        for (Subscription subscription : mSubscriptions.values()) {
            subscribe(subscription, true);
        }
    }

    /**
     * 取消所有订阅主题
     */
    public void unregisterAllSubscriptions() {
        for (Subscription subscription : mSubscriptions.values()) {
            unsubscribe(subscription, true);
        }
        mSubscriptions.clear();
    }


    //============================对外开放的动作===============================//

    /**
     * 建立连接
     *
     * @return
     */
    public boolean connect() {
        if (mClient == null) {
            return false;
        }

        try {
            changeConnectionStatus(ConnectionStatus.CONNECTING);
            mAction = MqttAction.CONNECT;
            mClient.connect(mOptions, null, this);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 建立连接
     *
     * @param listener 动作监听
     * @return
     */
    public void connect(IMqttActionListener listener) {
        if (mClient == null) {
            return;
        }
        try {
            changeConnectionStatus(ConnectionStatus.CONNECTING);
            mClient.connect(mOptions, null, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开连接
     *
     * @return
     */
    public boolean disconnect() {
        if (mClient == null) {
            return false;
        }

        try {
            changeConnectionStatus(ConnectionStatus.DISCONNECTING);
            mAction = MqttAction.DISCONNECT;
            mClient.disconnect(null, this);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 断开连接
     *
     * @return
     */
    public void disconnect(IMqttActionListener listener) {
        if (mClient == null) {
            return;
        }

        try {
            changeConnectionStatus(ConnectionStatus.DISCONNECTING);
            mClient.disconnect(null, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅主题【对外API】
     *
     * @param subscription
     * @param listener     动作监听
     */
    public void subscribe(Subscription subscription, IMqttActionListener listener) {
        if (mClient == null || subscription == null) {
            return;
        }
        try {
            mClient.subscribe(subscription.getTopic(), subscription.getQos(), null, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消订阅主题【对外API】
     *
     * @param subscription
     * @param listener     动作监听
     */
    public void unsubscribe(Subscription subscription, IMqttActionListener listener) {
        if (mClient == null || subscription == null) {
            return;
        }
        try {
            mClient.unsubscribe(subscription.getTopic(), null, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布主题【对外API】
     *
     * @param publishMessage
     * @param listener       动作监听
     */
    public void publish(PublishMessage publishMessage, IMqttActionListener listener) {
        if (mClient == null || publishMessage == null) {
            return;
        }

        try {
            mClient.publish(publishMessage.getTopic(), publishMessage.getMessage().getBytes(), publishMessage.getQos(), publishMessage.isRetain(), null, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //===================================内部动作========================================//

    /**
     * 订阅主题
     *
     * @param subscription
     */
    private boolean subscribe(Subscription subscription) {
        return subscribe(subscription, false);
    }

    /**
     * 订阅主题
     *
     * @param subscription
     * @param isSilent     是否静默设置，没有回调
     */
    private boolean subscribe(Subscription subscription, boolean isSilent) {
        if (mClient == null || subscription == null) {
            return false;
        }

        try {
            mAction = MqttAction.SUBSCRIBE;
            if (isSilent) {
                mClient.subscribe(subscription.getTopic(), subscription.getQos());
            } else {
                mClient.subscribe(subscription.getTopic(), subscription.getQos(), null, this);
            }
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 取消订阅主题
     *
     * @param subscription
     */
    private boolean unsubscribe(Subscription subscription) {
        return unsubscribe(subscription, false);
    }

    /**
     * 取消订阅主题
     *
     * @param subscription
     * @param isSilent     是否静默设置，没有回调
     */
    private boolean unsubscribe(Subscription subscription, boolean isSilent) {
        if (mClient == null || subscription == null) {
            return false;
        }

        try {
            mAction = MqttAction.UNSUBSCRIBE;
            if (isSilent) {
                mClient.unsubscribe(subscription.getTopic());
            } else {
                mClient.unsubscribe(subscription.getTopic(), null, this);
            }
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发布主题
     *
     * @param topic   主题
     * @param message 消息内容
     */
    private boolean publish(String topic, String message) {
        return publish(PublishMessage.get(topic, message));
    }

    /**
     * 发布主题
     *
     * @param publishMessage
     */
    private boolean publish(PublishMessage publishMessage) {
        if (mClient == null || publishMessage == null) {
            return false;
        }

        try {
            mAction = MqttAction.SUBSCRIBE;
            mClient.publish(publishMessage.getTopic(), publishMessage.getMessage().getBytes(), publishMessage.getQos(), publishMessage.isRetain(), null, this);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }

    //===========================监听事件================================//

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        switch (mAction) {
            case CONNECT:
                changeConnectionStatus(ConnectionStatus.CONNECTED);
                break;
            case DISCONNECT:
                changeConnectionStatus(ConnectionStatus.DISCONNECTED);
                break;
            case SUBSCRIBE:

                break;
            case PUBLISH:

                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        switch (mAction) {
            case CONNECT:
                changeConnectionStatus(ConnectionStatus.ERROR);
                break;
            case DISCONNECT:
                changeConnectionStatus(ConnectionStatus.DISCONNECTED);
                break;
            case SUBSCRIBE:

                break;
            case PUBLISH:

                break;
            default:
                break;
        }
    }

    /**
     * 连接状态改变
     *
     * @param newStatus 新状态
     */
    private void changeConnectionStatus(ConnectionStatus newStatus) {
        if (mConnectionStatus != newStatus) {
            if (mOnMqttListener != null) {
                mOnMqttListener.onConnectionStatusChanged(mConnectionStatus, newStatus);
            }
            mConnectionStatus = newStatus;
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        changeConnectionStatus(ConnectionStatus.CONNECTED);
        //连接成功后注册订阅
        registerAllSubscriptions();
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    //============================状态获取===============================//

    /**
     * @return 获取连接状态
     */
    public ConnectionStatus getConnectionStatus() {
        return mConnectionStatus;
    }

    public MqttAndroidClient getClient() {
        return mClient;
    }

    public MqttConnectOptions getConnectOptions() {
        return mOptions;
    }

    public String getClientId() {
        return mClient != null ? mClient.getClientId() : "";
    }

    public Map<String, Subscription> getSubscriptions() {
        return mSubscriptions;
    }

    public MqttAction getAction() {
        return mAction;
    }

    //===========================构建者================================//

    /**
     * 获取构建者
     *
     * @param context
     * @param host
     * @return
     */
    public static Builder Builder(Context context, String host) {
        return new Builder(context).setHost(host);
    }

    public static final int DEFAULT_MQTT_PORT = 1883;

    public static class Builder {

        Context context;
        /**
         * 服务地址
         */
        String host;
        /**
         * 端口
         */
        int port;
        /**
         * 是否使用tls
         */
        boolean isUseTls;
        /**
         * 客户端标识
         */
        String clientId;
        /**
         * 登录名
         */
        String userName;
        /**
         * 登录密码
         */
        String passWord;
        /**
         * 连接超时时间
         */
        int timeout;
        /**
         * 保持心跳连接的时间
         */
        int keepAlive;
        /**
         * 自动重新连接
         */
        boolean automaticReconnect;
        /**
         * 客户端连接丢失时服务端向自己发送的主题消息
         */
        PublishMessage willMessage;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
            port = DEFAULT_MQTT_PORT;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setUseTls(boolean useTls) {
            isUseTls = useTls;
            return this;
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setPassWord(String passWord) {
            this.passWord = passWord;
            return this;
        }

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setKeepAlive(int keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public Builder setWillMessage(PublishMessage willMessage) {
            this.willMessage = willMessage;
            return this;
        }

        public MqttCore build() {
            if (host == null) {
                throw new IllegalArgumentException("You must set mqtt server host!");
            }
            return new MqttCore(this);
        }

    }

}
