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

package com.xuexiang.xpush.mqtt.core.entity;

/**
 * MQTT协议的动作
 *
 * @author xuexiang
 * @since 2019-12-11 23:54
 */
public enum MqttAction {
    /**
     * 连接动作
     **/
    CONNECT("连接"),
    /**
     * 断开动作
     **/
    DISCONNECT("断开连接"),
    /**
     * 订阅动作
     **/
    SUBSCRIBE("订阅"),
    /**
     * 取消订阅动作
     **/
    UNSUBSCRIBE("取消订阅"),
    /**
     * 发送动作
     **/
    PUBLISH("发送");

    /**
     * 名称
     */
    public String actionName;
    /**
     * 携带的参数
     */
    public Object args;

    MqttAction(String actionName) {
        this.actionName = actionName;
    }

    public String getName() {
        return actionName;
    }

    public MqttAction setArgs(Object args) {
        this.args = args;
        return this;
    }

    public Object getArgs() {
        return args;
    }
}
