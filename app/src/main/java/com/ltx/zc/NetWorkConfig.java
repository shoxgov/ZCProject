package com.ltx.zc;

public class NetWorkConfig {

	public static String HTTPS = "https://www.beixianggo.com/getway/main.do";
//	public static String HTTPS = "https://139.196.121.226:8443/getway/main.do";
	// tobsenl.gicp.net:23599 外网测试用
	private static String HOST = "192.168.0.100";//"shoxgov.iask.in";
//	private static String PORT = "";//":443";
	// private static String HOST = "192.168.1.100";
	private static String PORT = ":8080";
	private static String PORTS = ":80";
	public static String HTTP = "https://";
	public static String HTTP_HOST = HTTP + HOST + PORT;
	public static String IMAGEPATH = "http://www.beixianggo.com:8081/";//http://shoxgov.iask.in:17545/";//"http://192.168.1.251:9999/";//"http://" + HOST + PORT + "/";

}
