package com.guessmusic.net;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * 解析类
 *
 */
public class JsonParser {
	/**
	 * @author Benlong.Tan <br>
	 * @createTime 2015-7-13 2:10:58 <br>
	 * @description cn.vjsp.shanjiuke.md.net
	 */

	public static Object parser(String Json, Object cls) {
		return JSON.parseObject(Json, cls.getClass());
	}
	
	//解析集合
	//public static <T> List<T> parserList(String json,Class<T> cls){
	//	return JSON.parseArray(json,cls);
	//}
	//public static  parserList(String json,Object cls){
	//	return  JSON.parseArray(json,cls.getClass());	
	//}

	public static Boolean parserBoolean(String Json) {
		try {
			JSONObject Jo = JSONObject.parseObject(Json);
			return "1".equals(Jo.getString("flag"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int parserInt(String Json, String key) {
		try {
			JSONObject Jo = JSONObject.parseObject(Json);
			return Jo.getIntValue(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String parserString(String Json, String key) {
		try {
			JSONObject Jo = JSONObject.parseObject(Json);
			return Jo.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
