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
 * 发布的主题消息
 *
 * @author xuexiang
 * @since 2019-12-12 00:45
 */
public class PublishMessage {

    /**
     * 主题
     */
    private String mTopic;
    /**
     * 消息
     */
    private String mMessage;
    /**
     * 请求质量
     */
    private int mQos;
    /**
     * 是否保持
     */
    private boolean mRetain;

    public static PublishMessage get(String topic, String message) {
        return new PublishMessage(topic, message);
    }

    public PublishMessage(String topic, String message) {
        mTopic = topic;
        mMessage = message;
    }

    public String getTopic() {
        return mTopic;
    }

    public PublishMessage setTopic(String topic) {
        mTopic = topic;
        return this;
    }

    public String getMessage() {
        return mMessage;
    }

    public PublishMessage setMessage(String message) {
        mMessage = message;
        return this;
    }

    public int getQos() {
        return mQos;
    }

    public PublishMessage setQos(int qos) {
        mQos = qos;
        return this;
    }

    public boolean isRetain() {
        return mRetain;
    }

    public PublishMessage setRetain(boolean retain) {
        mRetain = retain;
        return this;
    }

    @Override
    public String toString() {
        return "PublishMessage{" +
                "mTopic='" + mTopic + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mQos=" + mQos +
                ", mRetain=" + mRetain +
                '}';
    }
}
