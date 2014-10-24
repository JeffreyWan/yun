package com.china.infoway;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.china.infoway.utils.AsyncImageLoader;
import com.china.infoway.utils.AsyncImageLoader.ImageCallback;

@SuppressLint({ "HandlerLeak", "DefaultLocale" })
public class MessageActivity extends Activity {

	public ProgressDialog mydDialog = null;

	Intent intent;

	private EditText titleView, timeView, contentView;
	private ImageView imageView;
	private Button musicViewStart, musicViewStop;
	private LinearLayout back;

	// URL地址
	private String currentFilePath = "";

	// URL路径
	private String currentTempFilePath = "";
	private String strVideoURL = "";

	private TextView mTextView01;
	private MediaPlayer mMediaPlayer01;
	private static final String TAG = "Hippo_URL_MP3_Player";
	private boolean bIsReleased = false;

	private String image;
	private String music;

	Bitmap bitmap;

	private Button main;

	private Button set;

	private Button exit;

	private Button more;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_message);

		titleView = (EditText) findViewById(R.id.titleView);
		timeView = (EditText) findViewById(R.id.timeView);
		contentView = (EditText) findViewById(R.id.contentView);
		imageView = (ImageView) findViewById(R.id.imageView);
		musicViewStart = (Button) findViewById(R.id.musicViewStart);
		musicViewStop = (Button) findViewById(R.id.musicViewStop);
		back = (LinearLayout) findViewById(R.id.bespeak_btn_back);
		main = (Button) findViewById(R.id.t08);
		set = (Button) findViewById(R.id.t09);
		exit = (Button) findViewById(R.id.t10);
		more = (Button) findViewById(R.id.t11);

		back.setOnClickListener(new MyOnclickListener());
		main.setOnClickListener(new MyOnclickListener());
		set.setOnClickListener(new MyOnclickListener());
		exit.setOnClickListener(new MyOnclickListener());
		more.setOnClickListener(new MyOnclickListener());
		intent = getIntent();
		String title = intent.getStringExtra("message_title");
		String content = intent.getStringExtra("message_content");
		String time = intent.getStringExtra("message_time");
		image = intent.getStringExtra("message_img");
		music = intent.getStringExtra("message_music");

		titleView.setText(title);

		timeView.setText(time);

		contentView.setText(content);

		if (music == null) {
			musicViewStart.setEnabled(false);
			musicViewStop.setEnabled(false);
		}

		// 音频
		strVideoURL = music;

		// mTextView01 = (TextView) findViewById(R.id.mTextView01);

		musicViewStart = (Button) findViewById(R.id.musicViewStart);
		musicViewStop = (Button) findViewById(R.id.musicViewStop);

		// 播放
		musicViewStart.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				Uri  uri  =  Uri.parse(music);

                new MediaPlayer();
				MediaPlayer player = MediaPlayer.create(MessageActivity.this, uri);

                player.start();
				
