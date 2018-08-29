package com.guessmusic.net;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.guessmusic.base.SJKApp;
import com.guessmusic.tools.LogUtil;
import com.guessmusic.tools.NetworkUtils;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;


/**
 * @author Benlong.Tan <br>
 * @createTime 2015-7-13 11:38:09 <br>
 * @description
 */
public class NetBases implements Listener<String>, ErrorListener{
	String AppVersion = "1.0.0";
	String Device = "android";
	String send_sitebh = "ec";
	String send_partner = "00001";
	String send_signmsg = "d258323372bc0cb039afea693309369d";
	String send_sitecharset = "utf-8";
	public NetListenner listenner = null;
	public String InterFace;

	protected void doRequest(NetListenner listenner, final SJKRequest req) {
		this.listenner = listenner;
		this.InterFace = req.getLocation();
		/*if (!NetworkUtils.isNetworkAvailable(SJKApp.getContext())) {
			listenner.onNetErrorResponse();
			return;
		}*/
		StringRequest post = new StringRequest(Request.Method.POST,
				req.getReqLocation(), this, this) {
			@Override
			public byte[] getBody() throws AuthFailureError {
				return req.getParamBytes();
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Charset", "UTF-8");
				headers.put("Accept-Language", "zh-cn,zh;q=0.5");
				return headers;
			}
		};
		LogUtil.getInstance().i("url=" + post.getUrl());
		// 设置请求时间 第一个参数请求超时时间   第二个参数是重试发送次数
		post.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, 1.0f));
		// 设置需要缓存
		post.setShouldCache(true);
		SJKApp.getmQueue().add(post);
	}

	public String enCodeMap(JSONObject dataJson) {
		return dataJson.toString();
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		LogUtil.getInstance().i("onErrorResponse=" + error.getMessage());
		listenner.onConnectionError(InterFace, SJKResponse.netError());
	}

	@Override
	public void onResponse(String response) {
		try {
			//response = response;
			JSONObject object = JSONObject.parseObject(response);
			SJKResponse res = SJKResponse.parser(object);
			if (res.success()) {  
				success(res);
			} else {
				fail(res);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			listenner.onConnectionError(InterFace, SJKResponse.netError());
		}
	}

	public void success(SJKResponse res) {
		listenner.onConnectionRecieveData(InterFace, res);
	}

	public void fail(SJKResponse res) {
		listenner.onConnectionError(InterFace, res);
	}

}
