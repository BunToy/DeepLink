package com.guessmusic.myui;

import java.util.ArrayList;

import com.guessmusic.R;
import com.guessmusic.model.WordButton;
import com.guessmusic.model.iWordButtonClickListener;
import com.guessmusic.tools.MyPlayer;
import com.guessmusic.tools.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class MyGridView extends GridView {

	// 数据容器
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();

	private MyGridAdapter mAdapter;

	private Context mContext;

	private Animation mScaleAnim;

	private iWordButtonClickListener mWordButtonClickListener; // 声明接口

	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		// 设置数据源
		mAdapter = new MyGridAdapter();
		this.setAdapter(mAdapter);
	}

	public void updateData(ArrayList<WordButton> list) {
		mArrayList = list;

		// 重新设置数据源
		this.setAdapter(mAdapter);
	}

	class MyGridAdapter extends BaseAdapter {

		@Override
		// 返回适配器中的数据数量
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		// 返回当前选择的对象
		public Object getItem(int position) {
			return mArrayList.get(position);
		}

		@Override
		// 返回当前选择的引索
		public long getItemId(int position) {
			return position;
		}

		@Override
		// 绘制单元View
		public View getView(int position, View convertView, ViewGroup parent) {

			final WordButton btnWord; // 文字选择按钮实例化

			if (convertView == null) {
				convertView = Tools.getView(mContext,
						R.layout.self_ui_gridview_item);

				btnWord = mArrayList.get(position); // 获取容器中的对象

				// 加载动画
				mScaleAnim = AnimationUtils.loadAnimation(mContext,
						R.anim.word_scale);
				// 设置动画延迟时间
				mScaleAnim.setStartOffset(position * 70);
				btnWord.mIndex = position;
				if (btnWord.mViewButton == null) {
					btnWord.mViewButton = (Button) convertView
							.findViewById(R.id.btn_gridview_item);
					// 设置按钮监听事件
					btnWord.mViewButton
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									// 播放音效
									MyPlayer.playTone(mContext,
											MyPlayer.INDEX_TONE_ENTER);
									mWordButtonClickListener
											.onWordButtonClickListener(btnWord);
								}
							});
				}

				convertView.setTag(btnWord);

			} else {
				btnWord = (WordButton) convertView.getTag();
			}

			btnWord.mViewButton.setText(btnWord.mWordString);

			// 动画播放
			convertView.startAnimation(mScaleAnim);

			return convertView;
		}

	}

	/**
	 * 注册接口监听器
	 */
	public void registOnWordButtonClick(iWordButtonClickListener listener) {
		mWordButtonClickListener = listener;
	}

}
