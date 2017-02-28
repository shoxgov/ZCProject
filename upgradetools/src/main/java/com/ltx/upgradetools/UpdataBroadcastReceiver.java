/**  
 * @Title: ghdfh.java
 * @date: 2015-5-19 下午2:05:26
 * @Copyright: (c) 2015, unibroad.com Inc. All rights reserved.
 */
package com.ltx.upgradetools;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * @Class: ghdfh
 * @Package: com.unibroad.carphone.download
 * @Description: TODO(描述类作用)
 * @author: wsy@unibroad.com
 * @version: V1.0
 */
public class UpdataBroadcastReceiver extends BroadcastReceiver {

    @SuppressLint("NewApi")
    public void onReceive(Context context, Intent intent) {
        try {
            long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            SharedPreferences sPreferences = context.getSharedPreferences("downloadcomplete", 0);
            long downloadId = sPreferences.getLong("downloadId", 0);
            if (downloadId == myDwonloadID) {
                String serviceString = Context.DOWNLOAD_SERVICE;
                DownloadManager dManager = (DownloadManager) context.getSystemService(serviceString);
                Intent install = new Intent(Intent.ACTION_VIEW);
                Uri downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID);
                if (downloadFileUri == null)
                    return;
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}