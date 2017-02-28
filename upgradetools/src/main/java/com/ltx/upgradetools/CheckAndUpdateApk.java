/**  
 * @Title: CheckAndUpdateApk.java
 * @date: 2015-2-11 下午4:52:01
 * @Copyright: (c) 2015, unibroad.com Inc. All rights reserved.
 */
package com.ltx.upgradetools;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ltx.upgradetools.R;


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
    public static String TOUCHUS_APK = "ZcStore.apk";
    private static final String PACKAGENAME = "com.ltx.zc";
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Activity mActivity;
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
                UPApplication.getInstance().downloadId = -1;
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
     * 
     * @param mainFragmentActivity
     */
    public void init(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * 检查软件是否有更新版本
     * 
     * @return
     */
    public void checkUpdate() {
        // 获取当前软件版本
        cancelUpdate = false;
        localVersionCode = getVersionCode(mActivity);
        VersionCheckAsyncTask versionCheck = new VersionCheckAsyncTask(this, localVersionCode, "TouChus");
        versionCheck.execute("");
        // WaitTool.showDialog(mContext, "");
    }

    /**
     * 取消下载
     * 
     * @Description:
     */
    public void stopDownload() {
        UPApplication.getInstance().cancelDownloadById();
    }

    /**
     * 获取软件版本号
     * 
     * @param context
     * @return
     */
    private int getVersionCode(Activity mActivity) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mActivity.getPackageManager().getPackageInfo(PACKAGENAME, 0).versionCode;
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
            UPApplication.downloadFileByUrl(UPApplication.UPDATE_URL, mHandler);
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
            UPApplication.isNeedUpdate = true;
            Message msg = new Message();
            msg.what = SHOW_FORCE_UPDATE;
            msg.obj = update_info;
            mHandler.sendMessageDelayed(msg, 2000);
        } else if (flag.equals("yes")) {
            // 显示提示对话框
            UPApplication.isNeedUpdate = true;
            Message msg = new Message();
            msg.what = SHOW_UPDATE_DIALOG;
            msg.obj = update_info;
            mHandler.sendMessageDelayed(msg, 2000);
            return;
        }
        UPApplication.isNeedUpdate = false;
        WaitTool.dismissDialog();
    }
}
