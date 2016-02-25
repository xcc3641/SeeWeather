# SeeWeather

[中文版](https://github.com/xcc3641/SeeWeather/blob/master/README.md)丨[English](https://github.com/xcc3641/SeeWeather/blob/master/README_EN.md)


### Foreword

SeeWeather is my first relatively mature application,going on-line in the October 2015.And then I answered this question [how to learn Android by oneself](https://www.zhihu.com/question/26417244/answer/70193822) in the ZhiHu platform

Because my code was poorly written, I decided to refactor the whole project and made a new look to show SeeWeather Version 2.0.

If you find any problems and suggestion,you can e-mail me or open issues at any time.


**Attention**

**If you want to pull the project to run, you should apply for API at [和风天气](http://www.heweather.com/) and add it in Setting.java**

**If the application crashes, pleace e-mail the log(`/Android/data/com.xiecc.seeWeather/cache/Log`) to me**

- **open-source project is valuable, hope to give me a star for support**


### Brief introduction
SeeWeather is an following ** Material Design** Weather application.Use	the least Permission to do the best with the least experience.

- CardView Display(Current weather conditions,The next few hours the weather conditions,Life suggestion,A week overview)
- Cache data, reduce network requests, ensure that offline viewing
- Built-in two sets of icons
- Automatic positioning
- Automatic night mode


- - -

Permission Description


```
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

### Updated version && Download URL

Fir.im: http://fir.im/seeWeather

Wandoujia：http://www.wandoujia.com/apps/com.xiecc.seeWeather

Flyme： http://developer.meizu.com/console/apps/detail/6530883

Kuan：http://www.coolapk.com/apk/com.xiecc.seeWeather



### TODO
I am a little busy in this semester,preparing for the job interview, but I will still complete to-do.
- [ ] Widget
- [ ] Notification
- [ ] Prettier Weather Icons
- [ ] Multi-City Selection
- [x] ~~Automatic Positioning~~
- [ ] DIY Items Interface


- - -

### Project
#### Open API

Weather Data：和风天气

City Data：CSDN

City Orientation： 高德地图

#### Open-source project
1. [Rxjava](https://github.com/ReactiveX/RxJava)
2. [RxAndroid](https://github.com/ReactiveX/RxAndroid)
3. [Retrofit](https://github.com/square/retrofit)
4. [GLide](https://github.com/bumptech/glide)
5. [ASimpleCache](https://github.com/yangfuhai/ASimpleCache)


- - -


### Screenshot
![](http://xcc3641.qiniudn.com/app-%E5%B0%B1%E7%9C%8B%E5%A4%A9%E6%B0%94-%E9%97%AA%E5%B1%8F.png)
![](http://xcc3641.qiniudn.com/app-%E5%B0%B1%E7%9C%8B%E5%A4%A9%E6%B0%94-%E7%99%BD%E5%A4%A9%E6%A8%A1%E5%BC%8F.png)
![](http://xcc3641.qiniudn.com/app-%E5%B0%B1%E7%9C%8B%E5%A4%A9%E6%B0%94-%E5%A4%9C%E6%99%9A%E6%A8%A1%E5%BC%8F.png)

### Appreciation
- [@张鸿洋](https://github.com/hongyangAndroid)
- [@扔物线](https://github.com/rengwuxian)
- [@drakeet](https://github.com/drakeet)
- [@代码家](https://github.com/daimajia)
- [@程序亦非猿](https://github.com/AlanCheen)
- [@小鄧子](https://github.com/SmartDengg)
- [@Jude95](https://github.com/Jude95)
- [@泡在网上编代码](http://weibo.com/u/2711441293?topnav=1&wvr=6&topsug=1&is_all=1)

### About Me

![](http://xcc3641.qiniudn.com/app-%E5%A4%B4%E5%83%8F-1.jpeg)
 
JianShu：http://www.jianshu.com/users/3372b4a3b9e5/latest_articles
 
ZhiHu：https://www.zhihu.com/people/xcc3641.github.io
 
WeiBo：http://weibo.com/xcc3641
 
Blog： http://IMXIE.CC

### Buy me a cup of coffee

_ _ _

![](http://xcc3641.qiniudn.com/app-%E6%94%AF%E4%BB%98%E5%AE%9D.jpg)
_ _ _

### LICENSE

Copyright 2016 HugoXie  Licensed under the Apache License, Version 2.0 (the \"License\")
        you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
        Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


** Pictures from the network, the copyright belongs to the original author.**

