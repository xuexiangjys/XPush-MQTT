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

import com.xuexiang.xpush.mqtt.core.entity.ConnectionStatus;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MQTT事件监听器
 *
 * @author xuexiang
 * @since 2019-12-12 17:31
 */
public interface OnMqttEventListener {

    /**
     * 连接状态发生改变
     *
     * @param oldStatus
     * @param newStatus
     */
    void onConnectionStatusChanged(ConnectionStatus oldStatus, ConnectionStatus newStatus);

    /**
     * 连接完成
     *
     * @param reconnect 是否是重连
     * @param serverUri 连接的服务器地址
     */
    void onConnectComplete(boolean reconnect, String serverUri);

    /**
     * 连接丢失，是否需要手动重新连接
     *
     * @param cause
     * @return true:重新连接，false：不重新连接
     */
    boolean onConnectionLost(Throwable cause);

    /**
     * 收到消息
     *
     * @param topic   主题
     * @param message 消息
     */
    void onMessageReceived(String topic, MqttMessage message);

    /**
     * 消息发送成功
     *
     * @param message 消息
     */
    void onMessageDelivered(MqttMessage message);
}
