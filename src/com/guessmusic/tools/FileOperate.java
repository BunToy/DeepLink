package com.guessmusic.tools;

import com.guessmusic.data.Const;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 数据读取保存
 *
 */

public class FileOperate extends Activity {

	public static final String DATA_FILE_NAME = "Data";

	public static final int GAME_LEVEL = 0;

	public static final int GAME_COINS = 1;

	private static int[] mGameData = new int[2];

	public static int[] dataLoad(Context context) {
		SharedPreferences data = context.getSharedPreferences("DATA_FILE_NAME",
				context.MODE_PRIVATE);
		SharedPreferences.Editor editor = data.edit();
		mGameData[GAME_LEVEL] = data.getInt("GameLevel", Const.INIT_LEVEL);

		if (mGameData[GAME_LEVEL] >= Const.SONG_INFO.length) {
			mGameData[GAME_LEVEL] = 0;
		}

		mGameData[GAME_COINS] = data.getInt("GameCoins", Const.TOTAL_COINS);
		editor.commit();
		return mGameData;
	}

	public static void dataSave(Context context, int level, int coins) {
		SharedPreferences data = context.getSharedPreferences("DATA_FILE_NAME",
				context.MODE_PRIVATE);
		SharedPreferences.Editor editor = data.edit();
		editor.putInt("GameLevel", level);
		editor.putInt("GameCoins", coins);
		editor.commit();
	}
}
