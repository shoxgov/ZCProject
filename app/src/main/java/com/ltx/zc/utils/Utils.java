package com.ltx.zc.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import org.apache.http.NameValuePair;
import org.apache.http.conn.util.InetAddressUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Utils {

	// 判断网络是否正常
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// 判断网络是否连接上
	public static boolean checkNetworkInfo(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
			return true;
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
			return true;
		return false;
	}

	// 判断3G网络是否正常
	public static boolean is3GNetworkConnected(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mTelephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// 检查网络连接，如果无网络可用，就不需要进行连网操作等
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		if (info == null || !mConnectivity.getBackgroundDataSetting()) {
			return false;
		}
		// 判断网络连接类型，只有在3G或wifi里进行一些数据更新。
		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_MOBILE
		/*
		 * && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS &&
		 * !mTelephony.isNetworkRoaming()
		 */) {
			return info.isConnected();
		} else {
			return false;
		}
	}

	/**
	 * 防止按钮连续点击
	 */
	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static String connect(String path) {
		try {
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
				return result;
			} else if (urlConnection.getResponseCode() == 408
					|| urlConnection.getResponseCode() == 500
					|| urlConnection.getResponseCode() == 504
					|| urlConnection.getResponseCode() == 502) {
				return "";
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @Title: int2ip
	 * @Description: TODO(将ip的整数形式转换成ip形式 )
	 * @param 参数
	 * @param ipInt
	 * @param 参数
	 * @return
	 * @return 返回类型 String
	 * @throws
	 */
	public static String int2ip(int ipInt) {
		StringBuilder sb = new StringBuilder();
		sb.append(ipInt & 0xFF).append(".");
		sb.append((ipInt >> 8) & 0xFF).append(".");
		sb.append((ipInt >> 16) & 0xFF).append(".");
		sb.append((ipInt >> 24) & 0xFF);
		return sb.toString();
	}

	/**
	 * 
	 * @Title: getLocalIpAddress
	 * @Description: TODO(获取当前ip地址 )
	 * @param 参数
	 * @param context
	 * @param 参数
	 * @return
	 * @return 返回类型 String
	 * @throws
	 */
	public static String getLocalIpAddress(Context context) {
		try {
			String ipv4;
			List<NetworkInterface> nilist = Collections.list(NetworkInterface
					.getNetworkInterfaces());
			for (NetworkInterface ni : nilist) {
				List<InetAddress> ialist = Collections.list(ni.getInetAddresses());
				for (InetAddress address : ialist) {
					if (!address.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(ipv4 = address
									.getHostAddress())) {
						return ipv4;
					}
				}

			}

		} catch (SocketException ex) {
			ex.printStackTrace();
		}
	        return null;  
//		try {
//			WifiManager wifiManager = (WifiManager) context
//					.getSystemService(Context.WIFI_SERVICE);
//			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//			int i = wifiInfo.getIpAddress();
//			return int2ip(i);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + e.getMessage();
//		}
		// return null;
	}

	/**
	 * 
	 * @Title: genTimeStamp
	 * @Description: TODO(生成时间戳)
	 * @param 参数
	 * @return
	 * @return 返回类型 long
	 * @throws
	 */
	public static long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 
	 * @Title: genPackageSign
	 * @Description: TODO(生成签名)
	 * @param 参数
	 * @param params
	 * @param 参数
	 * @return
	 * @return 返回类型 String
	 * @throws
	 */
	public static String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=16036f22fe7e88c72c9449ace5b40281");
		String packageSign = getMd5(sb.toString().getBytes()).toUpperCase();
		return packageSign;
	}

	/**
	 * 
	 * @Title: genNonceStr
	 * @Description: TODO(随机字符串)
	 * @param 参数
	 * @return
	 * @return 返回类型 String
	 * @throws
	 */
	public static String genNonceStr() {
		Random random = new Random();
		return getMd5(String.valueOf(random.nextInt(10000)).getBytes());
	}

	public static String getMd5(byte[] buffer) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(buffer);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static String[] getPhoneContacts(Context context, Uri uri) {
		String[] contact = new String[2];
		// 得到ContentResolver对象
		ContentResolver cr = context.getContentResolver();
		// 取得电话本中开始一项的光标
		Cursor cursor = cr.query(uri, null, null, null, null);
		if (cursor != null) {
			Cursor phone = null;
			try {
				cursor.moveToFirst();
				// 取得联系人姓名
				int nameFieldColumnIndex = cursor
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				contact[0] = cursor.getString(nameFieldColumnIndex);
				// 取得电话号码
				String ContactId = cursor.getString(cursor
						.getColumnIndex(ContactsContract.Contacts._ID));
				phone = cr.query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ "=" + ContactId, null, null);
				if (phone != null) {
					phone.moveToFirst();
					contact[1] = phone
							.getString(phone
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (phone != null) {
					phone.close();
				}
				cursor.close();
			}
		} else {
			return null;
		}
		return contact;
	}
}
