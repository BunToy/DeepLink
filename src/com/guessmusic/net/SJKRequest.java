package com.guessmusic.net;


import com.alibaba.fastjson.JSONObject;

public class SJKRequest {
	
	private String req_no;
	
	private String device;
	
	private String device_id;
	
	public String os;
	
	private String version;
	
	private String location;
	
	private JSONObject param;
	
	private String mac;
	
	private SJKRequest() {
	}

	public String getReq_no() {
		return req_no;
	}

	public void setReq_no(String req_no) {
		this.req_no = req_no;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getOs() {
		return os;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getReqLocation() {
		return NC.ADDRESS + location;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location =location;
	}

	public JSONObject getParam() {
		return param;
	}

	public void setParam(JSONObject param) {
		this.param = param;
	}
	
	public static SJKRequest buildCommon(){
		SJKRequest req = new SJKRequest();
		req.setReq_no("");
		req.setDevice("android");
		req.setDevice_id("9999");
		req.setOs("android");
		//req.setVersion("V3.1.1");
		req.setMac("");
		return req;
	}
	
	public static SJKRequest buildCommon(String location){
		SJKRequest request = buildCommon(); 
		request.setLocation(location);
		return request;
	}
	
	public SJKRequest addParam(String key,Object value){
		if(param == null){
			param = new JSONObject();
		}
		param.put(key, value);
		return this;
	}

	public byte[] getParamBytes() {
		param.put("device", device);
		param.put("device_id", device_id);
		param.put("os", os);
		param.put("version", version);
		String encrypt = param.toString();
		return encrypt.getBytes();
	}
	

}
