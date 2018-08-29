package com.guessmusic.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.guessmusic.R;
import com.guessmusic.data.Const;
import com.guessmusic.httpclient.HttpClient;
import com.guessmusic.model.GetSong;
import com.guessmusic.model.WordButton;
import com.guessmusic.model.iDialogButtonListener;
import com.guessmusic.model.iWordButtonClickListener;
import com.guessmusic.myui.MyGridView;
import com.guessmusic.net.NC;
import com.guessmusic.net.NetListenner;
import com.guessmusic.net.SJKResponse;
import com.guessmusic.net.Task_coin;
import com.guessmusic.tools.FileOperate;
import com.guessmusic.tools.LogUtil;
import com.guessmusic.tools.MyPlayer;
import com.guessmusic.tools.Tools;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.webkit.WebView;

import org.json.JSONObject;

public class MainActivity extends Activity implements iWordButtonClickListener,NetListenner ,OnClickListener{
	// 待选文字数量
	public final static int COUNTS_WORDS = 24;

	// 答案正确
	public final static int ANSWER_RIGHT = 1;
	// 答案错误
	public final static int ANSWER_WRONG = 2;
	// 答案不完整
	public final static int ANSWER_LACK = 3;

	public final static int ID_DIALOG_DELET_WORD = 1;
	public final static int ID_DIALOG_TIP_WORD = 2;
	public final static int ID_DIALOG_LACK_COINS_WORD = 3;

	// 文字闪烁次数
	public final static int SPARD_TIME = 6;

	// 唱片，拨杆，开始按钮动画
	private Animation mDiscAnim, mDiscBarInAnim, mDiscBarOutAnim;
	private LinearInterpolator mDiscLin, mDiscBarInLin, mDiscBarOutLin;

	// 唱片，拨杆，开始按钮
	private ImageView mViewDisc, mViewDiscBar;
	private ImageButton mbtnGameStart;

	// 当前动画是否运行
	private boolean mIsRunning = false;

	// 已选，待选文字框容器
	private ArrayList<WordButton> mAllWords, mSelWords;

	// 待选文字框控件
	private MyGridView mMyGridView;

	// 已选择文字框的布局控件
	private LinearLayout mViewWordsContainer;

	// 关卡引索
	private int mCurrentIndex;
	private TextView mTextPassIndex;
	private TextView mTextCurrentIndex;

	// 当前关卡歌曲
	private GetSong mCurrentSong;
	private TextView mTextCurrentPassSongName;

	// 过关界面
	private LinearLayout mPassEvent;

	// 已选文字提示按钮，待选文字删除按钮, 分享按钮
	private ImageButton mBtnTip, mBtnDelet;

	// 已拥有的金币数量
	private TextView mTextCurrentCoin;
	private int mCurrentCoins;
	private android.webkit.WebView webView;
	private FrameLayout frameLayout,button;
	private LinearLayout lin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		 webView = (android.webkit.WebView) findViewById(R.id.webView);
		 //initData();


		 frameLayout=(FrameLayout)findViewById(R.id.layout_bar_coin);
		 frameLayout.setOnClickListener(this);

		/**
		 * 控件初始化
		 */
		// 唱片，拨杆，开始按钮
		mViewDisc = (ImageView) findViewById(R.id.view_disc);
		mViewDiscBar = (ImageView) findViewById(R.id.view_disc_bar);
		// 游戏开始按钮
		mbtnGameStart = (ImageButton) findViewById(R.id.btn_game_start);
		// 待选文字框控件
		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		// 已选择文字框的布局控件
		mViewWordsContainer = (LinearLayout) findViewById(R.id.layout_word_sel);
		// 已选文字提示按钮，待选文字删除按钮, 分享按钮
		mBtnTip = (ImageButton) findViewById(R.id.btn_tip_word);
		mBtnDelet = (ImageButton) findViewById(R.id.btn_delet_word);



