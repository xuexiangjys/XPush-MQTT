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

package com.xuexiang.mqttdemo.core.mqtt;

import com.xuexiang.mqttdemo.utils.XToastUtils;
import com.xuexiang.xpush.mqtt.core.MqttAction;
import com.xuexiang.xpush.mqtt.core.OnMqttActionListener;

import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * 简单的MQTT动作监听
 *
 * @author xuexiang
 * @since 2019-12-13 13:56
 */
public class SimpleMqttActionListener implements OnMqttActionListener {

    @Override
    public void onActionSuccess(MqttAction action, IMqttToken actionToken) {
        if (action.getArgs() != null) {
            XToastUtils.success("[" + action.getArgs() + "]" + action.getName() + "成功");
        } else {
            XToastUtils.success(action.getName() + "成功");
        }
    }

    @Override
    public void onActionFailure(MqttAction action, IMqttToken actionToken, Throwable exception) {
        XToastUtils.error(exception);
    }

}
