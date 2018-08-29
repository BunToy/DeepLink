package com.guessmusic.tools;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * 音乐音效播放
 */
public class MyPlayer {

	// 音效数组下标
	public final static int INDEX_TONE_ENTER = 0;
	public final static int INDEX_TONE_CANCEL = 1;
	public final static int INDEX_TONE_COIN = 2;
	// 音效的文件名
	private final static String[] TONE_NAMES = { "enter.mp3", "cancel.mp3",
			"coin.mp3" };

	// 歌曲播放
	private static MediaPlayer mMusicMediaPlayer;
	// 音效
	private static MediaPlayer[] mToneMediaPlayer = new MediaPlayer[TONE_NAMES.length];

	/**
	 * 播放歌曲
	 *
	 * @param context
	 * @param fileName
	 */
	public static void playSong(Context context, String fileName) {
		if (mMusicMediaPlayer == null) {
			mMusicMediaPlayer = new MediaPlayer();
		}

		// 强制重置
		mMusicMediaPlayer.reset();

		// 加载声音文件
		AssetManager assetManager = context.getAssets();
		try {
			AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);
			mMusicMediaPlayer
					.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());

			mMusicMediaPlayer.prepare();
			mMusicMediaPlayer.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void stopSong(Context context) {
		if (mMusicMediaPlayer != null) {
			mMusicMediaPlayer.stop();
		}
	}

	public static void playTone(Context context, int index) {
		if (mToneMediaPlayer[index] == null) {
			mToneMediaPlayer[index] = new MediaPlayer();
			// 加载声音文件
			AssetManager assetManager = context.getAssets();
			try {
				AssetFileDescriptor fileDescriptor = assetManager
						.openFd(TONE_NAMES[index]);
				mToneMediaPlayer[index].setDataSource(
						fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());

				mToneMediaPlayer[index].prepare();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		mToneMediaPlayer[index].start();
	}

}
