package com.guessmusic.net;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SJKResponse {

	private String code;

	private String status;

	private Object data;

	private JSONObject object;

	public JSONObject getObject() {
		return object;
	}

	public void setObject(JSONObject object) {
		this.object = object;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return status;
	}

	public void setMsg(String msg) {
		this.status = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public boolean success() {
		return "0000".equals(code);
	}

	public boolean empty() {
		if (object == null) {
			return true;
		}
		JSONObject jo = getJSONObjects();
		return jo == null || jo.isEmpty();
	}
	
	//返回data jsonarray 为空
	public boolean emptyArray() {
		if (object == null) {
			return true;
		}
		JSONArray jsonarray = getJsonArray();
		return jsonarray == null || jsonarray.isEmpty();
	}

	public String getString(String key) {
		return object.getJSONObject("data").getString(key);
	}

	public int getInteger(String key) {
		JSONObject jObject = object.getJSONObject("data");
		return jObject==null?0:jObject.getInteger(key)==null?0:jObject.getInteger(key);
	}
	
	public double getDouble(String key) {
		JSONObject jObject = object.getJSONObject("data");
		return jObject==null?0:jObject.getDouble(key)==null?0:jObject.getDouble(key);
	}

	public Map<String, Object> getMap(String key) {
		return object.getJSONObject("data").getJSONObject(key);
	}

	public JSONArray getJsonArray() {
		return object.getJSONArray("data");
	}
	
	public JSONArray getJsonArray(String key) {
		return object.getJSONObject("data").getJSONArray(key);
	}

	public JSONObject getJSONObjects() {
		try {
			return object.getJSONObject("data");
		} catch (Exception e) {
			e.printStackTrace();
			return new JSONObject();
		}
	}
	
	public int getJSONObject(String key,String key2) {
		return object.getJSONObject("data").getJSONObject(key).getIntValue(key2);
	}

	public JSONArray getJSONArray(String key,String key2) {
		return object.getJSONObject("data").getJSONObject(key).getJSONArray(key2);
	}
	
	public int getJSONArrayInt(String key,String key2) {
		return object.getJSONObject("data").getJSONObject(key).getIntValue(key2);
	}
	
	public String getJSONArrayString(String key,String key2) {
		return object.getJSONObject("data").getJSONObject(key).getString(key2);
	}

	public String getResponse() {
		return "code:" + getCode() + "  msg:" + getMsg() + " data:" + getData();
	}

	public static SJKResponse netError() {
		SJKResponse r = new SJKResponse();
		r.setCode("9999");
		r.setMsg("网络不给力");
		return r;
	}

	public static SJKResponse parser(JSONObject object) {
		SJKResponse res = new SJKResponse();
		if (object != null) {
			res.object = object;
			res.code = object.getString("code");
			res.status = object.getString("status");
			res.data = object.get("data");
		}
		return res;
	}

}
