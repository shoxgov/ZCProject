package com.ltx.zc.net;

import android.text.TextUtils;


public class BXBaseResponse extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private String result;//0表示成功
	private String message;

	public String getResult() {
		if(TextUtils.isEmpty(result)){
			return "-1";
		}
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
