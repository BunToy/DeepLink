package com.guessmusic.tools;

import com.guessmusic.R;
import com.guessmusic.model.iDialogButtonListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class Tools {

	private static AlertDialog mAlertDialog;

	/**
	 * 获取View
	 */
	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(layoutId, null);

		return layout;
	}

	/**
	 * 界面跳转
	 */
	public static void startIntent(Context context, Class skip) {
		Intent intent = new Intent(context, skip);
		context.startActivity(intent);
	}

	/**
	 * 自定义对话框
	 */
	public static void showDialog(final Context context, String message,
								  final iDialogButtonListener listener) {
		View dialogView = null;

		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				R.style.Theme_Transparent);
		dialogView = getView(context, R.layout.dialog_view);

		ImageButton btnConfirm = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_confirm);
		ImageButton btnCancel = (ImageButton) dialogView
				.findViewById(R.id.btn_dialog_cancel);
		TextView textMessage = (TextView) dialogView
				.findViewById(R.id.text_dialog_message);
		textMessage.setText(message);

		btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭对话框
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
				}
				// 事件回调
				if (listener != null) {
					listener.onClick();
				}
				// 播放音效
				MyPlayer.playTone(context, MyPlayer.INDEX_TONE_ENTER);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭对话框
				if (mAlertDialog != null) {
					mAlertDialog.dismiss();
				}
				// 播放音效
				MyPlayer.playTone(context, MyPlayer.INDEX_TONE_CANCEL);
			}
		});

		// 为dialog设置View
		builder.setView(dialogView);
		mAlertDialog = builder.create();

		// 显示对话框
		mAlertDialog.show();
	}

}
