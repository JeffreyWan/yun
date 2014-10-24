package com.china.infoway;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MessageImageActivity extends Activity {

	public ProgressDialog mydDialog = null;

	Intent intent;

	Bitmap bitmap;

	private String imageURL;

	private ImageView imageDetail;
	private LinearLayout back;

	private String saveDir = Environment.getExternalStorageDirectory()
			.getPath();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;

	private int mode = NONE;
	private float oldDist;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_message_image);

		intent = getIntent();

		imageURL = intent.getStringExtra("image");

		Log.e("imageURL", imageURL);

		imageDetail = (ImageView) findViewById(R.id.image_detail);
		back = (LinearLayout) findViewById(R.id.bespeak_btn_back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MessageImageActivity.this.finish();
			}
		});

		imageDetail.setScaleType(ScaleType.CENTER);

		imageDetail.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				ImageView view = (ImageView) v;

				view.setScaleType(ScaleType.MATRIX);

				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - start.x,
								event.getY() - start.y);
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				}

				view.setImageMatrix(matrix);
				return true;
			}

			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return (float) java.lang.Math.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
		});

	}

	@Override
	protected void onResume() {

		super.onResume();

		Toast.makeText(getApplicationContext(), "正在加载图片，请稍等。。。",
				Toast.LENGTH_LONG).show();

		new Handler().postDelayed(new Runnable() {
			public void run() {

				// httpURLConnection方式解析
				try {

					URL url = new URL(imageURL);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					bitmap = BitmapFactory.decodeStream(is);
					is.close();

					MyHandler myHandler = new MyHandler();

					myHandler.sendEmptyMessage(100);

					Log.e("HEHE111", "HEHE");

				} catch (Exception e) {

				}

			}
		}, 1000);

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
				imageDetail.setImageBitmap(bitmap);
				Log.e("HEHE", "HEHE");
			}

		}
	}

	public Bitmap getHttpBitmap(String url) {
		Bitmap bitmap = null;
		try {
			URL pictureUrl = new URL(url);
			InputStream in = pictureUrl.openStream();
			bitmap = BitmapFactory.decodeStream(in);
			in.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public void savePicture(Bitmap bitmap) {

		int i = imageURL.lastIndexOf("/");

		String imagesmpURL = imageURL.substring(i + 1, imageURL.length());

		Log.e("imagesmpURL", imagesmpURL);

		String pictureName = saveDir + "/DCIM/Camera/" + imagesmpURL;

		Log.e("!!!!!!!!!!!1", pictureName);

		File file = new File(pictureName);

		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void back(View view) {

		onBackPressed();

	}

	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 1, 1, "保存图片");

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {

			new AlertDialog.Builder(MessageImageActivity.this)
					.setTitle("提示")
					.setMessage("确定保存吗？")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									// mydDialog = ProgressDialog.show(
									// MessageImageActivity.this,
									// "请稍等              ",
									// "正在下载...               ", true);

									Bitmap bitmap = getHttpBitmap(imageURL);// 从网络获取图片

									savePicture(bitmap);

								}
							}).setNegativeButton("否", null).show();

		}
		return true;
	}
}
