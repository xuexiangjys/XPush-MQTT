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

import com.xuexiang.mqttdemo.R;
import com.xuexiang.mqttdemo.core.BaseFragment;
import com.xuexiang.mqttdemo.core.mqtt.entity.MqttSetting;
import com.xuexiang.mqttdemo.utils.MMKVUtils;
import com.xuexiang.mqttdemo.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xutil.common.StringUtils;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

/**
 * @author xuexiang
 * @since 2019-07-08 00:52
 */
@Page(name = "MQTT连接设置")
public class SettingFragment extends BaseFragment {

    @BindView(R.id.met_client_id)
    MaterialEditText metClientId;
    @BindView(R.id.met_host)
    MaterialEditText metHost;
    @BindView(R.id.met_port)
    MaterialEditText metPort;
    @BindView(R.id.met_username)
    MaterialEditText metUsername;
    @BindView(R.id.met_password)
    MaterialEditText metPassword;
    @BindView(R.id.met_timeout)
    MaterialEditText metTimeout;
    @BindView(R.id.met_keep_alive)
    MaterialEditText metKeepAlive;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle();
        titleBar.addAction(new TitleBar.TextAction("保存") {
            @SingleClick
            @Override
            public void performAction(View view) {
                doSaveSetting();
            }
        });
        return titleBar;
    }

    /**
     * 保存设置
     */
    private void doSaveSetting() {
        if (metHost.validate()) {
            MqttSetting setting = new MqttSetting(metHost.getEditValue());
            setting.setClientId(metClientId.getEditValue());
            setting.setPort(StringUtils.toInt(metPort.getEditValue()));
            setting.setUserName(metUsername.getEditValue());
            setting.setPassword(metPassword.getEditValue());
            setting.setTimeout(StringUtils.toInt(metTimeout.getEditValue()));
            setting.setKeepAlive(StringUtils.toInt(metKeepAlive.getEditValue()));
            if (MMKVUtils.put(MqttSetting.KEY, setting)) {
                XToastUtils.success("保存成功");
                setFragmentResult(RESULT_OK, null);
                popToBack();
            }
        }
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        MqttSetting setting = MMKVUtils.getObject(MqttSetting.KEY, MqttSetting.class);
        if (setting == null) {
            setting = new MqttSetting("");
        }

        metClientId.setText(StringUtils.getString(setting.getClientId()));
        metHost.setText(StringUtils.getString(setting.getHost()));
        metPort.setText(String.valueOf(setting.getPort()));
        metUsername.setText(StringUtils.getString(setting.getUserName()));
        metPassword.setText(StringUtils.getString(setting.getPassword()));
        metTimeout.setText(String.valueOf(setting.getTimeout()));
        metKeepAlive.setText(String.valueOf(setting.getKeepAlive()));
    }


}
