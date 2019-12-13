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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 发布的主题消息
 *
 * @author xuexiang
 * @since 2019-12-12 00:45
 */
public class PublishMessage implements Parcelable {

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

    protected PublishMessage(Parcel in) {
        mTopic = in.readString();
        mMessage = in.readString();
        mQos = in.readInt();
        mRetain = in.readByte() != 0;
    }

    public static final Creator<PublishMessage> CREATOR = new Creator<PublishMessage>() {
        @Override
        public PublishMessage createFromParcel(Parcel in) {
            return new PublishMessage(in);
        }

        @Override
        public PublishMessage[] newArray(int size) {
            return new PublishMessage[size];
        }
    };

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
        return "[" + mTopic + "] " + mMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTopic);
        dest.writeString(mMessage);
        dest.writeInt(mQos);
        dest.writeByte((byte) (mRetain ? 1 : 0));
    }
}
