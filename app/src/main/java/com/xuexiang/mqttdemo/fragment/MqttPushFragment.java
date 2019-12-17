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

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuexiang.mqttdemo.R;
import com.xuexiang.mqttdemo.core.BaseFragment;
import com.xuexiang.mqttdemo.utils.XToastUtils;
import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpush.XPush;
import com.xuexiang.xpush.core.XPushManager;
import com.xuexiang.xpush.core.queue.impl.MessageSubscriber;
import com.xuexiang.xpush.entity.CustomMessage;
import com.xuexiang.xpush.entity.Notification;
import com.xuexiang.xpush.entity.XPushCommand;
import com.xuexiang.xpush.util.PushUtils;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xutil.common.StringUtils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.xuexiang.mqttdemo.fragment.MqttPushFragment.LogType.SUCCESS;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_GET_ALIAS;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_GET_TAG;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_REGISTER;
import static com.xuexiang.xpush.core.annotation.CommandType.TYPE_UNREGISTER;

/**
 * @author xuexiang
 * @since 2019-12-15 23:18
 */
@Page(name = "MqttPush\n结合XPush进行消息的统一管理")
public class MqttPushFragment extends BaseFragment {

    @BindView(R.id.tv_push_platform)
    TextView tvPushPlatform;
    @BindView(R.id.tv_token)
    TextView tvToken;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.ll_status)
    LinearLayout llStatus;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_stop)
    Button btnStop;
    @BindView(R.id.et_tag)
    EditText etTag;
    @BindView(R.id.btn_add_tag)
    Button btnAddTag;
    @BindView(R.id.btn_delete_tag)
    Button btnDeleteTag;
    @BindView(R.id.btn_get_tag)
    Button btnGetTag;
    @BindView(R.id.ll_tag)
    LinearLayout llTag;
    @BindView(R.id.et_alias)
    EditText etAlias;
    @BindView(R.id.btn_bind_alias)
    Button btnBindAlias;
    @BindView(R.id.btn_unbind_alias)
    Button btnUnbindAlias;
    @BindView(R.id.btn_get_alias)
    Button btnGetAlias;
    @BindView(R.id.ll_alias)
    LinearLayout llAlias;

    @BindView(R.id.tv_log)
    TextView mTvLog;
    /**
     * 日志记录
     */
    private SpannableStringBuilder mLogSb = new SpannableStringBuilder();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mqtt_push;
    }

    @Override
    protected void initViews() {
        tvPushPlatform.setText(String.format("%s(%d)", XPush.getPlatformName(), XPush.getPlatformCode()));
        tvToken.setText(XPush.getPushToken());
        tvStatus.setText(PushUtils.formatConnectStatus(XPush.getConnectStatus()));

        mTvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        //MqttPush暂不支持别名操作
        llAlias.setVisibility(View.GONE);
    }


    @Override
    protected void initListeners() {
        XPushManager.get().register(mMessageSubscriber);
    }

    private MessageSubscriber mMessageSubscriber = new MessageSubscriber() {
        @MainThread
        @Override
        public void onMessageReceived(CustomMessage message) {
            addLog("消息内容:" + message.getMsg(), SUCCESS);
        }

        @MainThread
        @Override
        public void onNotification(Notification notification) {
        }

        @MainThread
        @Override
        public void onConnectStatusChanged(int connectStatus) {
            tvStatus.setText(PushUtils.formatConnectStatus(connectStatus));
        }

        @MainThread
        @Override
        public void onCommandResult(XPushCommand command) {
            if (!command.isSuccess()) {
                return;
            }
            switch (command.getType()) {
                case TYPE_REGISTER:
                    tvToken.setText(command.getContent());
                    break;
                case TYPE_UNREGISTER:
                    tvToken.setText("");
                    clearLog();
                    break;
                case TYPE_GET_TAG:
                    etTag.setText(command.getContent());
                    break;
                case TYPE_GET_ALIAS:
                    etAlias.setText(command.getContent());
                    break;
                default:
                    break;
            }
        }
    };

    @SingleClick
    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_add_tag, R.id.btn_delete_tag, R.id.btn_get_tag, R.id.btn_bind_alias, R.id.btn_unbind_alias, R.id.btn_get_alias})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                XPush.register();
                break;
            case R.id.btn_stop:
                XPush.unRegister();
                break;
            case R.id.btn_add_tag:
                addTags();
                break;
            case R.id.btn_delete_tag:
                deleteTags();
                break;
            case R.id.btn_get_tag:
                XPush.getTags();
                break;
            case R.id.btn_bind_alias:
                bindAlias();
                break;
            case R.id.btn_unbind_alias:
                unBindAlias();
                break;
            case R.id.btn_get_alias:
                XPush.getAlias();
                break;
            default:
                break;
        }
    }

    private void addTags() {
        String tag = etTag.getText().toString();
        if (StringUtils.isEmpty(tag)) {
            XToastUtils.toast("标签不能为空");
            return;
        }

        XPush.addTags(PushUtils.string2Array(tag));
    }

    private void deleteTags() {
        String tag = etTag.getText().toString();
        if (StringUtils.isEmpty(tag)) {
            XToastUtils.toast("标签不能为空");
            return;
        }

        XPush.deleteTags(PushUtils.string2Array(tag));
    }

    private void bindAlias() {
        String alias = etAlias.getText().toString();
        if (StringUtils.isEmpty(alias)) {
            XToastUtils.toast("别名不能为空");
            return;
        }

        XPush.bindAlias(alias);
    }

    private void unBindAlias() {
        String alias = etAlias.getText().toString();
        if (StringUtils.isEmpty(alias)) {
            XToastUtils.toast("别名不能为空");
            return;
        }

        XPush.unBindAlias(alias);
    }

    @Override
    public void onDestroyView() {
        XPushManager.get().unregister(mMessageSubscriber);
        super.onDestroyView();
    }

    //=================日志====================//

    /**
     * 添加日志
     *
     * @param logContent
     * @param logType
     */
    private void addLog(String logContent, LogType logType) {
        SpannableString spannableString = new SpannableString(logContent);
        switch (logType) {
            case NORMAL:
                break;
            case ERROR:
                spannableString.setSpan(new ForegroundColorSpan(ResUtils.getColor(R.color.xui_config_color_edittext_error_text)),
                        0,
                        logContent.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case SUCCESS:
                spannableString.setSpan(new ForegroundColorSpan(ResUtils.getColor(R.color.color_bg_success)),
                        0,
                        logContent.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            default:
                break;
        }
        updateLog(spannableString);
    }

    @MainThread
    protected void updateLog(SpannableString spannableString) {
        mLogSb.append(spannableString)
                .append("\r\n");
        mTvLog.setText(mLogSb);
    }

    @MainThread
    private void clearLog() {
        mLogSb.delete(0, mLogSb.length());
        mTvLog.setText(mLogSb);
    }

    public enum LogType {
        /**
         * 普通日志
         */
        NORMAL,
        /**
         * 成功的日志
         */
        SUCCESS,
        /**
         * 出错的日志
         */
        ERROR
    }

}
