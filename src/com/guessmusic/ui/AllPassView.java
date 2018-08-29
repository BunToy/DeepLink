package com.guessmusic.ui;

import com.guessmusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class AllPassView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pass_view);

		// 隐藏右上角的金币按钮
		FrameLayout view = (FrameLayout) findViewById(R.id.layout_bar_coin);
		view.setVisibility(View.INVISIBLE);
	}

}
