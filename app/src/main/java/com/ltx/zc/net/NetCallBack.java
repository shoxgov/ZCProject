package com.ltx.zc.net;


public interface NetCallBack {
    public void onNetResponse(BaseResponse baseRes);

    public void onNetErrorResponse(String tag, Object error);
}
