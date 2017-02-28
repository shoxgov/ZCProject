package com.ltx.upgrade;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String UPDATE_URL = "http://gdown.baidu.com/data/wisegame/fc328fa3a33efe57/QQ_482.apk";
        String checkUpdateUrl = "http://update.touchus.com/apps/AppsVersion.txt";// HOST_URL
        CheckAndUpdateApk checkAndUpdateApk = CheckAndUpdateApk.getInstance();
        checkAndUpdateApk.init(this,checkAndUpdateApk.getVersionCode(this),checkUpdateUrl,UPDATE_URL);
        checkAndUpdateApk.checkUpdate();
    }
}
