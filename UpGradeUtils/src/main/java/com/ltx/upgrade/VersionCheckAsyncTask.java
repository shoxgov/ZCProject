package com.ltx.upgrade;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class VersionCheckAsyncTask extends AsyncTask<String, Integer, String> {
	/**
	 * versionCode: 要比较的本地版本号
	 */
	private int versionCode;
	/**
	 * versionName: 从读取文件中提取当前版本的字段
	 */
	private String versionName;
	private IVersionUpdate iVersionUpdate;
	private String update_info;

	public interface IVersionUpdate {
		void updateVersion(String flag, String update_info);
	}

	public VersionCheckAsyncTask(IVersionUpdate interf, int versionCode,
								 String versionName) {
		this.iVersionUpdate = interf;
		this.versionCode = versionCode;
		this.versionName = versionName;
	}

	@Override
	protected String doInBackground(String... arg0) {
		String verjson = null;
		try {
			String path = CheckAndUpdateApk.checkUpdateUrl;
			int needIndex = path.indexOf("?");
			if (needIndex > 0) {
				String needDoStr = path.substring(needIndex + 1);
				String[] arr = needDoStr.split("&");
				StringBuffer strBuffer = new StringBuffer();
				for (int i = 0; i < arr.length - 1; i++) {
					int index = arr[i].indexOf("=");
					String temp1 = arr[i].substring(0, index + 1);
					String temp2 = arr[i].substring(index + 1);
					temp2 = URLEncoder.encode(temp2, "UTF-8");
					strBuffer.append(temp1).append(temp2).append("&");
				}

				int index = arr[arr.length - 1].indexOf("=");
				String temp1 = arr[arr.length - 1].substring(0, index + 1);
				String temp2 = arr[arr.length - 1].substring(index + 1);
				temp2 = URLEncoder.encode(temp2, "UTF-8");
				strBuffer.append(temp1).append(temp2);

				path = path.substring(0, needIndex + 1) + strBuffer.toString();
			}
			// 根据地址创建URL对象(网络访问的url)
			URL url = new URL(path);
			// url.openConnection()打开网络链接
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");// 设置请求的方式
			urlConnection.setReadTimeout(15000);// 设置超时的时间
			urlConnection.setConnectTimeout(5000);// 设置链接超时的时间
			// 设置请求的头
			urlConnection
					.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
			// 获取响应的状态码 404 200 505 302
			if (urlConnection.getResponseCode() == 200) {
				// 获取响应的输入流对象
				InputStream is = urlConnection.getInputStream();

				// 创建字节输出流对象
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				// 定义读取的长度
				int len = 0;
				// 定义缓冲区
				byte buffer[] = new byte[1024];
				// 按照缓冲区的大小，循环读取
				while ((len = is.read(buffer)) != -1) {
					// 根据读取的长度写入到os对象中
					os.write(buffer, 0, len);
				}
				// 释放资源
				is.close();
				os.close();
				// 返回字符串
				String result = new String(os.toByteArray());
				verjson = result;
			} else if (urlConnection.getResponseCode() == 408
					|| urlConnection.getResponseCode() == 500
					|| urlConnection.getResponseCode() == 504
					|| urlConnection.getResponseCode() == 502) {
				verjson = "";
			} else {
				verjson = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			verjson = null;
		}
		if (verjson == null)
			return "yes";//"netness";
		String reValue = "no";
		try {
			JSONObject obj = new JSONObject(verjson);
			JSONObject info;
			if (obj.has("version_info_" + versionCode)) {
				info = obj.getJSONObject("version_info_" + versionCode);
			} else {
				info = obj.getJSONObject("version_info");
			}
			int current = 0;
			int force = 0;
			if (info.has(versionName)) {
				current = info.getInt(versionName);
			}
			if (info.has("updateType")) {
				force = info.getInt("updateType");
			}
			if (info.has("update_info")) {
				update_info = info.getString("update_info");
			}
			if (current > versionCode) {
				reValue = "yes";
			}
			if (force == 1) {
				reValue = "force";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "yes";//"reValue;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result.equals("")) {
			iVersionUpdate.updateVersion("no", update_info);
		} else {
			iVersionUpdate.updateVersion(result, update_info);
		}
	}
}
