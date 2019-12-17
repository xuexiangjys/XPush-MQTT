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

package com.xuexiang.mqttdemo;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.multidex.MultiDex;

import com.xuexiang.mqttdemo.core.push.CustomPushReceiver;
import com.xuexiang.mqttdemo.utils.sdkinit.UMengInit;
import com.xuexiang.mqttdemo.utils.sdkinit.XBasicLibInit;
import com.xuexiang.xpush.XPush;
import com.xuexiang.xpush.core.dispatcher.impl.Android26PushDispatcherImpl;
import com.xuexiang.xpush.mqtt.MqttPushClient;
import com.xuexiang.xpush.mqtt.agent.MqttPersistence;
import com.xuexiang.xutil.system.DeviceUtils;

/**
 * @author xuexiang
 * @since 2018/11/7 下午1:12
 */
public class MyApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决4.x运行崩溃的问题
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLibs();
    }

    /**
     * 初始化基础库
     */
    private void initLibs() {
        XBasicLibInit.init(this);

        initXPush();

        //运营统计数据运行时不初始化
        if (!MyApp.isDebug()) {
            UMengInit.init(this);
        }
    }


    /**
     * @return 当前app是否是调试开发模式
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }


    public void initXPush() {
        XPush.debug(MyApp.isDebug());
        XPush.init(this, new MqttPushClient());
        //暂时设置登录的客户端ID为AndroidID
        MqttPersistence.setClientId(DeviceUtils.getAndroidID());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Android8.0静态广播注册失败解决方案二：修改发射器
            XPush.setIPushDispatcher(new Android26PushDispatcherImpl(CustomPushReceiver.class));
        }
        XPush.register();
    }
}
