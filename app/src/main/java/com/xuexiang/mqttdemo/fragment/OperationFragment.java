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

import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.mqttdemo.R;
import com.xuexiang.mqttdemo.core.BaseFragment;
import com.xuexiang.mqttdemo.core.mqtt.entity.MqttSetting;
import com.xuexiang.mqttdemo.utils.MMKVUtils;
import com.xuexiang.mqttdemo.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpush.mqtt.core.ConnectionStatus;
import com.xuexiang.xpush.mqtt.core.MqttAction;
import com.xuexiang.xpush.mqtt.core.MqttCore;
import com.xuexiang.xpush.mqtt.core.MqttEventListenerAdapter;
import com.xuexiang.xpush.mqtt.core.OnMqttActionListener;
import com.xuexiang.xpush.mqtt.core.Subscription;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.dialog.strategy.InputInfo;
import com.xuexiang.xutil.common.StringUtils;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * @author xuexiang
 * @since 2019-12-12 23:53
 */
@Page(name = "MQTT详细操作")
public class OperationFragment extends BaseFragment implements RecyclerViewHolder.OnItemClickListener<Subscription> {
    private static final int REQUEST_CODE_SETTING = 1000;

    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.btn_disconnect)
    Button btnDisconnect;
    @BindView(R.id.btn_subscribe)
    Button btnSubscribe;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private MqttCore mMqttCore;
    private MqttSetting mMqttSetting;

    private BaseRecyclerAdapter<Subscription> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_operation;
    }

    @Override
    protected void initArgs() {
        mMqttSetting = MMKVUtils.getObject(MqttSetting.KEY, MqttSetting.class);
    }

    @Override
    protected void initViews() {
        WidgetUtils.initRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Subscription>() {
            @Override
            protected int getItemLayoutId(int viewType) {
                return android.R.layout.simple_list_item_1;
            }

            @Override
            protected void bindData(@NonNull RecyclerViewHolder holder, int position, Subscription item) {
                holder.text(android.R.id.text1, item.getTopic());
            }
        });
        refreshConnectionStatus(false);
    }

    @Override
    protected void initListeners() {
        mAdapter.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(View itemView, Subscription item, int position) {
        DialogLoader.getInstance().showConfirmDialog(getContext(), "是否取消订阅 [" + item.getTopic() + "] ？", "是", (dialog, which) -> {
            dialog.dismiss();
            if (mMqttCore != null) {
                mMqttCore.unregisterSubscription(item);
            }
        }, "否");
    }

    private void refreshConnectionStatus(boolean isConnected) {
        if (isConnected) {
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnSubscribe.setEnabled(true);
            mAdapter.refresh(mMqttCore.getSubscriptionList());
        } else {
            btnConnect.setEnabled(true);
            btnDisconnect.setEnabled(false);
            btnSubscribe.setEnabled(false);
            mAdapter.clear();
        }
    }

    @OnClick({R.id.btn_connect, R.id.btn_disconnect, R.id.btn_subscribe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                doConnect();
                break;
            case R.id.btn_disconnect:
                doDisConnect();
                break;
            case R.id.btn_subscribe:
                doSubscribe();
                break;
            default:
                break;
        }
    }

    private void doConnect() {
        if (mMqttSetting == null) {
            XToastUtils.error("请先配置MQTT连接设置");
            openPageForResult(SettingFragment.class, REQUEST_CODE_SETTING);
            return;
        }

        if (mMqttCore == null) {
            mMqttCore = MqttCore.Builder(getContext(), mMqttSetting.getHost())
                    .setClientId(mMqttSetting.getClientId())
                    .setPort(mMqttSetting.getPort())
                    .setUserName(mMqttSetting.getUserName())
                    .setPassWord(mMqttSetting.getPassword())
                    .setTimeout(mMqttSetting.getTimeout())
                    .setKeepAlive(mMqttSetting.getKeepAlive())
                    .build();
            //订阅信息
            mMqttCore.setSubscriptions(mMqttSetting.getSubscriptions());
            //动作回调
            mMqttCore.setOnMqttActionListener(mOnMqttActionListener);
            //事件回调
            mMqttCore.setOnMqttEventListener(new MqttEventListenerAdapter() {
                @Override
                public void onMessageReceived(String topic, MqttMessage message) {
                    XToastUtils.info("收到 [topic]:" + topic + "[message]:" + message.toString());
                }

                @Override
                public void onConnectionStatusChanged(ConnectionStatus oldStatus, ConnectionStatus newStatus) {
                    super.onConnectionStatusChanged(oldStatus, newStatus);
                    refreshConnectionStatus(newStatus == ConnectionStatus.CONNECTED);
                }
            });
        }
        if (!mMqttCore.isConnected()) {
            mMqttCore.connect();
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SETTING) {
            mMqttSetting = MMKVUtils.getObject(MqttSetting.KEY, MqttSetting.class);
        }
    }

    /**
     * 断开
     */
    private void doDisConnect() {
        if (mMqttCore != null && mMqttCore.isConnected()) {
            mMqttCore.disconnect();
        }
    }


    /**
     * 订阅
     */
    private void doSubscribe() {
        DialogLoader.getInstance().showInputDialog(getContext(),
                -1,
                "订阅主题",
                null,
                new InputInfo(InputType.TYPE_CLASS_TEXT, "请输入订阅的主题"),
                null,
                "订阅",
                (dialog, which) -> {
                    if (dialog instanceof MaterialDialog) {
                        String topic = ((MaterialDialog) dialog).getInputEditText().getText().toString();
                        if (StringUtils.isEmpty(topic)) {
                            XToastUtils.error("主题不能为空！");
                            return;
                        }
                        dialog.dismiss();
                        if (mMqttCore != null) {
                            mMqttCore.registerSubscription(topic);
                        }
                    }
                },
                "取消",
                null);
    }

    private OnMqttActionListener mOnMqttActionListener = new OnMqttActionListener() {
        @Override
        public void onActionSuccess(MqttAction action, IMqttToken actionToken) {
            if (action.getArgs() != null) {
                XToastUtils.success("[" + action.getArgs() + "]" + action.getName() + "成功");
                switch (action) {
                    case SUBSCRIBE:
                    case UNSUBSCRIBE:
                        mMqttSetting.setSubscriptions(mMqttCore.getSubscriptionList());
                        //更新订阅信息
                        MMKVUtils.put(MqttSetting.KEY, mMqttSetting);
                        mAdapter.refresh(mMqttCore.getSubscriptionList());
                        break;
                    default:
                        break;
                }
            } else {
                XToastUtils.success(action.getName() + "成功");
            }
        }

        @Override
        public void onActionFailure(MqttAction action, IMqttToken actionToken, Throwable exception) {
            XToastUtils.error(exception);
        }
    };

    @Override
    public void onDestroyView() {
        //资源回收
        if (mMqttCore != null) {
            mMqttCore.close();
        }
        super.onDestroyView();
    }

}
