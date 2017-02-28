package com.ltx.zc;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.ltx.zc.utils.BitmapLruCache;
import com.ltx.zc.utils.CrashHandler;

/**
 * Created by Administrator on 2017-02-20.
 */

public class ZCApplication extends Application {
    private final String LogTag = "ZCProject-ZCApplication";
    private static ZCApplication instance;
    /**
     * 全局统一一个实例，节省资源
     */
    private RequestQueue mRequestQueue;
    /**
     * mImageLoader: 网络加载图片并缓存
     */
    private ImageLoader mImageLoader;
    private String dirPATH = "zc_download";
    /**
     * downloadId: 更新下载时的任务ID号
     */
    public long downloadId = -1;
    /**
     * isNeedUpdate: 是否需要升级标记
     */
    public static boolean isNeedUpdate = false;
    public static String lastTime;
    private static final String tag = "ZCApplication";

    public static ZCApplication getInstance() {
        if (instance == null) {
            instance = new ZCApplication();
        }
        return instance;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            synchronized (ZCApplication.class) {
                if (mRequestQueue == null) {
                    mRequestQueue = Volley
                            .newRequestQueue(getApplicationContext());
                }
            }
        }
        return mRequestQueue;
    }

    /**
     * @return
     * @Description:图片加载网络
     */
    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            // int memClass = ((ActivityManager)
            // getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            // // Use 1/8th of the available memory for this memory cache.
            // int cacheSize = 1024 * 1024 * memClass / 4;
            mImageLoader = new ImageLoader(getRequestQueue(),
                    new BitmapLruCache());
        }
        return mImageLoader;
    }

    @Override
    public void onCreate() {
        Log.d(LogTag, "ZCApplication>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>onCreate");
        super.onCreate();
        // 第一个参数是Application Context
        instance = this;
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        String SAVEPATH = Environment.getExternalStorageDirectory()
                + "/zcProject/ImageCache";
    }

}
