package com.guessmusic.net;

import android.content.Context;

/**
 * @className 回调接口
 * 
 * @author benlong.Tan
 * 
 * @TODO 用来数据回传
 * 
 * @decription <br>
 *             onConnectionRecieveData：当数据访问成功时:InterFaceType为接口号，
 *             RecieveData为回调数据 <br>
 * 
 *             onConnectionError:网络错误信息<br>
 * 
 * @date 2014/08/13
 */
/**
 * @author xiabing-wineti
 * 
 */
public interface NetListenner {
	/**
	 * 网络访问成功回调
	 * 
	 * @param InterFaceType
	 * @param RecieveData
	 */
	public void onConnectionRecieveData(String InterFace, SJKResponse response);

	/**
	 * 网络访问失败回调
	 * 
	 * @param InterFaceType
	 * @param RecieveData
	 */
	public void onConnectionError(String InterFace, SJKResponse response);

	/**
	 * 网络异常
	 */
	public void onNetErrorResponse();

}
