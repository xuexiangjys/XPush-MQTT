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

import com.xuexiang.xpush.core.IPushClient;

/**
 * MQTT实现的消息推送
 *
 * @author xuexiang
 * @since 2019-12-11 22:38
 */
public class MqttPushClient implements IPushClient {


    @Override
    public void init(Context context) {

    }

    @Override
    public void register() {

    }

    @Override
    public void unRegister() {

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

    }

    @Override
    public void deleteTags(String... tag) {

    }

    @Override
    public void getTags() {

    }

    @Override
    public String getPushToken() {
        return null;
    }

    @Override
    public int getPlatformCode() {
        return 0;
    }

    @Override
    public String getPlatformName() {
        return null;
    }
}
