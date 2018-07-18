package com.liuchaoya.jartest;

import android.app.Application;
import android.content.Context;

/**
 * @classDescription: 描述：
 * @author: LiuChaoya
 * @createTime: 2018/7/17 0017 13:53.
 * @email: 1090969255@qq.com
 */
public class App extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
