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

package com.xuexiang.mqttdemo.fragment;

import android.view.View;
import android.widget.Button;

import com.xuexiang.mqttdemo.R;
import com.xuexiang.mqttdemo.core.BaseFragment;
import com.xuexiang.mqttdemo.core.mqtt.entity.MqttSetting;
import com.xuexiang.mqttdemo.utils.MMKVUtils;
import com.xuexiang.mqttdemo.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpush.mqtt.core.ConnectionStatus;
import com.xuexiang.xpush.mqtt.core.MqttCore;
import com.xuexiang.xpush.mqtt.core.MqttEventListenerAdapter;
import com.xuexiang.xpush.mqtt.core.Subscription;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author xuexiang
 * @since 2019-12-12 23:53
 */
@Page(name = "MQTT详细操作")
public class OperationFragment extends BaseFragment {

    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.btn_disconnect)
    Button btnDisconnect;

    private MqttCore mMqttCore;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_operation;
    }

    @Override
    protected void initViews() {
        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);

    }

    @OnClick({R.id.btn_connect, R.id.btn_disconnect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                doConnect();
                break;
            case R.id.btn_disconnect:
                doDisConnect();
                break;
            default:
                break;
        }
    }

    private void doConnect() {
        MqttSetting setting = MMKVUtils.getObject(MqttSetting.KEY, MqttSetting.class);
        if (setting == null) {
            XToastUtils.error("请先配置MQTT连接设置");
            openPage(SettingFragment.class);
            return;
        }

        if (mMqttCore == null) {
            mMqttCore = MqttCore.Builder(getContext(), setting.getHost())
                    .setClientId(setting.getClientId())
                    .setPort(setting.getPort())
                    .setUserName(setting.getUserName())
                    .setPassWord(setting.getPassword())
                    .setTimeout(setting.getTimeout())
                    .setKeepAlive(setting.getKeepAlive())
                    .build();
            mMqttCore.setOnMqttEventListener(new MqttEventListenerAdapter() {
                @Override
                public void onMessageReceived(String topic, MqttMessage message) {
                    XToastUtils.info("收到 [topic]:" + topic + "[message]:" + message.toString());
                }

                @Override
                public void onConnectionStatusChanged(ConnectionStatus oldStatus, ConnectionStatus newStatus) {
                    super.onConnectionStatusChanged(oldStatus, newStatus);
                    if (newStatus == ConnectionStatus.CONNECTED) {
                        btnConnect.setEnabled(false);
                        btnDisconnect.setEnabled(true);
                    } else {
                        btnConnect.setEnabled(true);
                        btnDisconnect.setEnabled(false);
                    }
                }
            });

            mMqttCore.setSubscriptions(new Subscription("TestTopic"));
        }

        mMqttCore.connect();
    }

    private void doDisConnect() {
        if (mMqttCore != null && mMqttCore.isConnected()) {
            mMqttCore.disconnect();
        }
    }


    @Override
    public void onDestroyView() {
        if (mMqttCore != null) {
            mMqttCore.close();
        }
        super.onDestroyView();
    }
}
