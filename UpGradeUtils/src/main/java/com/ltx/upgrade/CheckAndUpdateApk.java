/**  
 * @Title: CheckAndUpdateApk.java
 * @date: 2015-2-11 下午4:52:01
 * @Copyright: (c) 2015, unibroad.com Inc. All rights reserved.
 */
package com.ltx.upgrade;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ltx.upgrade.R;


/**
 * @Class: CheckAndUpdateApk
 * @Description: TODO(描述类作用)
 * @version: V1.0
 */

public class CheckAndUpdateApk implements VersionCheckAsyncTask.IVersionUpdate {
    /* 下载中 */
    public static final int DOWNLOAD = 1;
    /* 下载结束 */
    public static final int DOWNLOAD_FINISH = 2;
    private static final int SHOW_UPDATE_DIALOG = 3;
    private static final int SHOW_FORCE_UPDATE = 5;
    public static final int DOWNLOAD_ERROR = 4;
    public static final int DOWNLOAD_ERROR_INSUFFICIENT_SPACE = 6;
    public static final int SHOW_TOAST = 7;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Activity mActivity;
    // 下载任务管理器 有监听器
    private DownloadManager downloadManager;
    private DownloadChangeObserver apkDownloadObserver;
    private int fileTotalLength = -1;
    private static final String tag = "UpGradeApplication";
    private static final String CONTENT_URI = "content://downloads/my_downloads/";
    private String UPGRADE_APK = "UPGRADE.apk";
    public static String UPGRADENAME = "UPGRADE";
    /**
     * updateUrl: 有更新时的下载Apk地址
     */
    public static String UPDATE_URL = "http://gdown.baidu.com/data/wisegame/fc328fa3a33efe57/QQ_482.apk";
//    public static final String UPDATE_URL = "http://update.touchus.com/apps/TouChus.apk";
    /**
     * checkUpdateUrl: 检查更新包的路径文件
     */
    public static String checkUpdateUrl = "http://update.touchus.com/apps/AppsVersion.txt";// HOST_URL
    /**
     * downloadId: 更新下载时的任务ID号
     */
    public static long downloadId = -1;
    /**
     * isNeedUpdate: 是否需要升级标记
     */
    public static boolean isNeedUpdate = false;
    private String dirPATH = "upgrade_download";
    /* 更新进度条 */
    private ProgressBar mProgress;
    private TextView mProgress_info;
    private Dialog mDownloadDialog;
    /**
     * localVersionCode: 本应用当前版本
     */
    private int localVersionCode;
    // BASE Notification ID
    private static int Notification_ID_BASE = R.mipmap.ic_launcher;
    // private NotificationManager notficationManager;
    // // 新建状态栏通知
    // private Notification baseNF;
    // private PendingIntent pd;
    private static CheckAndUpdateApk instance;
    private Thread downloadApkThread;
    private Handler mHandler = new Handler() {
        @SuppressWarnings("deprecation")
        public void handleMessage(Message msg) {
            switch (msg.what) {
            // 正在下载
            case DOWNLOAD:
                // 设置进度条位置
                float prog = msg.getData().getFloat("progress", 0.0f);
//                msg.getData().putString("title", title);
                mProgress.setProgress((int) prog);
                mProgress_info.setText(prog + "%");
                // 发出状态栏通知
                // baseNF.setLatestEventInfo(mContext, "TouChus.apk", "已下载    " + prog + "%", pd);
                // notficationManager.notify(Notification_ID_BASE, baseNF);
                break;
            case DOWNLOAD_FINISH:
                if (cancelUpdate)
                    return;
                Toast.makeText(mActivity, R.string.download_success, Toast.LENGTH_SHORT).show();
                // 取消下载对话框显示
                mDownloadDialog.dismiss();
                // 安装文件
                String uri = (String) msg.obj;
                installApk(Uri.parse(uri));
                cancelUpdate = true;
                downloadId = -1;
                break;

            case SHOW_FORCE_UPDATE:
                WaitTool.dismissDialog();
                if (msg.obj == null) {
                    showForceUpdateDialog("");
                    break;
                }
                showForceUpdateDialog(msg.obj.toString());
                break;

            case SHOW_UPDATE_DIALOG:
                WaitTool.dismissDialog();
                if (msg.obj == null) {
                    showNoticeDialog("");
                    break;
                }
                showNoticeDialog(msg.obj.toString());
                break;

            case DOWNLOAD_ERROR:
                Toast.makeText(mActivity,
                        mActivity.getString(R.string.soft_update_download_error),Toast.LENGTH_SHORT).show();
                if (mDownloadDialog != null) {
                    mDownloadDialog.dismiss();
                }
                break;
                
            case DOWNLOAD_ERROR_INSUFFICIENT_SPACE:
                Toast.makeText(mActivity, R.string.storageless_clear_toast,Toast.LENGTH_SHORT).show();
                if (mDownloadDialog != null) {
                    mDownloadDialog.dismiss();
                }
                break;
            case SHOW_TOAST:
                Toast.makeText(mActivity, msg.obj.toString(),Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
            }
        };
    };

    public static CheckAndUpdateApk getInstance() {
        if (instance == null) {
            instance = new CheckAndUpdateApk();
        }
        return instance;
    }

    /**
     * Creates a new instance of CheckAndUpdateApk.
     */
    public void init(Activity mActivity, int localVersionCode,String checkUpdateUrl , String downApkUrl) {
        this.mActivity = mActivity;
        this.localVersionCode = localVersionCode;
        checkUpdateUrl = checkUpdateUrl;
        UPDATE_URL = downApkUrl;
    }

    /**
     * 检查软件是否有更新版本
     * 
     * @return
     */
    public void checkUpdate() {
        // 获取当前软件版本
        cancelUpdate = false;
        VersionCheckAsyncTask versionCheck = new VersionCheckAsyncTask(this, localVersionCode, UPGRADENAME);
        versionCheck.execute("");
        WaitTool.showDialog(mActivity, "");
    }

    /**
     * 取消下载
     * 
     * @Description:
     */
    public void stopDownload() {

        cancelDownloadById();
    }

    /**
     * 获取软件版本号
     * @return
     */
    public int getVersionCode(Activity mActivity) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog(String update_info) {
        // 构造对话框
        try {
            String updateInfo;
            if (TextUtils.isEmpty(update_info)) {
                updateInfo = mActivity.getString(R.string.soft_update_info);
            } else {
                updateInfo = update_info;
            }
            // + "\n"
            // + "1. 修复BUG,优化性能"
            // + "\n"
            // + "2. 由于适配某些低版本,造成原来导航的记录会清空,造成不便请见谅!";
            Builder builder = new Builder(mActivity);
            builder.setTitle(R.string.soft_update_title);
            builder.setMessage(updateInfo);
            // 更新
            builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // 显示下载对话框
                    showDownloadDialog();
                }
            });
            // 稍后更新
            builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final Dialog noticeDialog = builder.create();
            // noticeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            noticeDialog.setCancelable(false);
            noticeDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示软件更新对话框
     */
    private void showForceUpdateDialog(String update_info) {
        // 构造对话框
        try {
            String updateInfo;
            if (TextUtils.isEmpty(update_info)) {
                updateInfo = mActivity.getString(R.string.soft_update_info);
            } else {
                updateInfo = update_info;
            }
            Builder builder = new Builder(mActivity);
            builder.setTitle(R.string.soft_update_title);
            builder.setMessage(updateInfo);
            // 更新
            builder.setPositiveButton(R.string.force_update, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // 显示下载对话框
                    showDownloadDialog();
                }
            });
            final Dialog noticeDialog = builder.create();
            // noticeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            noticeDialog.setCancelable(false);
            noticeDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        // 构造软件下载对话框
        Builder builder = new Builder(mActivity);
        builder.setTitle(R.string.soft_updating);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(mActivity);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        mProgress_info = (TextView) v.findViewById(R.id.update_progress_info);
        mProgress.setMax(100);// 设置processBar
        builder.setView(v);
        builder.setPositiveButton(R.string.soft_update_background, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDownloadDialog = builder.create();
        // mDownloadDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.show();
        // 下文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        // initNoticationNotice();
        if (downloadApkThread == null || !downloadApkThread.isAlive()) {
            downloadApkThread = new downloadApkThread();
            downloadApkThread.start();
        }
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            // String sdpath = Environment.getExternalStorageDirectory() + "/touchus/";
            // mSavePath = sdpath + "download";
            downloadFileByUrl(UPDATE_URL, mHandler);
        }
    };

    /**
     * 安装APK文件
     */
    private void installApk(Uri uri) {
        // File apkfile = new File(mSavePath, TOUCHUS_APK);
        if (uri == null) {
            return;
        }
        // 通过Intent安装APK文件
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(uri, "application/vnd.android.package-archive");
            mActivity.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateVersion(String flag, String update_info) {
//         flag = "yes";
        if (flag.equals("force")) {
            isNeedUpdate = true;
            Message msg = new Message();
            msg.what = SHOW_FORCE_UPDATE;
            msg.obj = update_info;
            mHandler.sendMessageDelayed(msg, 2000);
        } else if (flag.equals("yes")) {
            // 显示提示对话框
            isNeedUpdate = true;
            Message msg = new Message();
            msg.what = SHOW_UPDATE_DIALOG;
            msg.obj = update_info;
            mHandler.sendMessageDelayed(msg, 2000);
        } else {
            isNeedUpdate = false;
            WaitTool.dismissDialog();
            Toast.makeText(mActivity, "已是最新版本，无须升级~", Toast.LENGTH_SHORT).show();
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////
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
        Log.d(tag, "downloadFileByUrl    url="+url);
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
                        msg.obj = mActivity.getString(R.string.download_pause_hint);
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
            downloadManager = (DownloadManager) mActivity.getSystemService(Activity.DOWNLOAD_SERVICE);
            // 创建下载请求
            DownloadManager.Request down = new DownloadManager.Request(
                    Uri.parse(url));
            // 设置允许使用的网络类型，这里是移动网络和wifi都可以
            down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                    | DownloadManager.Request.NETWORK_WIFI);
            DateFormat formatter0 = new SimpleDateFormat("yyyyMMddHHmmss");
            down.setTitle(mActivity.getPackageName()+"_" + formatter0.format(new Date())+"_"  + UPGRADE_APK);// 设置下载中通知栏提示的标题
            down.setDescription(mActivity.getString(R.string.downloaded));// 设置下载中通知栏提示的介绍
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
            Log.d(tag, "save dir="+dir.getAbsolutePath());
            DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            down.setDestinationInExternalPublicDir(dirPATH, mActivity.getPackageName()+"_" + formatter.format(new Date())+"_"  + UPGRADE_APK);
            // 将下载请求放入队列
            downloadId = downloadManager.enqueue(down);
            Log.d(tag, "downloadId="+downloadId);
            // 把当前下载的ID保存起来
            SharedPreferences sPreferences = mActivity.getSharedPreferences(
                    "downloadcomplete", 0);
            sPreferences.edit().putLong("downloadId", downloadId).commit();
            // registerReceiver(receiver, new
            // IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            if (apkDownloadObserver == null) {
                apkDownloadObserver = new DownloadChangeObserver(mHandler);
            }
            apkDownloadObserver.setDownloadId(mHandler, downloadId);
            mActivity.getContentResolver().registerContentObserver(
                    Uri.parse(CONTENT_URI + downloadId), false,
                    apkDownloadObserver);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag, "Exception e="+e.getMessage());
            try {
                Uri uri = Uri.parse(UPDATE_URL);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(it);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            mHandler.sendEmptyMessage(CheckAndUpdateApk.DOWNLOAD_ERROR);
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
                    if (title.contains("apk-1")) {
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
                    if (handler != null && downloadId > 0) {
                        Message msg2 = new Message();
                        msg2.what = CheckAndUpdateApk.DOWNLOAD_FINISH;
                        msg2.obj = fileUri.toLowerCase();
                        handler.sendMessage(msg2);
                    }
                    this.downloadId = -1;
                    mActivity.getSharedPreferences("downloadcomplete", 0).edit()
                            .putLong("downloadId", 0).commit();
                    try {
                        mActivity.getContentResolver().unregisterContentObserver(
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
                    mActivity.getSharedPreferences("downloadcomplete", 0).edit()
                            .putLong("downloadId", 0).commit();
                    try {
                        mActivity.getContentResolver().unregisterContentObserver(
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
