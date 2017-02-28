package com.ltx.upgradetools;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017-02-20.
 */

public class UPApplication extends Application {
    private final String LogTag = "ZCProject-UPApplication";
    private String dirPATH = "upgrade_download";
    /**
     * downloadId: 更新下载时的任务ID号
     */
    public long downloadId = -1;
    /**
     * isNeedUpdate: 是否需要升级标记
     */
    public static boolean isNeedUpdate = false;
    public static String lastTime;
    // 下载任务管理器 有监听器
    private DownloadManager downloadManager;
    private DownloadChangeObserver apkDownloadObserver;
    private int fileTotalLength = -1;
    private static final String tag = "UPApplication";
    private static final String CONTENT_URI = "content://downloads/my_downloads/";
    /**
     * updateUrl: 有更新时的下载Apk地址
     */
    public static final String UPDATE_URL = "http://update.touchus.com/apps/TouChus.apk";
    /**
     * checkUpdateUrl: 检查更新包的路径文件
     */
    public static final String checkUpdateUrl = "http://update.touchus.com/apps/AppsVersion.txt";// HOST_URL


    @Override
    public void onCreate() {
        Log.d(LogTag, "UPApplication>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>onCreate");
        super.onCreate();
    }

    /**
     * 取消更新下载
     *
     * @Description:
     */
    public void cancelDownloadById() {
        if (downloadId < 0)
            return;
        downloadManager.remove(downloadId);
        downloadId = -1;
    }

