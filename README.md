# MQTTDemo

MQTT在Android上的使用

## 关于我

[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)   [![简书](https://img.shields.io/badge/简书-xuexiangjys-red.svg)](https://www.jianshu.com/u/6bf605575337)   [![掘金](https://img.shields.io/badge/掘金-xuexiangjys-brightgreen.svg)](https://juejin.im/user/598feef55188257d592e56ed)   [![知乎](https://img.shields.io/badge/知乎-xuexiangjys-violet.svg)](https://www.zhihu.com/people/xuexiangjys) 

## MQTT服务器

> 在运行Demo之前请先安装MQTT服务器，并在客户端中配置好服务器的地址和端口号。

* [ActiveMQ](http://activemq.apache.org/)

* [EMQ](https://www.emqx.io/)

## MQTT服务器配置

请在AndroidManifest.xml中修改你的服务器配置:

```
<meta-data
    android:name="MQTT_HOST"
    android:value="192.168.0.154" />
<meta-data
    android:name="MQTT_PORT"
    android:value="1883" />
```
