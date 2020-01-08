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
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.xuexiang.xpush.mqtt.core.callback.OnMqttActionListener;
import com.xuexiang.xpush.mqtt.core.callback.OnMqttEventListener;
import com.xuexiang.xpush.mqtt.core.callback.XPushTraceCallback;
import com.xuexiang.xpush.mqtt.core.entity.ConnectionStatus;
import com.xuexiang.xpush.mqtt.core.entity.MqttAction;
import com.xuexiang.xpush.mqtt.core.entity.PublishMessage;
import com.xuexiang.xpush.mqtt.core.entity.Subscription;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MQTT Core API
 *
 * @author xuexiang
 * @since 2019-12-11 22:41
 */
public class MqttCore implements IMqttActionListener, MqttCallbackExtended {

    private Context mContext;
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
     * 订阅主题
     */
    private final Map<String, Subscription> mSubscriptions = new HashMap<>();

    /**
     * MQTT事件监听器
     */
    private OnMqttEventListener mOnMqttEventListener;
    /**
     * MQTT动作监听器
     */
    private OnMqttActionListener mOnMqttActionListener;

    private Handler mHandler;
    /**
     * 手动重连的延迟时间，默认是5秒
     */
    private long mReconnectDelay = 5 * 1000L;

    /**
     * 构建
     *
     * @param builder
     */
    private MqttCore(Builder builder) {
        mContext = builder.context;
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

        mOptions = getConnectOptions(builder);
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取连接参数
     *
     * @param builder
     * @return
     */
    private MqttConnectOptions getConnectOptions(Builder builder) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(builder.automaticReconnect);
        if (!TextUtils.isEmpty(builder.userName)) {
            options.setUserName(builder.userName);
        }
        if (!TextUtils.isEmpty(builder.passWord)) {
            options.setPassword(builder.passWord.toCharArray());
        }
        if (builder.timeout != 0) {
            options.setConnectionTimeout(builder.timeout);
        }
        if (builder.keepAlive != 0) {
            options.setKeepAliveInterval(builder.keepAlive);
        }
        if (builder.willMessage != null) {
            options.setWill(builder.willMessage.getTopic(), builder.willMessage.getPayload(), builder.willMessage.getQos(), builder.willMessage.isRetain());
        }
        return options;
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

    /**
     * 设置MQTT事件监听器
     *
     * @param onMqttEventListener
     * @return
     */
    public MqttCore setOnMqttEventListener(OnMqttEventListener onMqttEventListener) {
        mOnMqttEventListener = onMqttEventListener;
        return this;
    }

    /**
     * 设置MQTT动作监听器
     *
     * @param onMqttActionListener
     * @return
     */
    public MqttCore setOnMqttActionListener(OnMqttActionListener onMqttActionListener) {
        mOnMqttActionListener = onMqttActionListener;
        return this;
    }

    /**
     * 设置手动重连的延迟时间
     *
     * @param reconnectDelay
     * @return
     */
    public MqttCore setReconnectDelay(long reconnectDelay) {
        mReconnectDelay = reconnectDelay;
        return this;
    }

    //==========================主题订阅================================//

    /**
     * 订阅新的主题
     *
     * @param topic 主题
     */
    public void registerSubscription(String topic) {
        registerSubscription(Subscription.wrap(topic));
    }

    /**
     * 订阅多个新的主题
     *
     * @param subscriptions
     */
    public void registerSubscriptions(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            registerSubscription(subscription);
        }
    }

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
     * @param topic 主题
     */
    public void unregisterSubscription(String topic) {
        unregisterSubscription(Subscription.wrap(topic));
    }

