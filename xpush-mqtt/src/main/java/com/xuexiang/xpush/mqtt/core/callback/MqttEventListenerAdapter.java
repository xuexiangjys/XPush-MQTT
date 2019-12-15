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

package com.xuexiang.xpush.mqtt.core.callback;

import com.xuexiang.xpush.logs.PushLog;
import com.xuexiang.xpush.mqtt.core.entity.ConnectionStatus;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MQTT事件监听适配器
 *
 * @author xuexiang
 * @since 2019-12-12 21:55
 */
public abstract class MqttEventListenerAdapter implements OnMqttEventListener {

    private static final String TAG = "MQTT-";

    @Override
    public void onConnectionStatusChanged(ConnectionStatus oldStatus, ConnectionStatus newStatus) {
        PushLog.d(TAG + "[onConnectionStatusChanged]: oldStatus:" + oldStatus + ", newStatus:" + newStatus);
    }

    @Override
    public void onConnectComplete(boolean reconnect, String serverUri) {
        PushLog.d(TAG + "[onConnectComplete]: reconnect:" + reconnect + ", serverURI:" + serverUri);
    }

    @Override
    public boolean onConnectionLost(Throwable cause) {
        PushLog.e(TAG + "[onConnectionLost]", cause);
        return false;
    }

    @Override
    public void onMessageDelivered(MqttMessage message) {
        PushLog.d(TAG + "[onMessageDelivered]:" + message.toString());
    }
}
