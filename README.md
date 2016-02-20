# 就看天气

_ _ _

### 前言
最初上线是在2015年10月，是自己第一个较为成熟的应用，开发完之后刚好答了知乎这篇从零到就看天气上线，但是因为代码确实写的很烂，所以决定全部重构代码全新风格的展示就看天气Ver2.0.当然自己也在学习之中，如果发现有任何问题，随时欢迎Email或者开Issues
**开源不易，希望能给个Star鼓励**
项目地址：https://github.com/xcc3641/SeeWeather
项目主页发布issue: https://github.com/xcc3641/SeeWeather/issues
本项目为开源项目,技术交流可以通过邮箱联系: Hugo3641@gmail.com

### 简介
就看天气——是一款遵循**Material Design**风格的只看天气的APP。无流氓权限，无自启，xxx，用最少的权限做最优的体验。
- 卡片展现（当前天气情况，未来几小时天气情况，生活建议，一周七天概况）
- 彩蛋（自动夜间状态）
- 补全城市（第一版本因为自己偷懒所以城市有缺陷对不起各位）
- 缓存数据，减少网络请求，保证离线查看
- 内置两套图标（设置里更改）

### 版本更新&&下载地址
Fir.im:
豌豆荚：
魅族应用中心：

v2.0
- 重构代码，全新UI


v1.1
- d


v1.0
- d


### TODO
- [ ] 桌面小部件
- [ ] 更好，更多的天气ICONS
- [ ] 管理城市（多城市选择）
- [ ] 自动定位
- [ ] 自由定制的Item界面



_ _ _

### 项目
#### 公开 API
天气数据来源于：和风天气
城市信息来源于：CSDN

#### 开源技术
1. [Rxjava](https://github.com/ReactiveX/RxJava)
2. [RxAndroid](https://github.com/ReactiveX/RxAndroid)
3. [Retrofit](https://github.com/square/retrofit)
4. [GLide](https://github.com/bumptech/glide)
5. [ASimpleCache](https://github.com/yangfuhai/ASimpleCache)

#### 简单介绍代码

##### 网络
就看天气的网络部分的支持是用`RxJava+RxAndroid+Retrofit+Gson`再加上`ACache`缓存
```
**
     * <p/>
     * 首先从本地缓存获取数据
     * if 有
     * 更新UI
     * else
     * 直接进行网络请求，更新UI并保存在本地
     */
    private void fetchData() {
        observer = new Observer<Weather>() {
                        //节约篇幅，已省略
                        ...
        };

        fetchDataByCache(observer);
        fetchDataByNetWork(observer);
    }


    /**
     * 从本地获取
     */
    private void fetchDataByCache(Observer<Weather> observer) {
        Weather weather = null;
        try {
            weather = (Weather) aCache.getAsObject("WeatherData");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (weather != null) {
        //distinct去重
            Observable.just(weather).distinct().subscribe(observer);
        }
    }


    /**
     * 从网络获取
     */
    private void fetchDataByNetWork(Observer<Weather> observer) {
        String cityName = mSetting.getString(Setting.CITY_NAME, "重庆");
        RetrofitSingleton.getApiService(this)
                         .mWeatherAPI(cityName, key)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
						////节约篇幅，已省略
                        ...
    }
```
##### RecycerVIew展示
就像洪洋说的一样
> 整体上看RecyclerView架构，提供了一种插拔式的体验，高度的解耦，异常的灵活，通过设置它提供的不同LayoutManager，ItemDecoration , ItemAnimator实现令人瞠目的效果。

该项目中用到RecyclerView中级的用法是根据itemType展示不同的布局，这就是主页核心的代码了。
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

        if (holder instanceof NowWeatherViewHolder) {
        //更新布局
        ....
        }
}

```



_ _ _


#### 截图


#### 感谢
感谢开源，学习到了前辈们优秀的代码
[@张鸿洋](https://github.com/hongyangAndroid)
[@扔物线](https://github.com/rengwuxian)
[@drakeet](https://github.com/drakeet)
[@代码家](https://github.com/daimajia)
[@程序亦非猿](https://github.com/AlanCheen)
[@小鄧子](https://github.com/SmartDengg)

#### 关于作者
![](http://)
简书：http://www.jianshu.com/users/3372b4a3b9e5/latest_articles
知乎：https://www.zhihu.com/people/xcc3641.github.io
微博：http://weibo.com/xcc3641
个人博客： http://IMXIE.CC


#### 请我喝杯咖啡

_ _ _

![](http://7lrzxl.com1.z0.glb.clouddn.com/blog-zhifubao.jpg)
_ _ _

#### LICENSE

Copyright 2016 HugoXie  Licensed under the Apache License, Version 2.0 (the \"License\")
        you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
        Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

图片来源于网络，版权属于原作者。






