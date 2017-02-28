package com.ltx.zc.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.ltx.zc.R;
import com.ltx.zc.ZCApplication;
import com.ltx.zc.utils.ToastTool;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommReq {
	private final String LogTag = "ZCProject";
	/**
	 * 请求类的TAG
	 */
	private String tag;
	/**
	 * 请求的URL
	 */
	private String baseUrl;
	private RequestQueue requestQueue;
	private ObjectRequest request;

	public enum RequestType {
		POST, GET, POST_JSON
	}

	/**
	 * 请求类型
	 */
	private RequestType requestType = RequestType.POST_JSON;

	/**
	 * POST请求时附带的参数
	 */
	protected Map<String, String> postParams = new HashMap<String, String>();
	/**
	 * 当Post请求时，提交的参数是String型的，不是Map型时
	 */
	protected String postStringParams = null;
	private RetryPolicy retryPolicy = new DefaultRetryPolicy(5000,
			DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
			DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

	// ///////////////////
	// 抽象函数
	// /////////////////
	abstract public String generUrl();

	abstract public Class<BaseResponse> getResClass();

	abstract public BaseResponse getResBean();

	/**
	 * 请求结果回调接口
	 */
	private NetCallBack netCallback = null;
	private Context context;
	/**
	 * hasDecode: 返回的数据是否需要解密， 默认是不要解
	 */
	private boolean hasDecode = false;

	public BaseCommReq() {
		requestQueue = ZCApplication.getInstance().getRequestQueue();
		context = ZCApplication.getInstance().getApplicationContext();
	}

	protected void setTag(String tag) {
		this.tag = tag;
	}

	public void setStringPostParams(String postStringParams) {
		this.postStringParams = postStringParams;
	}

	public void setIsneedDecode(boolean hasDecode) {
		this.hasDecode = hasDecode;
	}

	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}

	protected void handPostParam() {
	}

	protected void handPostStringParams() {
	}

	public void setNetCallback(NetCallBack netCallback) {
		this.netCallback = netCallback;
	}

	private void produceRequst() {
		baseUrl = generUrl();
		if (TextUtils.isEmpty(baseUrl)) {
			// handleErrorResponse(null);
			return;
		}
		FakeX509TrustManager.allowAllSSL();
		switch (requestType) {
		case POST:
			// 参数以String上传
			handPostParam();
			StringRequest stringRequest = new StringRequest(
					Request.Method.POST, baseUrl,
					new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							handleOnRequest(response);
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							handleErrorResponse(error);
						}
					}) {
				@Override
				protected Map<String, String> getParams() {
					return postParams;
				}

				@Override
				public byte[] getBody() throws AuthFailureError {
					if (TextUtils.isEmpty(postStringParams)) {
						return super.getBody();
					}
					try {
						return postStringParams.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						return super.getBody();
					}
				}
			};
			stringRequest.setTag(tag);
			stringRequest.setRetryPolicy(retryPolicy);
			requestQueue.add(stringRequest);
			break;
		case POST_JSON:
			// 参数以JSON形式提交
			handPostParam();
			request = new ObjectRequest(Request.Method.POST, baseUrl,
					new JSONObject(postParams),
					new Response.Listener<Object>() {

						@Override
						public void onResponse(Object obj) {
							handleOnRequest(obj);
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							handleErrorResponse(error);
						}
					});
			request.setTag(tag);
			request.setRetryPolicy(retryPolicy);
			requestQueue.add(request);
			break;

		case GET:
		default:
			if (hasDecode) {
			} else {
				request = new ObjectRequest(Request.Method.GET, baseUrl, null,
						new Response.Listener<Object>() {
							@Override
							public void onResponse(Object obj) {
								handleOnRequest(obj);
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								handleErrorResponse(error);
							}
						});
				request.setTag(tag);
				request.setRetryPolicy(retryPolicy);
				requestQueue.add(request);
			}
		}
	}

	public void addRequest() {
		produceRequst();
		Log.d(tag, LogTag+" BaseCommReq addRequest" + "  url=" + baseUrl);
		if (requestType == RequestType.POST) {
			Log.d(tag, LogTag+" BaseCommReq addRequest" + "  postParams="
					+ postParams.toString());
		} else if (requestType == RequestType.POST_JSON) {
			Log.d(tag, LogTag+" BaseCommReq addRequest" + "  postParams="
					+ postParams.toString());
		}
	}

	private void handleOnRequest(Object obj) {
		if (netCallback != null) {
			try {
				BaseResponse baseRes = getResBean();
					baseRes = HttpJsonAdapter.getInstance().get(obj.toString(),
							getResClass());
				netCallback.onNetResponse(baseRes);
				System.out.println(LogTag+" onResponse----------:"
						+ obj.toString().replace("\n", ""));
			} catch (BizException be) {
				be.printStackTrace();
				ToastTool.showShortBigToast(context,
						R.string.process_data_exception_try);
				netCallback.onNetErrorResponse(tag,
						context.getString(R.string.process_data_exception_try));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void handleErrorResponse(VolleyError error) {
		if (error != null) {
			error.printStackTrace();
			System.out.println(LogTag+" onErrorResponse----------:"
					+ error.toString());
		}
		if (netCallback != null) {
			netCallback.onNetErrorResponse(tag, error);
		}
	}
}