    /**
     * 取消多个订阅主题
     *
     * @param subscriptions 主题
     */
    public void unregisterSubscriptions(Subscription... subscriptions) {
        for (Subscription subscription : subscriptions) {
            unregisterSubscription(subscription);
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
        if (mClient == null || mClient.isConnected()) {
            return false;
        }

        try {
            changeConnectionStatus(ConnectionStatus.CONNECTING);
            mClient.connect(mOptions, MqttAction.CONNECT, this);
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
        if (mClient == null || mClient.isConnected()) {
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
        if (!isConnected()) {
            return false;
        }

        try {
            changeConnectionStatus(ConnectionStatus.DISCONNECTING);
            mClient.disconnect(MqttAction.DISCONNECT, this);
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
        if (!isConnected()) {
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
        if (!isConnected()) {
            return;
        }
        if (subscription == null || TextUtils.isEmpty(subscription.getTopic())) {
            return;
        }

        try {
            mClient.subscribe(subscription.getTopic(), subscription.getQos(), null, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅主题【对外API】
     *
     * @param subscription
     * @param actionListener  动作监听
     * @param messageListener 消息监听
     */
    public void subscribe(Subscription subscription, IMqttActionListener actionListener, IMqttMessageListener messageListener) {
        if (!isConnected()) {
            return;
        }
        if (subscription == null || TextUtils.isEmpty(subscription.getTopic())) {
            return;
        }

        try {
            mClient.subscribe(subscription.getTopic(), subscription.getQos(), null, actionListener, messageListener);
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
        if (!isConnected()) {
            return;
        }
        if (subscription == null || TextUtils.isEmpty(subscription.getTopic())) {
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
        if (!isConnected()) {
            return;
        }
        if (publishMessage == null || TextUtils.isEmpty(publishMessage.getTopic())) {
            return;
        }

        try {
            mClient.publish(publishMessage.getTopic(), publishMessage.getPayload(), publishMessage.getQos(), publishMessage.isRetain(), null, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁【必须先断开连接】
     */
    public void close() {
        if (mClient != null) {
            if (!mClient.isConnected()) {
                recycle();
            } else {
                try {
                    mClient.disconnect(null, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            recycle();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            recycle();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 资源回收
     */
    private void recycle() {
        mClient.close();
        mClient.unregisterResources();
        mClient = null;
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mOnMqttEventListener = null;
        mOnMqttActionListener = null;
        mOptions = null;
        mConnectionStatus = null;
        mSubscriptions.clear();
        mContext = null;
    }

    //===================================内部动作========================================//

    /**
     * 订阅主题
     *
     * @param subscription 主题信息
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
        if (!isConnected()) {
            return false;
        }
        if (subscription == null || TextUtils.isEmpty(subscription.getTopic())) {
            return false;
        }

        try {
            if (isSilent) {
                mClient.subscribe(subscription.getTopic(), subscription.getQos());
            } else {
                MqttAction action = MqttAction.SUBSCRIBE.setArgs(subscription);
                mClient.subscribe(subscription.getTopic(), subscription.getQos(), action, this);
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
        if (!isConnected()) {
            return false;
        }
        if (subscription == null || TextUtils.isEmpty(subscription.getTopic())) {
            return false;
        }

        try {
            if (isSilent) {
                mClient.unsubscribe(subscription.getTopic());
            } else {
                MqttAction action = MqttAction.UNSUBSCRIBE.setArgs(subscription);
                mClient.unsubscribe(subscription.getTopic(), action, this);
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
    public boolean publish(String topic, String message) {
        return publish(PublishMessage.wrap(topic, message));
    }

    /**
     * 发布主题
     *
     * @param publishMessage
     */
    public boolean publish(PublishMessage publishMessage) {
        if (!isConnected()) {
            return false;
        }
        if (publishMessage == null || TextUtils.isEmpty(publishMessage.getTopic())) {
            return false;
        }

        try {
            MqttAction action = MqttAction.PUBLISH.setArgs(publishMessage);
            mClient.publish(publishMessage.getTopic(), publishMessage.getPayload(), publishMessage.getQos(), publishMessage.isRetain(), action, this);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }

    //===========================监听事件================================//

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        MqttAction action = (MqttAction) asyncActionToken.getUserContext();
        onActionSuccess(action, asyncActionToken);

        switch (action) {
            case CONNECT:
                changeConnectionStatus(ConnectionStatus.CONNECTED);
                break;
            case DISCONNECT:
                changeConnectionStatus(ConnectionStatus.DISCONNECTED);
                break;
            case SUBSCRIBE:
            case PUBLISH:
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        MqttAction action = (MqttAction) asyncActionToken.getUserContext();
        onActionFailure(action, asyncActionToken, exception);

        switch (action) {
            case CONNECT:
                changeConnectionStatus(ConnectionStatus.ERROR);
                break;
            case DISCONNECT:
                changeConnectionStatus(ConnectionStatus.DISCONNECTED);
                break;
            case SUBSCRIBE:
            case PUBLISH:
                break;
            default:
                break;
        }
    }

    /**
     * 动作成功
     *
     * @param action
     * @param asyncActionToken
     */
    public void onActionSuccess(MqttAction action, IMqttToken asyncActionToken) {
        if (mOnMqttActionListener != null) {
            mOnMqttActionListener.onActionSuccess(action, asyncActionToken);
        }
    }

    /**
     * 动作失败
     *
     * @param action
     * @param asyncActionToken
     */
    public void onActionFailure(MqttAction action, IMqttToken asyncActionToken, Throwable exception) {
        if (mOnMqttActionListener != null) {
            mOnMqttActionListener.onActionFailure(action, asyncActionToken, exception);
        }
    }

    /**
     * 连接状态改变
     *
     * @param newStatus 新状态
     */
    private void changeConnectionStatus(ConnectionStatus newStatus) {
        if (mConnectionStatus != newStatus) {
            if (mOnMqttEventListener != null) {
                mOnMqttEventListener.onConnectionStatusChanged(mConnectionStatus, newStatus);
            }
            mConnectionStatus = newStatus;
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        changeConnectionStatus(ConnectionStatus.CONNECTED);
        //连接成功后注册订阅
        registerAllSubscriptions();

        if (mOnMqttEventListener != null) {
            mOnMqttEventListener.onConnectComplete(reconnect, serverURI);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        changeConnectionStatus(ConnectionStatus.DISCONNECTED);
        if (mOnMqttEventListener != null) {
            boolean isNeedReconnect = mOnMqttEventListener.onConnectionLost(cause);
            if (isNeedReconnect && !mOptions.isAutomaticReconnect()) {
                reconnect();
            }
        }
    }

    /**
     * 重新连接
     */
    private void reconnect() {
        if (mHandler != null) {
            //手动连接
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connect();
                }
            }, mReconnectDelay);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (mOnMqttEventListener != null) {
            mOnMqttEventListener.onMessageReceived(topic, message);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        if (mOnMqttEventListener != null) {
            try {
                mOnMqttEventListener.onMessageDelivered(token.getMessage());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
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

    public String getClientHandle() {
        if (mClient != null) {
            try {
                Class<?> ownerClass = mClient.getClass();
                Field field = ownerClass.getDeclaredField("clientHandle");
                field.setAccessible(true);
                return (String) field.get(mClient);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public Map<String, Subscription> getSubscriptions() {
        return mSubscriptions;
    }

    public List<Subscription> getSubscriptionList() {
        return new ArrayList<>(mSubscriptions.values());
    }

    public boolean isConnected() {
        return mClient != null && mClient.isConnected();
    }

    public Context getContext() {
        return mContext;
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

    /**
     * 默认mqtt监听端口
     */
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
         * 自动重新连接,每隔1秒重连一次，如果失败则延长1倍时间，直到间隔为2分钟为止
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

        public Builder setAutomaticReconnect(boolean automaticReconnect) {
            this.automaticReconnect = automaticReconnect;
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
