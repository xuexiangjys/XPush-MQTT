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

/**
 * 订阅信息
 *
 * @author xuexiang
 * @since 2019-12-12 00:16
 */
public class Subscription {

    private String mTopic;
    private int mQos;

    public Subscription(String topic) {
        mTopic = topic;
    }

    public Subscription(String topic, int qos) {
        mTopic = topic;
        mQos = qos;
    }

    public String getTopic() {
        return mTopic;
    }

    public Subscription setTopic(String topic) {
        mTopic = topic;
        return this;
    }

    public int getQos() {
        return mQos;
    }

    public Subscription setQos(int qos) {
        mQos = qos;
        return this;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "mTopic='" + mTopic + '\'' +
                ", mQos=" + mQos +
                '}';
    }
}
