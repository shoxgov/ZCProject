package com.ltx.zc.net;

import java.io.Serializable;

public class BaseResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private int status_code;

	public int getStatus_code() {
		return status_code;
	}

	public void setStatus_code(int status_code) {
		this.status_code = status_code;
	}

}
