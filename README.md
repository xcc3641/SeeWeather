# 就看天气
[![Build Status](https://travis-ci.org/xcc3641/SeeWeather.svg?branch=master)](https://travis-ci.org/xcc3641/SeeWeather)

就看天气该应用就是如同它的名字一样，只做一个单纯、简单的看天气软件。这么多天气软件，你选择了我，这是我的幸运。

从15年10月上线，到目前经历两次重大改版，一次代码的重构，一次界面的大幅度改动，目的都是为了你们。
在开源的过程中，收到了很多来自有趣的你们的邮件。我也曾遇到过棘手的问题无处咨询又谷歌不到。那个时候的我，也可能是现在的你。所以我希望能够帮助到你。

----

### 简介
就看天气——是一款遵循 **Material Design** 风格的只看天气的APP。
- 卡片展现（当前天气情况，未来几小时天气情况，生活建议，一周七天概况）
- 缓存数据，减少网络请求，保证离线查看
- 内置两套图标（设置里更改）


----

权限说明

```xml
	<!--用于进行网络定位-->
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
	<!--用于访问GPS定位-->
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
	<!--获取运营商信息，用于支持提供运营商信息相关的接口-->
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	<!--用于访问wifi网络信息，wifi信息会用于进行网络定位-->
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	<!--这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
	<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
	<!--用于访问网络，网络定位需要上网-->
	<uses-permission android:name="android.permission.INTERNET"/>
	<!--用于读取手机当前的状态-->
	<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
	<!--写入扩展存储，向扩展卡写入数据，用于写入缓存定位数据-->
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

```

### 下载地址

Fir.im: http://fir.im/seeWeather

酷安市场：http://www.coolapk.com/apk/com.xiecc.seeWeather

豌豆荚：http://www.wandoujia.com/apps/com.xiecc.seeWeather

[更新日志](changeLog.md)


### TODO

- [ ] 桌面小部件
- [x] 通知栏提醒
- [x] 更好，更多的天气ICONS
- [x] 管理城市（多城市选择）
- [x] 自动定位
- [ ] 自由定制的Item界面
- [ ] 引导页面


----

### 项目
#### 公开 API

天气数据来源于：和风天气

城市信息来源于：CSDN

地理定位服务： 高德地图

#### 开源技术
1. [Rxjava][2]
2. [RxAndroid][3]
3. [Retrofit][4]
4. [GLide][5]


### 截图

![][image-2]
![][image-3]

### 感谢
感谢开源，学习到了前辈们优秀的代码：
- [@张鸿洋][7]
- [@扔物线][8]
- [@drakeet][9]
- [@代码家][10]
- [@程序亦非猿][11]
- [@小鄧子][12]
- [@Jude95][13]
- [@泡在网上编代码][14]

感谢优秀的设计师提供素材：
- 多城市卡片 by [YujunZhu](http://yujunzhu.zcool.com.cn/)

### 关于作者

谢三弟

![][image-4]

- [简书](http://www.jianshu.com/users/3372b4a3b9e5/latest_articles)
- [个人博客](imxie.itscoder.com)
- [知乎](https://www.zhihu.com/people/xcc3641.github.io)

### License

    Copyright 2017 HugoXie Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

__软件中图片素材均来源于网络，版权属于原作者。__






[1]: https://www.zhihu.com/question/26417244/answer/70193822
[2]: https://github.com/ReactiveX/RxJava
[3]: https://github.com/ReactiveX/RxAndroid
[4]: https://github.com/square/retrofit
[5]: https://github.com/bumptech/glide
[6]: https://github.com/yangfuhai/ASimpleCache
[7]: https://github.com/hongyangAndroid
[8]: https://github.com/rengwuxian
[9]: https://github.com/drakeet
[10]: https://github.com/daimajia
[11]: https://github.com/AlanCheen
[12]: https://github.com/SmartDengg
[13]: https://github.com/Jude95
[14]: http://weibo.com/u/2711441293?topnav=1&amp;wvr=6&amp;topsug=1&amp;is_all=1


[image-2]: /images/day.png
[image-3]: /images/night.png
[image-4]: http://xcc3641.qiniudn.com/app-%E5%A4%B4%E5%83%8F-1.jpeg
[image-5]: http://xcc3641.qiniudn.com/app-%E6%94%AF%E4%BB%98%E5%AE%9D.jpg
