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

import org.eclipse.paho.android.service.MqttTraceHandler;

/**
 * @author xuexiang
 * @since 2019-12-12 9:32
 */
public class XPushTraceCallback implements MqttTraceHandler {

    private static final String TAG = "MQTT-";

    @Override
    public void traceDebug(String tag, String message) {
        PushLog.d(TAG + tag + ": " + message);
    }

    @Override
    public void traceError(String tag, String message) {
        PushLog.e(TAG + tag + ": " + message);
    }

    @Override
    public void traceException(String tag, String message, Exception e) {
        PushLog.e(TAG + tag + ": " + message, e);
    }

}
