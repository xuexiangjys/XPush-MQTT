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

package com.xuexiang.xpush.mqtt.agent.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.xuexiang.xpush.mqtt.core.MqttCore;
import com.xuexiang.xpush.mqtt.core.entity.Subscription;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.List;

/**
 * MQTT客户端设置选项
 *
 * @author xuexiang
 * @since 2019-12-12 22:26
 */
public class MqttOptions implements Parcelable {

    public static final String KEY = "key_mqtt_option";

    //======基础设置====//
    /**
     * 客户端ID
     */
    private String mClientId;
    /**
     * 服务器IP
     */
    private String mHost;
    /**
     * 端口号
     */
    private int mPort;

    //======高级设置====//
    /**
     * 用户名
     */
    private String mUserName;
    /**
     * 密码
     */
    private String mPassword;
    /**
     * 连接超时时间
     */
    private int mTimeout;
    /**
     * 心跳保持的时间
     */
    private int mKeepAlive;
    /**
     * 订阅主题
     */
    private List<Subscription> mSubscriptions;

    public static MqttOptions create(String host) {
        return new MqttOptions(host);
    }

    public MqttOptions(String host) {
        mHost = host;
    }


    protected MqttOptions(Parcel in) {
        mClientId = in.readString();
        mHost = in.readString();
        mPort = in.readInt();
        mUserName = in.readString();
        mPassword = in.readString();
        mTimeout = in.readInt();
        mKeepAlive = in.readInt();
        mSubscriptions = in.createTypedArrayList(Subscription.CREATOR);
    }

    public static final Creator<MqttOptions> CREATOR = new Creator<MqttOptions>() {
        @Override
        public MqttOptions createFromParcel(Parcel in) {
            return new MqttOptions(in);
        }

        @Override
        public MqttOptions[] newArray(int size) {
            return new MqttOptions[size];
        }
    };

    public String getClientId() {
        return mClientId;
    }

    public MqttOptions setClientId(String clientId) {
        mClientId = clientId;
        return this;
    }

    public String getHost() {
        return mHost;
    }

    public MqttOptions setHost(String host) {
        mHost = host;
        return this;
    }

    public int getPort() {
        if (mPort == 0) {
            return MqttCore.DEFAULT_MQTT_PORT;
        }
        return mPort;
    }

    public MqttOptions setPort(int port) {
        mPort = port;
        return this;
    }

    public String getUserName() {
        return mUserName;
    }

    public MqttOptions setUserName(String userName) {
        mUserName = userName;
        return this;
    }

    public String getPassword() {
        return mPassword;
    }

    public MqttOptions setPassword(String password) {
        mPassword = password;
        return this;
    }

    public int getTimeout() {
        if (mTimeout == 0) {
            return MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT;
        }
        return mTimeout;
    }

    public MqttOptions setTimeout(int timeout) {
        mTimeout = timeout;
        return this;
    }

    public int getKeepAlive() {
        if (mKeepAlive == 0) {
            return MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT;
        }
        return mKeepAlive;
    }

    public MqttOptions setKeepAlive(int keepAlive) {
        mKeepAlive = keepAlive;
        return this;
    }

    public MqttOptions setSubscriptions(List<Subscription> subscriptions) {
        mSubscriptions = subscriptions;
        return this;
    }

    public List<Subscription> getSubscriptions() {
        return mSubscriptions;
    }

    @Override
    public String toString() {
        return "MqttOption{" +
                "mClientId='" + mClientId + '\'' +
                ", mHost='" + mHost + '\'' +
                ", mPort=" + mPort +
                ", mUserName='" + mUserName + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mTimeout=" + mTimeout +
                ", mKeepAlive=" + mKeepAlive +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mClientId);
        dest.writeString(mHost);
        dest.writeInt(mPort);
        dest.writeString(mUserName);
        dest.writeString(mPassword);
        dest.writeInt(mTimeout);
        dest.writeInt(mKeepAlive);
        dest.writeTypedList(mSubscriptions);
    }
}
