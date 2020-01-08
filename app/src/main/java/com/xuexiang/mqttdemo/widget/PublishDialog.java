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

package com.xuexiang.mqttdemo.widget;

import android.content.Context;

import com.xuexiang.mqttdemo.ActionRequest;
import com.xuexiang.mqttdemo.R;
import com.xuexiang.xpush.mqtt.core.entity.PublishMessage;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.CustomMaterialDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;

import static com.xuexiang.mqttdemo.fragment.OperationFragment.TOPIC_PROTOBUF;

/**
 * @author xuexiang
 * @since 2019-12-15 23:29
 */
public class PublishDialog extends CustomMaterialDialog {

    private MaterialEditText metTopic;
    private MaterialEditText metMessage;

    private OnPublishListener mListener;

    /**
     * 构造窗体
     *
     * @param context
     */
    public PublishDialog(Context context, OnPublishListener listener) {
        super(context);
        mListener = listener;
    }

    @Override
    protected MaterialDialog.Builder getDialogBuilder(Context context) {
        return new MaterialDialog.Builder(context)
                .title("发布主题")
                .customView(R.layout.dialog_publish, false)
                .positiveText("发送")
                .onPositive((dialog, which) -> {
                    if (metTopic.validate() && metMessage.validate()) {
                        KeyboardUtils.forceCloseKeyboard(metMessage);
                        if (mListener != null) {
                            String topic = metTopic.getEditValue();
                            if (TOPIC_PROTOBUF.equals(topic)) {
                                ActionRequest actionRequest = ActionRequest.newBuilder().setMessage(metMessage.getEditValue()).build();
                                mListener.onPublish(PublishMessage.wrap(topic, actionRequest.toByteArray()));
                            } else {
                                mListener.onPublish(PublishMessage.wrap(topic, metMessage.getEditValue()));
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .negativeText("取消")
                .onNegative((dialog, which) -> dialog.dismiss())
                .autoDismiss(false)
                .cancelable(false);
    }


    @Override
    protected void initViews(Context context) {
        metTopic = findViewById(R.id.met_topic);
        metMessage = findViewById(R.id.met_message);
    }


    public interface OnPublishListener {

        void onPublish(PublishMessage message);
    }
}
