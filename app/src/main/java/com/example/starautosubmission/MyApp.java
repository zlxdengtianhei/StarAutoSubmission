package com.example.starautosubmission;

import android.app.Application;
import cn.bmob.v3.Bmob;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 Bmob SDK
        Bmob.initialize(this, "3f79b270a67de5fb0381d880c3c89e51");
    }
}