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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 订阅信息
 *
 * @author xuexiang
 * @since 2019-12-12 00:16
 */
public class Subscription implements Parcelable {

    /**
     * 主题
     */
    private String mTopic;
    /**
     * 质量
     */
    private int mQos;

    protected Subscription(Parcel in) {
        mTopic = in.readString();
        mQos = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTopic);
        dest.writeInt(mQos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Subscription> CREATOR = new Creator<Subscription>() {
        @Override
        public Subscription createFromParcel(Parcel in) {
            return new Subscription(in);
        }

        @Override
        public Subscription[] newArray(int size) {
            return new Subscription[size];
        }
    };

    public static Subscription wrap(String topic) {
        return new Subscription(topic);
    }

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
        return mTopic;
    }
}