		/**
		 * 初始化动画
		 */
		// 盘片旋转
		mDiscAnim = AnimationUtils.loadAnimation(this, R.anim.disc_rotate);
		mDiscLin = new LinearInterpolator();
		mDiscAnim.setInterpolator(mDiscLin);
		mDiscAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewDiscBar.startAnimation(mDiscBarOutAnim); // 拨杆开始退出
			}
		});
		// 拨杆开始
		mDiscBarInAnim = AnimationUtils.loadAnimation(this,
				R.anim.disc_bar_in_rotate);
		mDiscBarInLin = new LinearInterpolator();
		mDiscBarInAnim.setInterpolator(mDiscBarInLin);
		mDiscBarInAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mViewDisc.startAnimation(mDiscAnim); // 盘片开始转动
				// 播放音乐
				MyPlayer.playSong(MainActivity.this,
						mCurrentSong.getSongFileName());
			}
		});
		// 拨杆退出
		mDiscBarOutAnim = AnimationUtils.loadAnimation(this,
				R.anim.disc_bar_out_rotate);
		mDiscBarOutLin = new LinearInterpolator();
		mDiscBarOutAnim.setInterpolator(mDiscBarOutLin);
		mDiscBarOutAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mbtnGameStart.setVisibility(View.VISIBLE); // 动画结束，开始按钮取消隐藏
				mIsRunning = false;
			}
		});


		// 音乐播放按钮
		mbtnGameStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 播放拨杆进入动画
				handlePlayButton();
			}
		});

		// 初始化当前关卡数
		mCurrentIndex = FileOperate.dataLoad(MainActivity.this)[FileOperate.GAME_LEVEL];

		// 显示当前关的索引
		mTextCurrentIndex = (TextView) findViewById(R.id.text_level);
		mTextCurrentIndex.setText((mCurrentIndex + 1) + "");

		// 初始化当前金币数
		mCurrentCoins = FileOperate.dataLoad(MainActivity.this)[FileOperate.GAME_COINS];

		// 显示当前金币数
		mTextCurrentCoin = (TextView) findViewById(R.id.text_bar_coins);
		mTextCurrentCoin.setText(mCurrentCoins + "");
		mTextCurrentCoin.setOnClickListener(this);

		lin=(LinearLayout)findViewById(R.id.lin);
		lin.setOnClickListener(this);

		button=(FrameLayout)findViewById(R.id.button);
		button.setOnClickListener(this);

		// 设置文字框的监听事件
		mMyGridView.registOnWordButtonClick(this);

		// 初始化文字框数据
		initCurrentData();

		// 删除某个文字框
		handleDeletWord();

		// 提示一个正确答案
		handleTipWord();

	}

	@Override
	protected void onPause() {
		// 程序挂起时取消盘片转动动画
		mViewDisc.clearAnimation();

		// 暂停音乐
		MyPlayer.stopSong(MainActivity.this);
		super.onPause();

		// 保存数据
		FileOperate.dataSave(MainActivity.this, mCurrentIndex, mCurrentCoins);
	}

	/**
     * 动画启动控制
     */
	private void handlePlayButton() {
		if (mViewDiscBar != null) {
			if (!mIsRunning) {
				// 拨杆动画开始，开始按钮隐藏
				mViewDiscBar.startAnimation(mDiscBarInAnim);
				mbtnGameStart.setVisibility(View.INVISIBLE);
				mIsRunning = true;
			}
		}
	}

	/**
     * 初始化当前关卡数据
     */
	private void initCurrentData() {

		// 初始化已选文字框
		mSelWords = initSelectWord();
		// 设置已选文字框大小
		LayoutParams params = new LayoutParams(120, 120);

		// 清空原来的答案
		mViewWordsContainer.removeAllViews();

		// 动态添加文字框
		for (int i = 0; i < mSelWords.size(); i++) {
			mViewWordsContainer.addView(mSelWords.get(i).mViewButton, params);
		}

		// 初始化待选文字框
		mAllWords = initGetWord();

		// 更新MyGridView数据
		mMyGridView.updateData(mAllWords);

		// 自动播放音乐
		handlePlayButton();

	}


		private void initData() {
          webView.getSettings().setJavaScriptEnabled(true);
          webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");




          webView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
//                        + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
  webView.loadUrl("javascript:window.local_obj.showSource('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });

        //DEEPLINK.ME、BRANCH METRICS、URX、QUIXEY，还有国内的“应用链“公司



        webView.loadUrl("http://192.168.0.99:8080/test.html");
    }



	/**
     * 初始化已选文字框
     */
	private ArrayList<WordButton> initSelectWord() {

		mCurrentSong = initCurrentSong(); // 获取当前歌曲信息

		ArrayList<WordButton> data = new ArrayList<WordButton>();

		for (int i = 0; i < mCurrentSong.getSongNameLenth(); i++) {
			View view = Tools.getView(MainActivity.this,
					R.layout.self_ui_gridview_item);

			final WordButton btnWord = new WordButton();
			btnWord.mViewButton = (Button) view
					.findViewById(R.id.btn_gridview_item);
			btnWord.mViewButton.setTextColor(Color.WHITE);
			btnWord.mViewButton.setText("");
			btnWord.mIsVisible = false;
			btnWord.mViewButton
					.setBackgroundResource(R.drawable.game_wordblank); // 重新设置已选文字框的背景
			data.add(btnWord);
			btnWord.mViewButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cleanSelectWord(btnWord);
					// 播放音效
					MyPlayer.playTone(MainActivity.this,
							MyPlayer.INDEX_TONE_CANCEL);
				}
			});
		}
		return data;
	}

	/**
     * 初始化待选文字框
     */
	private ArrayList<WordButton> initGetWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		// 初始化待选文字数据
		String[] allWord = generateAllWord();

		// 为待选框赋值对应文字
		for (int i = 0; i < COUNTS_WORDS; i++) {
			WordButton btn = new WordButton();
			btn.mWordString = allWord[i];
			data.add(btn);
		}
		return data;
	}

	@Override
	/**
	 * 定义待选文字框监听事件
	 */
	public void onWordButtonClickListener(WordButton wordButton) {
		// 设置已选文字框文字
		setSelectWord(wordButton);

		// 检查答案
		int checkAnswer = checkAnswer();

		// 答案正确
		if (checkAnswer == ANSWER_RIGHT) {
			for (int i = 0; i < mSelWords.size(); i++) {
				mSelWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
			handlePassEvent();
		}
		// 答案错误
		else if (checkAnswer == ANSWER_WRONG) {
			sparkWord();
		}
		// 答案不完整
		else if (checkAnswer == ANSWER_LACK) {
			for (int i = 0; i < mSelWords.size(); i++) {
				mSelWords.get(i).mViewButton.setTextColor(Color.WHITE);
			}
		}

	}

	/**
     * 获取当前关卡歌曲
     */
	private GetSong initCurrentSong() {
		GetSong song = new GetSong();

		// 获取当前关卡的歌曲名字和文件名字
		String temp[] = Const.SONG_INFO[mCurrentIndex];
		song.setSongFileName(temp[Const.INDEX_FILE_NAME]);
		song.setSongName(temp[Const.INDEX_SONG_NAME]);

		return song;
	}

	/**
     * 生成随机文字
     */
	private char getRandomWord() {
		String word = "";
		int hight, low;

		Random random = new Random();

		hight = (176 + Math.abs(random.nextInt(30)));
		low = (161 + Math.abs(random.nextInt(70)));

		byte[] wordByte = new byte[2];
		wordByte[0] = (Integer.valueOf(hight)).byteValue();
		wordByte[1] = (Integer.valueOf(low)).byteValue();
		try {
			word = new String(wordByte, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return word.charAt(0);
	}

	/**
     * 生成所有随机文字
     */
	private String[] generateAllWord() {

		String allWord[] = new String[COUNTS_WORDS];
		Random random = new Random();
		// 存入歌曲名字
		for (int i = 0; i < mCurrentSong.getSongNameLenth(); i++) {
			allWord[i] = mCurrentSong.getSongNameChar()[i] + "";
		}
		// 存入随机文字
		for (int i = mCurrentSong.getSongNameLenth(); i < COUNTS_WORDS; i++) {
			allWord[i] = getRandomWord() + "";
		}
		// 打乱文字顺序,将第一个数和后面的输做随机交换，逐个遍历
		for (int i = COUNTS_WORDS - 1; i >= 0; i--) {
			int randomIndex = random.nextInt(i + 1);
			String temp;
			temp = allWord[randomIndex];
			allWord[randomIndex] = allWord[i];
			allWord[i] = temp;
		}
		return allWord;
	}

	/**
     * 设置答案
     */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mCurrentSong.getSongNameLenth(); i++) {
			// 如果不为空，则填入文字
			if (mSelWords.get(i).mWordString.length() == 0) {
				mSelWords.get(i).mViewButton.setText(wordButton.mWordString);
				mSelWords.get(i).mWordString = wordButton.mWordString;

				// 记录选择文字框索引
				mSelWords.get(i).mIndex = wordButton.mIndex;
				// 设置待选文字框可见性
				setAllWordVisible(wordButton);

				break;
			}
		}
	}

	/**
     * 设置待选文字框可见性
     */
	private void setAllWordVisible(WordButton wordButton) {
		wordButton.mIsVisible = false;
		wordButton.mViewButton.setVisibility(View.INVISIBLE);
	}

	/**
     * 清除已选文字，并在待选文字框中显示清除的文字
     */
	private void cleanSelectWord(WordButton wordButton) {
		wordButton.mWordString = "";
		wordButton.mViewButton.setText("");

		mAllWords.get(wordButton.mIndex).mViewButton
				.setVisibility(View.VISIBLE);
		mAllWords.get(wordButton.mIndex).mIsVisible = true;

	}

	/**
     * 检查答案
     */
	private int checkAnswer() {
		// 检查长度
		for (int i = 0; i < mSelWords.size(); i++) {
			if (mSelWords.get(i).mWordString.length() == 0) {
				return ANSWER_LACK;
			}
		}

		// 答案完整
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < mSelWords.size(); i++) {
			strBuf.append(mSelWords.get(i).mWordString);
		}
		return (strBuf.toString().equals(mCurrentSong.getSongName())) ? ANSWER_RIGHT
				: ANSWER_WRONG;
	}

	/**
     * 文字闪烁
     */
	private void sparkWord() {
		// 定时器
		TimerTask task = new TimerTask() {
			boolean change = false;
			int spardTimes = 0;

			@Override
			public void run() {
				// 对界面UI的更改一定要在UI线程
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (++spardTimes > SPARD_TIME) {
							return;
						}

						// 执行闪烁
						for (int i = 0; i < mSelWords.size(); i++) {
							mSelWords.get(i).mViewButton
									.setTextColor(change ? Color.RED
											: Color.WHITE);
						}

						change = !change;
					}
				});
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1, 150);
	}

	/**
     * 处理过关事件
     */
	private void handlePassEvent() {
		mPassEvent = (LinearLayout) this.findViewById(R.id.layout_pass_event);
		mPassEvent.setVisibility(View.VISIBLE);

		// 停止未完成的动画
		mViewDisc.clearAnimation();

		// 暂停音乐
		MyPlayer.stopSong(MainActivity.this);

		// 金币音效
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_COIN);

		// 奖励金币
		mCurrentCoins += Const.PASS_AWARD_COINS;
		mTextCurrentCoin.setText(mCurrentCoins + "");

		// 关卡引索加1
		mTextCurrentIndex.setText((++mCurrentIndex + 1) + "");

		// 显示过关画面关卡引索
		mTextPassIndex = (TextView) findViewById(R.id.text_pass_level);
		mTextPassIndex.setText((mCurrentIndex + 1) + "");

		// 显示当前关卡歌曲名称
		mTextCurrentPassSongName = (TextView) findViewById(R.id.text_pass_song_name);
		mTextCurrentPassSongName.setText(mCurrentSong.getSongName());

		// 下一关按键处理
		ImageButton btnPass = (ImageButton) findViewById(R.id.btn_pass_next);
		btnPass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 播放音效
				MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_ENTER);
				if (judegIsPassed()) {
					// 进入通关界面
					Tools.startIntent(MainActivity.this, AllPassView.class);
				} else {
					// 开始下一关
					mPassEvent.setVisibility(View.INVISIBLE);

					// 加载下一关数据
					initCurrentData();
				}
			}
		});

	}

	/**
     * 判断是否还有下一关
     */
	private boolean judegIsPassed() {
		return mCurrentIndex == Const.SONG_INFO.length;
	}

	/**
     * 从配置文件中获取删除文字时金币的扣除数量
     */
	private int getDeletWordCoin() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
     * 从配置文件中获取提示文字时金币的扣除数量
     */
	private int getTipWordCoin() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
     * 增加或减少已拥有的金币数量
     */
	private void handleCoins(int data) {

		mCurrentCoins += data;
		mTextCurrentCoin.setText(mCurrentCoins + "");

	}

	/**
     * 删除一个待选文字
     */
	private void deletOneWord() {

		if (mCurrentCoins - getDeletWordCoin() >= 0) {
			if (findNotAnswer() != null) {
				setAllWordVisible(findNotAnswer());

				// 减少金币数量
				handleCoins(-getDeletWordCoin());
			}
		} else {
			showConfirmDialog(ID_DIALOG_LACK_COINS_WORD);
		}
	}

	/**
     * 添加一个正确文字
     */
	private void tipOneWord() {
		boolean tipWord = false;

		if (mCurrentCoins - getTipWordCoin() >= 0) {
			for (int i = 0; i < mSelWords.size(); i++) {
				// 如果已选文字框为空，则填入一个正确答案
				if (mSelWords.get(i).mWordString.length() == 0) {
					onWordButtonClickListener(findIsAnswer(i));
					tipWord = true;

					// 减少金币数量
					handleCoins(-getTipWordCoin());
					break;
				}
			}
		} else {
			showConfirmDialog(ID_DIALOG_LACK_COINS_WORD);
		}
		// 如果没有找到可以填充答案的文字框
		if (!tipWord) {
			// 闪烁文字提示用户
			sparkWord();
		}

	}

	/**
     * 找到一个不是答案的文字框, 且当前文字框是可见的
     */
	private WordButton findNotAnswer() {
		Random random = new Random();
		WordButton buf = null;
		int count = 0;
		while (true) {
			// 随机挑选一个文字框
			int index = random.nextInt(COUNTS_WORDS);
			buf = mAllWords.get(index);

			if (buf.mIsVisible && !isTheAnswerWord(buf)) {
				return buf;
			}
			count++;
			if (count > 1000) {
				return null;
			}
		}
	}

	/**
     * 找到一个答案中的文字,index需要填入文字框的索引
     */
	private WordButton findIsAnswer(int index) {
		WordButton buf = null;

		for (int i = 0; i < COUNTS_WORDS; i++) {
			buf = mAllWords.get(i);

			// 返回一个正确文字
			if (buf.mWordString.equals(mCurrentSong.getSongNameChar()[index]
					+ "")) {
				return buf;
			}
		}

		return null;
	}

	/**
     * 判断某个文字是否为答案
     */
	private boolean isTheAnswerWord(WordButton word) {
		boolean result = false;

		for (int i = 0; i < mCurrentSong.getSongNameLenth(); i++) {
			if (word.mWordString.equals("" + mCurrentSong.getSongNameChar()[i])) {
				result = true;
				break;
			}
		}

		return result;
	}

	/**
     * 删除某个文字框事件
     */
	private void handleDeletWord() {
		mBtnDelet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_ENTER);
				showConfirmDialog(ID_DIALOG_DELET_WORD);
			}
		});
	}


	final class InJavaScriptLocalObj {
        @android.webkit.JavascriptInterface
        public void showSource(String html) {
           // refreshHtmlContent(html);
        }
    }



    private void refreshHtmlContent(final String html){
        android.util.Log.i("网页内容",html);
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //解析html字符串为对象
                org.jsoup.nodes.Document document = org.jsoup.Jsoup.parse(html);
                //通过类名获取到一组Elements，获取一组中第一个element并设置其html
                org.jsoup.select.Elements elements = document.getElementsByClass("linkedme");
                 String kkkk =  elements.get(0).attr("href");
               android.widget.Toast.makeText(getApplicationContext(), kkkk, android.widget.Toast.LENGTH_LONG).show();

            }
        },5000);

}


	/**
     * 显示答案的一个文字事件
     */
	private void handleTipWord() {
		mBtnTip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_TONE_ENTER);
				//showConfirmDialog(ID_DIALOG_TIP_WORD);

				//add by xiabing
				LogUtil.getInstance().i(" mBtnTip click");
				//new Task_coin(this);
				requestAdData();

}

			});
		}


	/**
     * 自定义AlertDialog事件响应
     */
	// 删除错误答案
	private iDialogButtonListener mBtnConfirmDeletWordListener = new iDialogButtonListener() {

		@Override
		public void onClick() {
			deletOneWord();
		}
	};

	// 答案提示
	private iDialogButtonListener mBtnConfirmTipWordListener = new iDialogButtonListener() {

		@Override
		public void onClick() {
			tipOneWord();
		}
	};

	// 金币不足
	private iDialogButtonListener mBtnConfirmLackCoinsListener = new iDialogButtonListener() {

		@Override
		public void onClick() {

		}
	};

	/**
     * 显示对话框
     */
	private void showConfirmDialog(int id) {
		switch (id) {
			case ID_DIALOG_DELET_WORD:
				Tools.showDialog(MainActivity.this, "确认花掉" + getDeletWordCoin()
						+ "个金币去掉一个错误答案", mBtnConfirmDeletWordListener);
				break;
			case ID_DIALOG_TIP_WORD:
				Tools.showDialog(MainActivity.this, "确认花掉" + getTipWordCoin()
						+ "个金币获得一个文字提示", mBtnConfirmTipWordListener);
				break;
			case ID_DIALOG_LACK_COINS_WORD:
				Tools.showDialog(MainActivity.this, "金币不足，去商店补充？",
						mBtnConfirmLackCoinsListener);
				break;
			default:
				break;
		}
	}


	@Override
	public void onConnectionRecieveData(String InterFace, SJKResponse response) {
		LogUtil.getInstance().i("response="+response.getResponse());
		if(InterFace.equals(NC.coin)){//验证码
			LogUtil.getInstance().i("hello");
		}
	}

	@Override
	public void onConnectionError(String InterFace, SJKResponse response) {
		LogUtil.getInstance().i("response="+response.getResponse());
	}

	@Override
	public void onNetErrorResponse() {
		LogUtil.getInstance().i("response");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.text_bar_coins:
			case R.id.lin:
				LogUtil.getInstance().i(" coin click");
				//new Task_coin(this);
				requestAdData();
				break;
		}
	}

	private void requestAdData() {
		final String url = NC.ADDRESS+NC.coin;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpClient.RequestParam param = new HttpClient.RequestParam();
					param.addParam("amount", "9");
					param.addParam("to", "0x77bbe12CA1F7b98BB07293Cf1AE399Be51a636D6");
					param.addParam("inputData", "0x");
					param.addParam("dappIcon", "https://buytoken.cn/easyee/ckfinderfiles/images/ico(3).jpg");
					param.addParam("backUrl", "http://192.168.0.99:9090/sdk/txPaid");
					param.addParam("key", "f2b5ade83a08bf3abea98deb167cf594");
					param.addParam("chainId", "3");
					param.addParam("paidId", "1008796");

					String result = HttpClient.post(url, param);
					LogUtil.getInstance().i("result=" + result.toString());
					JSONObject jsonobject = new JSONObject(result);
					String url=jsonobject.getJSONObject("data").getString("lkUrl").toString();
					Message msg = new Message();
					msg.obj = url;
					msg.what = 1;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String url= (String) msg.obj;
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			boolean flag = isAppInstallen(MainActivity.this,"com.buntoy.wallet");
			if(flag){
				intent.setClassName("com.buntoy.wallet","com.buntoy.wallet.activity.common.SplashActivity");
			}
			startActivity(intent);
		}
	};

	public static boolean isAppInstallen(Context context , String packageName){
		PackageManager pm = context.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			installed = false;
		}
		return  installed;

	}

}
