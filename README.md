# 就看天气

就看天气该应用就是如同它的名字一样，只做一个单纯、简单的看天气软件。这么多天气软件，你选择了我，这是我的幸运。


从15年10月上线，到目前经历两次重大改版，一次代码的重构，一次界面的大幅度改动，目的都是为了你们。
在开源的过程中，收到了很多来自有趣的你们的邮件。我也曾遇到过棘手的问题无处咨询又谷歌不到。那个时候的我，也可能是现在的你。所以我希望能够帮助到你。

- **开源不易，希望能给个Star鼓励**
- 项目地址：https://github.com/xcc3641/SeeWeather
- 项目主页发布issue: https://github.com/xcc3641/SeeWeather/issues
- 本项目为开源项目,技术交流可以通过邮箱联系: Hugo3641@gmail.com


----

### 简介
就看天气——是一款遵循**Material Design**风格的只看天气的APP。无流氓权限，无自启，xxx，用最少的权限做最优的体验。
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

### 版本更新&&下载地址

Fir.im: http://fir.im/seeWeather

酷安市场：http://www.coolapk.com/apk/com.xiecc.seeWeather

豌豆荚：http://www.wandoujia.com/apps/com.xiecc.seeWeather

v2.2
- 新增 多城市管理
- 新增 Bug反馈和意见
- 改版 首页UI 更丰富
- 美化 关于页面
- 美化 部分图标

v2.1
- 修复 定位逻辑
- 优化 SP的统一化
- 优化 Error界面
- 更新 新的天气 ICON
- 更新 通知栏提醒（后台可能需要白名单，不然无法自动更新）
- 设置 自动更新频率（0 为不自动更新）


v2.0
- 重构代码，全新UI，升级体验
- 就看天气——是一款遵循**Material Design**风格的只看天气的APP。无流氓权限，无自启，xxx，用最少的权限做最优的体验。
- 卡片展现（当前天气情况，未来几小时天气情况，生活建议，一周七天概况）
- 彩蛋（自动夜间状态）
- 补全城市（第一版本因为自己偷懒所以城市有缺陷对不起各位）
- 缓存数据，减少网络请求，保证离线查看
- 内置两套图标（设置里更改）





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
5. ~~[ASimpleCache][6]~~

#### 代码

##### 网络

Update 7.11:

因为天气软件请求比较单一，没必要用其他的缓存，可以直接用 okhttp 缓存。

```java
File cacheFile = new File(BaseApplication.getmAppContext().getExternalCacheDir(), "SeeWeatherCache");
Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
Interceptor cacheInterceptor = chain -> {
	Request request = chain.request();
	if (!Util.isNetworkConnected(BaseApplication.getmAppContext())) {
		request = request.newBuilder()
			.cacheControl(CacheControl.FORCE_CACHE)
			.build();
	}
	Response response = chain.proceed(request);
	if (Util.isNetworkConnected(BaseApplication.getmAppContext())) {
		int maxAge = 0;
		// 有网络时 设置缓存超时时间0个小时
		response.newBuilder()
			.header("Cache-Control", "public, max-age=" + maxAge)
			.build();
	} else {
		// 无网络时，设置超时为4周
		int maxStale = 60 * 60 * 24 * 28;
		response.newBuilder()
			.header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
			.build();
	}
	return response;
};
builder.cache(cache).addInterceptor(cacheInterceptor);
```
设置好缓存地址，设置好 header 即可实现缓存。




网络部分：
配合 RetrofitSingleton 中封装的方法：
```java
public Observable<Weather> fetchWeather(String city) {
		return apiService.mWeatherAPI(city, C.KEY)
				.filter(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0).status.equals("ok"))
				.map(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0))
				.compose(RxUtils.rxSchedulerHelper());
}
```

RxUtils 工具类中封装了线程调度：

```java
public static <T> Observable.Transformer<T, T> rxSchedulerHelper() {
		return tObservable -> tObservable.subscribeOn(Schedulers.io())
				.unsubscribeOn(AndroidSchedulers.mainThread())
				.observeOn(AndroidSchedulers.mainThread());
}
```

感受下实际操作

```java
    private Observable<Weather> fetchDataByNetWork() {
        String cityName = Util.replaceCity(mSharedPreferenceUtils.getCityName());
        return RetrofitSingleton.getInstance()
            .fetchWeather(cityName)
            .onErrorReturn(throwable -> {
                PLog.e(throwable.getMessage());
                return null;
            });
    }
```

一定要写 onErrorRrturn ，不然是不会执行到缓存的流。

因为和风天气 API 有些城市/省份的末尾的特殊符号需要过滤掉，比如 `省|市|自治区|特别行政区|地区|盟`,所以在我的方法类中写了一个安全的替换方法。

缓存部分：
```java
private Observable<Weather> fetchDataByCache() {
		return Observable.defer(() -> {
						Weather weather = (Weather) aCache.getAsObject(C.WEATHER_CACHE);
						return Observable.just(weather);
				}
		).compose(RxUtils.rxSchedulerHelper());
}
```

使用 concat 连接：
优先网络数据

```java
private void load() {
        Observable.concat(fetchDataByNetWork(), fetchDataByCache())
            .first(weather -> weather != null)
            .doOnError(throwable -> {
                mErroImageView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            })
            .doOnNext(weather -> {
                mErroImageView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            })
            .doOnTerminate(() -> {
                mRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
            })
            .subscribe(observer);
    }
```


##### RxBus

具体的逻辑分析在这里 [RxBus 的简单实现](http://brucezz.github.io/articles/2016/06/02/a-simple-rxbus-implementation/)

深入分析理解可以看我的这篇文章:[从 RxBus 这辆兰博基尼深入进去](http://imxie.cc/2016/06/02/deep-understanding-of-RxBus/)

感谢 Brucezz

##### RecyclerView 展示

就像洪洋说的一样

> 整体上看RecyclerView架构，提供了一种插拔式的体验，高度的解耦，异常的灵活，通过设置它提供的不同LayoutManager，ItemDecoration , ItemAnimator实现令人瞠目的效果。

该项目中用到 RecyclerView 中的用法是根据 itemType 展示不同的布局，这就是主页 UI 核心的代码了。

```
@Override public int getItemViewType(int position) {
	    if (position == TYPE_ONE) {
	    //标识
	        ...
	    }
	    return super.getItemViewType(position);
	}

@Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	    if (viewType == TYPE_ONE) {
	    //绑定
	        ...
	        }
	    }
   }

@Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
	    //更新布局
	    ....

}

```



----


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
- [个人博客](http://imxie.cc/)
- [知乎](https://www.zhihu.com/people/xcc3641.github.io)


### 请我喝杯咖啡

----

![][image-5]


### LICENSE

```License
Copyright 2016 HugoXie  
Licensed under the Apache License, Version 2.0 (the \"License\")

you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
```



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
