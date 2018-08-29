package com.guessmusic.tools;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class NetworkUtils {
	private static String LOG_TAG = "NetworkUtils";
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	private static final int NETWORK_TYPE_UNAVAILABLE = -1;  
    // private static final int NETWORK_TYPE_MOBILE = -100;  
    private static final int NETWORK_TYPE_WIFI = -101;  
  
    private static final int NETWORK_CLASS_WIFI = -101;  
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;  
    /** Unknown network class. */  
    private static final int NETWORK_CLASS_UNKNOWN = 0;  
    /** Class of broadly defined "2G" networks. */  
    private static final int NETWORK_CLASS_2_G = 1;  
    /** Class of broadly defined "3G" networks. */  
    private static final int NETWORK_CLASS_3_G = 2;  
    /** Class of broadly defined "4G" networks. */  
    private static final int NETWORK_CLASS_4_G = 3;  
 
  
    // 适配低版本手机  
    /** Network type is unknown */  
    public static final int NETWORK_TYPE_UNKNOWN = 0;  
    /** Current network is GPRS */  
    public static final int NETWORK_TYPE_GPRS = 1;  
    /** Current network is EDGE */  
    public static final int NETWORK_TYPE_EDGE = 2;  
    /** Current network is UMTS */  
    public static final int NETWORK_TYPE_UMTS = 3;  
    /** Current network is CDMA: Either IS95A or IS95B */  
    public static final int NETWORK_TYPE_CDMA = 4;  
    /** Current network is EVDO revision 0 */  
    public static final int NETWORK_TYPE_EVDO_0 = 5;  
    /** Current network is EVDO revision A */  
    public static final int NETWORK_TYPE_EVDO_A = 6;  
    /** Current network is 1xRTT */  
    public static final int NETWORK_TYPE_1xRTT = 7;  
    /** Current network is HSDPA */  
    public static final int NETWORK_TYPE_HSDPA = 8;  
    /** Current network is HSUPA */  
    public static final int NETWORK_TYPE_HSUPA = 9;  
    /** Current network is HSPA */  
    public static final int NETWORK_TYPE_HSPA = 10;  
    /** Current network is iDen */  
    public static final int NETWORK_TYPE_IDEN = 11;  
    /** Current network is EVDO revision B */  
    public static final int NETWORK_TYPE_EVDO_B = 12;  
    /** Current network is LTE */  
    public static final int NETWORK_TYPE_LTE = 13;  
    /** Current network is eHRPD */  
    public static final int NETWORK_TYPE_EHRPD = 14;  
    /** Current network is HSPA+ */  
    public static final int NETWORK_TYPE_HSPAP = 15;  
  

	/**
	 * 判断网络是否可用
	 * 
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!TextUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断Gps是否打开
	 * 
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 * 判断网络是否为漫游
	 */
	public static boolean isNetworkRoaming(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.w(LOG_TAG, "couldn't get connectivity manager");
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null
					&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				if (tm != null && tm.isNetworkRoaming()) {
					Log.d(LOG_TAG, "network is roaming");
					return true;
				} else {
					Log.d(LOG_TAG, "network is not roaming");
				}
			} else {
				Log.d(LOG_TAG, "not using mobile network");
			}
		}
		return false;
	}

	/**
	 * 判断wifi是否打开
	 */
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) {
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络是否3G网络
	 */
	public static boolean is3G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	/**
	 * 打开网络设置界面
	 */
	public static void openSetting(Activity activity) {
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}
	
	/** 
     * 获取网络类型 是否有 2,3g 等
     *  
     * @return 
     */  
    public static String getCurrentNetworkType(Context context) {  
        int networkClass = getNetworkClass(context);  
        String type = "未知";  
        switch (networkClass) {  
        case NETWORK_CLASS_UNAVAILABLE:  
            type = "无";  
            break;  
        case NETWORK_CLASS_WIFI:  
            type = "Wi-Fi";  
            break;  
        case NETWORK_CLASS_2_G:  
            type = "2G";  
            break;  
        case NETWORK_CLASS_3_G:  
            type = "3G";  
            break;  
        case NETWORK_CLASS_4_G:  
            type = "4G";  
            break;  
        case NETWORK_CLASS_UNKNOWN:  
            type = "未知";  
            break;  
        }  
        return type;  
    }  
  
    private static int getNetworkClass(Context context) {  
        int networkType = NETWORK_TYPE_UNKNOWN;  
        try {  
            final NetworkInfo network = ((ConnectivityManager) 
                    context
                    .getSystemService(Context.CONNECTIVITY_SERVICE))  
                    .getActiveNetworkInfo();  
            if (network != null && network.isAvailable()  
                    && network.isConnected()) {  
                int type = network.getType();  
                if (type == ConnectivityManager.TYPE_WIFI) {  
                    networkType = NETWORK_TYPE_WIFI;  
                } else if (type == ConnectivityManager.TYPE_MOBILE) {  
                    TelephonyManager telephonyManager = (TelephonyManager) 
                            context.getSystemService(  
                                    Context.TELEPHONY_SERVICE);  
                    networkType = telephonyManager.getNetworkType();  
                }  
            } else {  
                networkType = NETWORK_TYPE_UNAVAILABLE;  
            }  
  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
        return getNetworkClassByType(networkType);  
    }  
    
    private static int getNetworkClassByType(int networkType) {  
        switch (networkType) {  
        case NETWORK_TYPE_UNAVAILABLE:  
            return NETWORK_CLASS_UNAVAILABLE;  
        case NETWORK_TYPE_WIFI:  
            return NETWORK_CLASS_WIFI;  
        case NETWORK_TYPE_GPRS:  
        case NETWORK_TYPE_EDGE:  
        case NETWORK_TYPE_CDMA:  
        case NETWORK_TYPE_1xRTT:  
        case NETWORK_TYPE_IDEN:  
            return NETWORK_CLASS_2_G;  
        case NETWORK_TYPE_UMTS:  
        case NETWORK_TYPE_EVDO_0:  
        case NETWORK_TYPE_EVDO_A:  
        case NETWORK_TYPE_HSDPA:  
        case NETWORK_TYPE_HSUPA:  
        case NETWORK_TYPE_HSPA:  
        case NETWORK_TYPE_EVDO_B:  
        case NETWORK_TYPE_EHRPD:  
        case NETWORK_TYPE_HSPAP:  
            return NETWORK_CLASS_3_G;  
        case NETWORK_TYPE_LTE:  
            return NETWORK_CLASS_4_G;  
        default:  
            return NETWORK_CLASS_UNKNOWN;  
        }  
    }  
}
