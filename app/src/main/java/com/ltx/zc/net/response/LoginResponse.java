package com.ltx.zc.net.response;

import com.ltx.zc.net.BaseResponse;

/**
 * {"success":false,"hasErrors":false,"message":"帐号或密码不能为空","code":500,"timeSpan":1487575800094}
 */
public class LoginResponse extends BaseResponse {

    private boolean success;
    private boolean hasErrors;
    private String message;
    private int code;
    private long timeSpan;

    public long getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(long timeSpan) {
        this.timeSpan = timeSpan;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