//				mydDialog = ProgressDialog.show(MessageActivity.this,
//						"请稍等              ", "正在下载音频...               ", true);
//
//				// 调用播放影片Function
//				playVideo(strVideoURL);

				// mTextView01.setText(strVideoURL);

			}
		});

		// 停止
		musicViewStop.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				try {
					if (mMediaPlayer01 != null) {
						if (bIsReleased == false) {
							mMediaPlayer01.seekTo(0);
							mMediaPlayer01.pause();
							// mTextView01.setText("已停止");
						}
					}
				} catch (Exception e) {

					mTextView01.setText(e.toString());
					Log.e(TAG, e.toString());
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	protected void onResume() {

		super.onResume();

		AsyncImageLoader loader = new AsyncImageLoader();
		Drawable d = loader.loadDrawable2(image, imageView, new ImageCallback() {
			
			public void imageLoaded(Drawable imageDrawable, ImageView imageView,
					String imageUrl) {
				if(imageDrawable!=null)
					imageView.setImageDrawable(imageDrawable);
			}
		});
		if(d!=null){
			imageView.setImageDrawable(d);
		}
	}

	class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// 子类必须重写此方法,接受数据
		@Override
		public void handleMessage(Message msg) {
			Log.d("MyHandler", "handleMessage......");
			super.handleMessage(msg);
			// 此处可以更新UI

			if (msg.what == 100) {
				imageView.setImageBitmap(bitmap);
				Log.e("HEHE", "HEHE");
			}

		}
	}

	private void playVideo(final String strPath) {

		try {
			if (strPath.equals(currentFilePath) && mMediaPlayer01 != null) {

				mMediaPlayer01.start();
				mydDialog.dismiss();
				return;

			}

			currentFilePath = strPath;

			mMediaPlayer01 = new MediaPlayer();
			mMediaPlayer01.setAudioStreamType(2);

			// 捕抓使用MediaPlayer缓冲区的更新事件
			mMediaPlayer01
					.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

						public void onBufferingUpdate(MediaPlayer mp,
								int percent) {

							Log.e(TAG,
									"Update buffer:"
											+ Integer.toString(percent) + "%");

						}
					});

			// 当MP3播放完毕
			mMediaPlayer01
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

						public void onCompletion(MediaPlayer mp) {

							Log.e(TAG, "mMediaPlayer01 Listener Completed");

						}
					});

			// 当Prepare阶段Listener
			mMediaPlayer01
					.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

						public void onPrepared(MediaPlayer mp) {

							Log.e(TAG, "Prepared Listener");

						}
					});

			// 起一个Runnable来确保文件在存储完之后才开始start()
			Runnable r = new Runnable() {

				public void run() {

					try {
						// setDataSource会将文件存到存储卡
						setDataSource(strPath);

						// 顺序运行
						mMediaPlayer01.prepare();
						Log.e(TAG, "Duration:" + mMediaPlayer01.getDuration());

						// 开始播放
						mMediaPlayer01.start();
						mydDialog.dismiss();
					} catch (Exception e) {

						Log.e(TAG, e.getMessage(), e);

					}
				}
			};
			new Thread(r).start();
		} catch (Exception e) {

			mydDialog.dismiss();

			if (mMediaPlayer01 != null) {

				// 线程异常,终止播放
				mMediaPlayer01.stop();
				mMediaPlayer01.release();

			}
			e.printStackTrace();
		}

	}

	// 存储mp3
	private void setDataSource(String strPath) throws Exception {

		// 判断是否为URL
		if (!URLUtil.isNetworkUrl(strPath)) {

			mMediaPlayer01.setDataSource(strPath);

		} else {
			if (bIsReleased == false) {
				// 创建URL
				URL myURL = new URL(strPath);
				URLConnection conn = myURL.openConnection();
				conn.connect();

				// 取得URLConnection的InputStream
				InputStream is = conn.getInputStream();

				if (is == null) {

					throw new RuntimeException("stream is null");

				}

				// 创建新的临时文件
				File myTempFile = File.createTempFile("hippoplayertem", "."
						+ getFileExtension(strPath));
				currentTempFilePath = myTempFile.getAbsolutePath();

				if (currentTempFilePath != "") {

					Log.i(TAG, currentTempFilePath);

				}

				FileOutputStream fos = new FileOutputStream(myTempFile);
				byte buf[] = new byte[128];
				do {

					int numread = is.read(buf);
					if (numread <= 0) {

						break;
					}
					fos.write(buf, 0, numread);

				} while (true);

				// 直到fos存储完毕
				mMediaPlayer01.setDataSource(currentTempFilePath);
				try {

					is.close();
				} catch (Exception ex) {

					Log.e(TAG, "error:" + ex.getMessage(), ex);
				}
			}
		}
	}

	// 取得音乐文件的扩展名自定义函数
	private String getFileExtension(String strFileName) {

		String strFileExtension = "dat";
		File myFile = new File(strFileName);
		strFileExtension = (null != myFile.getName() && !"".equals(myFile
				.getName())) ? myFile.getName() : "dat";
		strFileExtension = (strFileExtension.substring(strFileExtension
				.lastIndexOf(".") + 1)).toLowerCase();
		return strFileExtension;

	}

	private void delFile(String strFileName) {

		File myFile = new File(strFileName);
		if (myFile.exists()) {

			myFile.delete();
		}

	}

	protected void onPause() {

		if (mMediaPlayer01 != null) {
			if (bIsReleased == false) {
				mMediaPlayer01.seekTo(0);
				mMediaPlayer01.pause();
				// mTextView01.setText("已停止");
			}
		}

		try {

			delFile(currentTempFilePath);

		} catch (Exception e) {

			e.printStackTrace();
		}

		super.onPause();
	}

	public void intentImage(View view) {

		intent = new Intent(MessageActivity.this, MessageImageActivity.class);
		intent.putExtra("image", image);
		startActivity(intent);

	}

	/**
	 * 自定义点击事件监听器
	 * 
	 * @author ChJan
	 * 
	 */
	private class MyOnclickListener implements OnClickListener {

		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.bespeak_btn_back:
				MessageActivity.this.finish();
				break;
			case R.id.t08:
				MessageActivity.this.finish();
				ArrayList<Activity> arrayList = ExitClass.getInstance().getAa();
				for (Activity activity : arrayList) {
					if (activity instanceof DownloadActivity) {
						activity.finish();
					}
				}
				break;
			case R.id.t09:
				intent = new Intent();
				intent.setClass(MessageActivity.this, SetActivity.class);
				startActivity(intent);
				break;
			case R.id.t10:
				exitAlert("你确定退出本系统吗？\n（将无法收到新消息）");
				break;
			case R.id.t11:
				intent = new Intent();
				intent.setClass(MessageActivity.this, MoreActivity.class);
				startActivity(intent);
				break;

			default:
				break;
			}

		}

	}

	/**
	 * 菜单栏退出事件
	 * 
	 * @param msg
	 */

	private void exitAlert(String msg) {
		// 实例化AlertDialog.Builder
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// 设置显示信息
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						ExitClass.getInstance().exit();

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						return;
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
