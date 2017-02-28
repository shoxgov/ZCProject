package com.ltx.zc.net.request;


import com.ltx.zc.NetWorkConfig;
import com.ltx.zc.net.BaseCommReq;
import com.ltx.zc.net.BaseResponse;
import com.ltx.zc.net.response.LoginResponse;


/**
 */
public class LoginReq extends BaseCommReq {
    private LoginResponse response;

    private String username;
    private String password;

    @Override
    public String generUrl() {
        setTag("LoginReq");
        return NetWorkConfig.HTTP_HOST + "/jeesite/app/testLogin/login";
    }

    @Override
    public Class getResClass() {
        return LoginResponse.class;
    }

    @Override
    protected void handPostParam() {
        postParams.put("username", getUsername());
        postParams.put("password", getPassword());
    }

    @Override
    public BaseResponse getResBean() {
        if (response == null) {
            response = new LoginResponse();
        }
        return response;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}