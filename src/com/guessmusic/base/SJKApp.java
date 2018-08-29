package com.guessmusic.base;


import android.app.Application;
import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.guessmusic.tools.LogUtil;

/**
 * application
 * 
 */
public class SJKApp extends Application {
	public static SJKApp Instance;
	static RequestQueue mQueue;
	//static Gson gson = new Gson();

	public static SJKApp getInstance() {
		return Instance;
	}

	public static void setInstance(SJKApp instance) {
		Instance = instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		setInstance(this);
		mQueue = Volley.newRequestQueue(context);// 初始化volley
	}

	/*public static Gson getGson() {
		if (gson == null)
			return new Gson();
		else {
			return gson;
		}
	}

	public static void setGson(Gson gson) {
		SJKApp.gson = gson;
	}*/

	public static RequestQueue getmQueue() {
		if (mQueue == null)
			mQueue = Volley.newRequestQueue(context);
		return mQueue;
	}

	public static void setmQueue(RequestQueue mQueue) {
		SJKApp.mQueue = mQueue;
	}

	static Context context;

	public static Context getContext() {
		if (context == null)
			context = new SJKApp();
		return context;
	}

	public void setContext(Context context) {
		SJKApp.context = context;
	}


}