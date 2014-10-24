package com.china.infoway.net;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	static {
		client.setTimeout(15000);
	}
	
	public static void get(String urlString, AsyncHttpResponseHandler res) {
		client.get(urlString, res);
	}
	
	public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res) {
		client.get(urlString, params, res);
	}
	
	public static void get(String urlString, JsonHttpResponseHandler res) {
		client.get(urlString, res);
	}
	
	public static void get(String urlString, RequestParams params, JsonHttpResponseHandler res) {
		client.get(urlString, params, res);
	}
	
	public static void get(String urlString, BinaryHttpResponseHandler bHandler) {
		client.get(urlString, bHandler);
	}
	
	public static void post(String urlString, AsyncHttpResponseHandler res) {
		client.post(urlString, res);
	}
	
	public static void post(String urlString, RequestParams params, AsyncHttpResponseHandler res) {
		client.post(urlString, params, res);
	}
	
	public static void post(String urlString, JsonHttpResponseHandler res) {
		client.post(urlString, res);
	}
	
	public static void post(String urlString, RequestParams params, JsonHttpResponseHandler res) {
		client.post(urlString, params, res);
	}
	
	public static AsyncHttpClient getClient() {
		return client;	
	}
}
