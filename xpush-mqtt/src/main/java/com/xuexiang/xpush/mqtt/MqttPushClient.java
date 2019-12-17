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

import com.xuexiang.xpush.XPush;
import com.xuexiang.xpush.core.IPushClient;
import com.xuexiang.xpush.core.XPushManager;
import com.xuexiang.xpush.logs.PushLog;
import com.xuexiang.xpush.mqtt.agent.MqttPushAgent;
import com.xuexiang.xpush.mqtt.core.MqttCore;
import com.xuexiang.xpush.mqtt.core.callback.MqttEventListenerAdapter;
import com.xuexiang.xpush.mqtt.core.callback.OnMqttActionListener;
import com.xuexiang.xpush.mqtt.core.callback.OnMqttEventListener;
import com.xuexiang.xpush.mqtt.core.entity.ConnectionStatus;
import com.xuexiang.xpush.mqtt.core.entity.MqttAction;
import com.xuexiang.xpush.mqtt.core.entity.Subscription;
import com.xuexiang.xpush.util.PushUtils;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Set;

import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_ADD_TAG;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_DEL_TAG;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_GET_TAG;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_REGISTER;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_UNREGISTER;
import static com.xuexiang.xpush.core.annotation.ConnectStatus.CONNECTED;
import static com.xuexiang.xpush.core.annotation.ConnectStatus.CONNECTING;
import static com.xuexiang.xpush.core.annotation.ConnectStatus.DISCONNECT;
import static com.xuexiang.xpush.core.annotation.ResultCode.RESULT_ERROR;
import static com.xuexiang.xpush.core.annotation.ResultCode.RESULT_OK;
import static com.xuexiang.xpush.mqtt.core.entity.MqttAction.SUBSCRIBE;

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
        if (MqttPushAgent.getInstance().register(mOnMqttActionListener)) {
            MqttPushAgent.getInstance().setOnMqttEventListener(mOnMqttEventListener);
        } else {
            XPush.transmitCommandResult(MqttPushAgent.getContext(), TYPE_REGISTER, RESULT_ERROR, null, null, "推送已注册");
        }
    }


    @Override
    public void unRegister() {
        if (MqttPushAgent.getInstance().unRegister()) {
            MqttPushAgent.getInstance().setOnMqttEventListener(null);
        } else {
            XPush.transmitCommandResult(MqttPushAgent.getContext(), TYPE_UNREGISTER, RESULT_ERROR, null, null, "推送尚未注册");
        }
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
        if (!MqttPushAgent.getInstance().addTags(tag)) {
            XPush.transmitCommandResult(MqttPushAgent.getContext(), TYPE_ADD_TAG, RESULT_ERROR, null, null, "推送尚未连接");
        }
    }

    @Override
    public void deleteTags(String... tag) {
        if (!MqttPushAgent.getInstance().deleteTags(tag)) {
            XPush.transmitCommandResult(MqttPushAgent.getContext(), TYPE_DEL_TAG, RESULT_ERROR, null, null, "推送尚未连接");
        }
    }

    @Override
    public void getTags() {
        Set<String> tags = MqttPushAgent.getInstance().getTags();
        XPush.transmitCommandResult(MqttPushAgent.getContext(), TYPE_GET_TAG, RESULT_OK, PushUtils.collection2String(tags), null, null);
    }

    @Override
    public String getPushToken() {
        return MqttPushAgent.getPushToken();
    }

    @Override
    public int getPlatformCode() {
        return MQTT_PUSH_PLATFORM_CODE;
    }

    @Override
    public String getPlatformName() {
        return MQTT_PUSH_PLATFORM_NAME;
    }


    /**
     * 动作监听
     */
    private OnMqttActionListener mOnMqttActionListener = new OnMqttActionListener() {
        @Override
        public void onActionSuccess(MqttAction action, IMqttToken actionToken) {
            switch (action) {
                case CONNECT:
                    MqttPushAgent.getInstance().updateToken();
                    XPush.transmitCommandResult(MqttPushAgent.getContext(), TYPE_REGISTER, RESULT_OK, MqttPushAgent.getPushToken(), null, "");
                    break;
                case DISCONNECT:
                    XPush.transmitCommandResult(MqttPushAgent.getContext(), TYPE_UNREGISTER, RESULT_OK, null, null, "");
                    break;
                case SUBSCRIBE:
                case UNSUBSCRIBE:
                    MqttPushAgent.getInstance().updateTags();
                    Subscription subscription = action.args != null ? (Subscription) action.args : null;
                    XPush.transmitCommandResult(MqttPushAgent.getContext(), SUBSCRIBE.equals(action) ? TYPE_ADD_TAG : TYPE_DEL_TAG, RESULT_OK, subscription != null ? subscription.getTopic() : "", null, "");
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onActionFailure(MqttAction action, IMqttToken actionToken, Throwable exception) {
            String errorMessage = exception != null ? exception.getMessage() : "";
            switch (action) {
                case CONNECT:
                    XPush.transmitCommandResult(MqttPushAgent.getContext(), TYPE_REGISTER, RESULT_ERROR, null, null, errorMessage);
                    break;
                case DISCONNECT:
                    XPush.transmitCommandResult(MqttPushAgent.getContext(), TYPE_UNREGISTER, RESULT_ERROR, null, null, errorMessage);
                    break;
                case SUBSCRIBE:
                case UNSUBSCRIBE:
                    XPush.transmitCommandResult(MqttPushAgent.getContext(), SUBSCRIBE.equals(action) ? TYPE_ADD_TAG : TYPE_DEL_TAG, RESULT_ERROR, null, null, errorMessage);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 事件监听
     */
    private OnMqttEventListener mOnMqttEventListener = new MqttEventListenerAdapter() {
        @Override
        public void onMessageReceived(String topic, MqttMessage message) {
            XPush.transmitMessage(MqttPushAgent.getContext(), message.toString(), null, null);
        }

        @Override
        public void onConnectComplete(boolean reconnect, String serverUri) {
            super.onConnectComplete(reconnect, serverUri);
        }

        @Override
        public void onConnectionStatusChanged(ConnectionStatus oldStatus, ConnectionStatus newStatus) {
            super.onConnectionStatusChanged(oldStatus, newStatus);
            if (newStatus.equals(ConnectionStatus.CONNECTED)) {
                XPushManager.get().notifyConnectStatusChanged(CONNECTED);
            } else if (newStatus.equals(ConnectionStatus.CONNECTING)) {
                XPushManager.get().notifyConnectStatusChanged(CONNECTING);
            } else if (newStatus.equals(ConnectionStatus.DISCONNECTED) || newStatus.equals(ConnectionStatus.ERROR)) {
                XPushManager.get().notifyConnectStatusChanged(DISCONNECT);
            }

        }
    };


}
