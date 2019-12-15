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

    private static SharedPreferences sSP;

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        sSP = context.getSharedPreferences(MQTT_NAME, Context.MODE_PRIVATE);
    }


}