    /**
     * 下载URL路径更新包
     *
     * @param url
     * @param mHandler
     * @Description:
     */
    public void downloadFileByUrl(String url, Handler mHandler) {
        if (downloadId > 0) {
            try {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor c = downloadManager.query(query);
                if (c.moveToFirst()) {// 有记录
                    int status = c.getInt(c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_PAUSED) {
                        Message msg = new Message();
                        msg.what = CheckAndUpdateApk.SHOW_TOAST;
                        msg.obj = getString(R.string.download_pause_hint);
                        if (mHandler != null)
                            mHandler.sendMessage(msg);
                    }
                    c.close();
                    return;
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            // 创建下载请求
            DownloadManager.Request down = new DownloadManager.Request(
                    Uri.parse(url));
            // 设置允许使用的网络类型，这里是移动网络和wifi都可以
            down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                    | DownloadManager.Request.NETWORK_WIFI);
            DateFormat formatter0 = new SimpleDateFormat("yyyyMMddHHmmss");
            down.setTitle("ZcStore_" + formatter0.format(new Date()) + ".apk");// 设置下载中通知栏提示的标题
            down.setDescription(getString(R.string.downloaded));// 设置下载中通知栏提示的介绍
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);// 表示下载进行中和下载完成的通知栏是否显示
            // 禁止发出通知，既后台下载
            // down.setShowRunningNotification(false);
            // 不显示下载界面
            down.setVisibleInDownloadsUi(false);
            // 设置下载后文件存放的位置
            File dir = new File(dirPATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File folder = Environment
                    .getExternalStoragePublicDirectory(dirPATH);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            down.setDestinationInExternalPublicDir(dirPATH, "ZcStore_"
                    + formatter.format(new Date()) + ".apk");
            // 将下载请求放入队列
            downloadId = downloadManager.enqueue(down);
            // 把当前下载的ID保存起来
            SharedPreferences sPreferences = getSharedPreferences(
                    "downloadcomplete", 0);
            sPreferences.edit().putLong("downloadId", downloadId).commit();
            // registerReceiver(receiver, new
            // IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            if (apkDownloadObserver == null) {
                apkDownloadObserver = new DownloadChangeObserver(mHandler);
            }
            apkDownloadObserver.setDownloadId(mHandler, downloadId);
            getContentResolver().registerContentObserver(
                    Uri.parse(CONTENT_URI + downloadId), false,
                    apkDownloadObserver);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Uri uri = Uri.parse(UPDATE_URL);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * @Class: DownloadChangeObserver
     * @Description: 监听下载进度条变化 回调
     * @version: V1.0
     */
    class DownloadChangeObserver extends ContentObserver {
        private Handler handler;
        private long downloadId;

        public DownloadChangeObserver(Handler handler) {
            super(handler);
            this.handler = handler;
        }

        public void setDownloadId(Handler handler, long downloadId) {
            this.handler = handler;
            this.downloadId = downloadId;
        }

        @Override
        public void onChange(boolean selfChange) {
            try {
                queryDownloadStatus(handler, this.downloadId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @param handler
     * @Description: 从下载管理数据库查询状态
     */
    private synchronized void queryDownloadStatus(Handler handler,
                                                  long downloadId) throws Exception {
        if (downloadId < 0)
            return;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor c = downloadManager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c
                    .getColumnIndex(DownloadManager.COLUMN_STATUS));
            // int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);
            int titleIdx = c.getColumnIndex(DownloadManager.COLUMN_TITLE);
            int fileSizeIdx = c
                    .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            int bytesDLIdx = c
                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            String title = c.getString(titleIdx);
            int fileSize = c.getInt(fileSizeIdx);
            int bytesDL = c.getInt(bytesDLIdx);
            StringBuilder sb = new StringBuilder();
            sb.append(title);
            sb.append(">>>>").append(bytesDL).append("/").append(fileSize);
            // Display the status
            Log.d(tag, " " + sb.toString());
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.d(tag, "  STATUS_PAUSED");
                case DownloadManager.STATUS_PENDING:
                    Log.d(tag, "  STATUS_PENDING");
                case DownloadManager.STATUS_RUNNING:
                    // 正在下载，不做任何事情
                    Log.d(tag, "  STATUS_RUNNING");
                    if (fileSize <= 0) {
                        break;
                    } else {
                        fileTotalLength = fileSize;
                    }
                    if (title.contains("-")) {
                        // downloadManager.remove(downloadId);
                        Log.d(tag, "STATUS_RUNNING   title.contains(-)=" + title);
                        break;
                    }
                    float progress = 0;
                    float num = bytesDL * 1.0f / fileTotalLength;
                    progress = (float) (Math.round(num * 1000)) / 10;// 这里的100就是2位小数点
                    Message msg = new Message();
                    msg.what = CheckAndUpdateApk.DOWNLOAD;
                    msg.getData().putFloat("progress", progress);
                    msg.getData().putString("title", title.toLowerCase());
                    if (handler != null)
                        handler.sendMessage(msg);
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    // 完成
                    fileTotalLength = -1;
                    // int filenameIdx =
                    // c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                    int filenameUriIdx = c
                            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                    // String filePath = c.getString(filenameIdx);
                    String fileUri = c.getString(filenameUriIdx);
                    Log.d(tag, "STATUS_SUCCESSFUL  filenameuri" + fileUri);
                    if (handler != null) {
                        Message msg2 = new Message();
                        msg2.what = CheckAndUpdateApk.DOWNLOAD_FINISH;
                        msg2.obj = fileUri.toLowerCase();
                        handler.sendMessage(msg2);
                    }
                    this.downloadId = -1;
                    getSharedPreferences("downloadcomplete", 0).edit()
                            .putLong("downloadId", 0).commit();
                    try {
                        getContentResolver().unregisterContentObserver(
                                apkDownloadObserver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    // 清除已下载的内容，重新下载
                    fileTotalLength = -1;
                    // Translate the pause reason to friendly text.
                    Log.d(tag, "STATUS_FAILED ");
                    downloadManager.remove(downloadId);
                    this.downloadId = -1;
                    getSharedPreferences("downloadcomplete", 0).edit()
                            .putLong("downloadId", 0).commit();
                    try {
                        getContentResolver().unregisterContentObserver(
                                apkDownloadObserver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);
                    int reason = c.getInt(reasonIdx);
                    if (reason == DownloadManager.ERROR_INSUFFICIENT_SPACE) {
                        if (handler != null)
                            handler.sendEmptyMessage(CheckAndUpdateApk.DOWNLOAD_ERROR_INSUFFICIENT_SPACE);
                    } else if (handler != null) {
                        handler.sendEmptyMessage(CheckAndUpdateApk.DOWNLOAD_ERROR);
                    }
                    break;
            }
        }
        c.close();
    }
}
