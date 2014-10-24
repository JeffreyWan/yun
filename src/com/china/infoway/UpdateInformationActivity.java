package com.china.infoway;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.china.infoway.utils.Constants;
import com.china.infoway.utils.GalleryFlow;
import com.china.infoway.utils.MyDatabaseHelper;

@SuppressLint({ "SimpleDateFormat", "CutPasteId", "DefaultLocale",
		"HandlerLeak" })
public class UpdateInformationActivity extends Activity {

	private String messageSubmit7;
	private String messageSubmit8;
	private StringBuffer sb;
	private StringBuffer imageUploadsb;
	private StringBuffer messageSubmitsb;
	private String imageUpload;
	private String sqliteImagePath;
	private String sqliteMusicPath;
	private String messageSubmitPost;
	private AnimationSet manimationSet;

	private Button bt_camera;
	private Bitmap photo;
	private File file;

	private String saveDir = Environment.getExternalStorageDirectory()
			.getPath() + "/temp_image/";

	private String saveMusicDir = Environment.getExternalStorageDirectory()
			.getPath() + "/temp_voice/";

	private Button bt_voice_start;
	private Button bt_voice_stop;
	private Button bt_voice_src;
	private LinearLayout back;

	private File myRecAudioFile;
	private File myRecAudioDir;
	private File myPlayFile;
	private MediaRecorder mMediaRecorder01;

	private boolean sdCardExit;
	private boolean isStopRecord;

	private EditText company_name;
	private EditText contact_person;
	private EditText contact_phone;
	private EditText pending_things;
	private EditText work_results;
	private EditText other_remark;

	private Button submitBtn, updateBtn;

	private Cursor cursor = null;

	// 实时定位成功时返回的id
	private String statue;
	private String tmpph;

	private String str_companyName;
	private String str_contactName;
	private String str_pending;
	private String str_work_Results;
	private String str_contactPhone;
	private String str_otherRemark;
	private String str_photograph;
	private String str_music;

	private Intent intent;
	private String location_time;
	private String time;
	private int click_count = 0;

	private SharedPreferences spPreferences;

	private String informationUri = Constants.informationUri;

	// http请求时的url格式
	private SQLiteDatabase db;
	private MyDatabaseHelper helper;

	// KIMI0311H
	private String imageUploadFile;
	private String musicUploadFile;
	private String imageActionUrl = Constants.imageActionUrl;
	private String musicActionUrl = Constants.musicActionUrl;
	private Button exit;
	private Button more;
	private Button set;
	private Button main;
	private GalleryFlow galleryFlow;
	private ImageAdapter adapter;
	private List<String> paths;
	private Handler handler;

	protected void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updateinformation);

		helper = new MyDatabaseHelper(this, MyDatabaseHelper.DATA_NAME, null,
				MyDatabaseHelper.DATA_VERSION);

		db = helper.getWritableDatabase();

		intent = getIntent();

		try {
			location_time = new String(intent.getStringExtra("location_time").getBytes(),"UTF-8");
			statue = URLEncoder.encode(intent.getStringExtra("statue"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		company_name = (EditText) findViewById(R.id.company_name);
		contact_person = (EditText) findViewById(R.id.contact_person);
		contact_phone = (EditText) findViewById(R.id.contact_phone);
		pending_things = (EditText) findViewById(R.id.pending_things);
		work_results = (EditText) findViewById(R.id.work_results);
		other_remark = (EditText) findViewById(R.id.other_remark);
		galleryFlow = (GalleryFlow) findViewById(R.id.gallery);
		galleryFlow.setVisibility(View.GONE);

		paths = new ArrayList<String>();
		sb = new StringBuffer();
		imageUploadsb = new StringBuffer();
		messageSubmitsb = new StringBuffer();

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

		bt_voice_start = (Button) findViewById(R.id.bt_voice_start);
		bt_voice_stop = (Button) findViewById(R.id.bt_voice_stop);
		bt_voice_src = (Button) findViewById(R.id.bt_voice_src);

		updateBtn = (Button) findViewById(R.id.updateBtn);
		submitBtn = (Button) findViewById(R.id.submitBtn);

		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);

		tmpph = spPreferences.getString("phone", "");
		showContent();
		/**
		 * 点击保存按钮
		 */
		updateBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				// 得到输入框中的内容
				String message1 = company_name.getText().toString();
				String message2 = contact_person.getText().toString();
				final String message3 = contact_phone.getText().toString();
				String message4 = pending_things.getText().toString();
				String message5 = work_results.getText().toString();
				String message6 = other_remark.getText().toString();
				// String message7 = str7;
				String message7 = sqliteImagePath;
				String message8 = str_music;

				if (!TextUtils.isEmpty(message3)) {
					if (!message3.matches("1[0-9]{10}")) {
						Toast.makeText(getApplicationContext(), "手机号码有误",
								Toast.LENGTH_SHORT).show();
						contact_phone.requestFocus();
						return;
					}
				}

				// 进行更新的操作
				ContentValues values = new ContentValues();
				values.put(MyDatabaseHelper.COL_COMPANYNAME, message1);
				values.put(MyDatabaseHelper.COL_CONTACTPERSON, message2);
				values.put(MyDatabaseHelper.COL_CONTACTPHONE, message3);
				values.put(MyDatabaseHelper.COL_PENDINGTHINGS, message4);
				values.put(MyDatabaseHelper.COL_WORKRESULTS, message5);
				values.put(MyDatabaseHelper.COL_REMARK, message6);
				values.put(MyDatabaseHelper.COL_IMAGE, message7);
				values.put(MyDatabaseHelper.COL_VOICE, message8);
				values.put(MyDatabaseHelper.COL_COUNT, 0);

				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss");
				time = sDateFormat.format(new Date());

				values.put(MyDatabaseHelper.COL_TIME, time);
				int rows = db.update(MyDatabaseHelper.TABLE_NAME1, values,
						"date_time=?", new String[] { location_time });
				if (rows <= 0) {
					Toast.makeText(getApplicationContext(), "信息保存失败",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					Toast.makeText(getApplicationContext(), "信息保存成功",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		/**
		 * 点击提交按钮
		 */
		submitBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				str_companyName = company_name.getText().toString();
				str_contactName = contact_person.getText().toString();
				str_pending = pending_things.getText().toString();
				str_work_Results = work_results.getText().toString();
				str_contactPhone = contact_phone.getText().toString();
				str_otherRemark = other_remark.getText().toString();

				if (str_companyName.equals("") && str_contactName.equals("") && str_pending.equals("")
						&& str_work_Results.equals("") && str_contactPhone.equals("")
						&& str_otherRemark.equals("")) {
					Toast.makeText(getApplicationContext(), "请填写至少一项信息!",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					if (!TextUtils.isEmpty(str_contactPhone)) {
						if (!str_contactPhone.matches("1[0-9]{10}")) {
							Toast.makeText(getApplicationContext(), "手机号码有误",
									Toast.LENGTH_SHORT).show();
							contact_phone.requestFocus();
							return;
						}
					}

					SimpleDateFormat sDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String tmptime = sDateFormat.format(new Date());

					cursor = db.query(MyDatabaseHelper.TABLE_NAME1, null,
							"date_time = ?", new String[] { location_time },
							null, null, null);
					if (cursor.moveToFirst()) {

						messageSubmit7 = cursor.getString(cursor
								.getColumnIndex("image"));
						messageSubmit8 = cursor.getString(cursor
								.getColumnIndex("voice"));
					}

					if (str_photograph == null) { // 没有点击拍照

						if (messageSubmit7 != null) {
							String messageSubmit = null;
							String[] array = new String[10];
							array = messageSubmit7.split("#");
							for (String a : array) {
								messageSubmit = saveDir + a;
								messageSubmitsb.append(messageSubmit).append(
										"#");

							}
							messageSubmitPost = messageSubmitsb
									.toString()
									.substring(
											0,
											messageSubmitsb.toString().length() - 1);

							new Thread() {
								@Override
								public void run() {
									String messageSubmit7Response = "";
									try {
										messageSubmit7Response = post(
												messageSubmitPost,
												imageActionUrl);
									} catch (ClientProtocolException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									} catch (JSONException e) {
										e.printStackTrace();
									}
									if (!messageSubmit7Response
											.contains("success")) {
										handler.sendEmptyMessage(1);
										return;
									}
								}
							}.start();
						}

					} else {
						new Thread() {
							@Override
							public void run() {
								String imageUploadFileResponse = "";
								try {
									imageUploadFileResponse = post(
											imageUploadFile, imageActionUrl);
								} catch (ClientProtocolException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								} catch (JSONException e) {
									e.printStackTrace();
								}
								if (!imageUploadFileResponse
										.contains("success")) {
									handler.sendEmptyMessage(1);
									return;
								}
							}
						}.start();

					}

					if (str_music == null) {

						if (messageSubmit8 != null) {
							new Thread() {
								@Override
								public void run() {
									try {
										post(saveMusicDir + messageSubmit8,
												musicActionUrl);
									} catch (ClientProtocolException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}.start();
						}

					} else {
						new Thread() {
							@Override
							public void run() {
								try {
									post(musicUploadFile, musicActionUrl);
								} catch (ClientProtocolException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}.start();
					}

					isInformationAvailable(tmptime);
					update();

					Intent intent = new Intent(UpdateInformationActivity.this,
							InformationAppActivity.class);
					// intent.putExtra("click_count", click_count);
					/* UpdateInformationActivity.this.startActivity(intent); */
					setResult(1, intent);
					UpdateInformationActivity.this.finish();

				}
			}
		});

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					Toast.makeText(UpdateInformationActivity.this, "上传失败",
							Toast.LENGTH_SHORT).show();
				}
			}

		};

		bt_camera = (Button) findViewById(R.id.imageBtn);

		bt_camera.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {

					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd-HH-mm-ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String str = tmpph + "_" + formatter.format(curDate);

					file = new File(saveDir, str + ".jpg");
					str_photograph = str + ".jpg";

					imageUpload = saveDir + str_photograph;
					imageUploadsb.append(imageUpload).append("#");
					imageUploadFile = imageUploadsb.toString().substring(0,
							imageUploadsb.toString().length() - 1);
					// 构建sqlite数据库存储的路径
					sb.append(str_photograph).append("#");

					// 一张照片sqliteImagePath-----13122516402_2013-07-08-11-46-47.jpg
					// 多张照片sqliteImagePath-----
					// 13122516402_2013-07-08-11-46-47.jpg&13122516402_2013-07-08-11-47-44.jpg
					sqliteImagePath = sb.toString().substring(0,
							sb.toString().length() - 1);
					// file.delete();
					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(UpdateInformationActivity.this,
									"文件不存在!", Toast.LENGTH_LONG).show();
							return;
						}
					}
					Intent intent = new Intent(
							"android.media.action.IMAGE_CAPTURE");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					startActivityForResult(intent, 1);

				} else {
					Toast.makeText(UpdateInformationActivity.this, "sdcard不存在",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		File savePath = new File(saveDir);
		if (!savePath.exists()) {
			savePath.mkdirs();
		}
		bt_voice_stop.setEnabled(false);

		// 判断SD Card是否插入
		sdCardExit = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		// 取得SD Card路径作为录音的文件位置
		if (sdCardExit) {
			// myRecAudioDir = Environment.getExternalStorageDirectory();
			myRecAudioDir = new File(Environment.getExternalStorageDirectory()
					.toString() + "/temp_voice");

			File savePathVoice = new File(Environment
					.getExternalStorageDirectory().toString() + "/temp_voice");
			if (!savePathVoice.exists()) {
				savePathVoice.mkdirs();
			}
		}
		// 录音
		bt_voice_start.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					if (!sdCardExit) {
						Toast.makeText(getApplicationContext(), "请插入SD Card",
								Toast.LENGTH_SHORT).show();
						return;
					}
					SimpleDateFormat formatterVoice = new SimpleDateFormat(
							"yyyy-MM-dd-HH-mm-ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String strVoice = tmpph + "_"
							+ formatterVoice.format(curDate);
					Log.e("TIME", strVoice);

					// // 创建录音频文件
					myRecAudioFile = new File(myRecAudioDir, strVoice + ".3gp");
					str_music = strVoice + ".3gp";

					musicUploadFile = saveMusicDir + str_music;

					mMediaRecorder01 = new MediaRecorder();
					// 设置录音音源为麦克风
					mMediaRecorder01
							.setAudioSource(MediaRecorder.AudioSource.MIC);
					mMediaRecorder01
							.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					mMediaRecorder01
							.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
					mMediaRecorder01.setOutputFile(myRecAudioFile
							.getAbsolutePath());
					mMediaRecorder01.prepare();
					mMediaRecorder01.start();

					bt_voice_start.setEnabled(false);
					bt_voice_stop.setEnabled(true);
					bt_voice_src.setEnabled(false);
					bt_voice_start.setBackgroundResource(R.drawable.greeyvoice);
					bt_voice_stop.setBackgroundResource(R.drawable.whitestop);
					bt_voice_src.setBackgroundResource(R.drawable.greeyplay);
					isStopRecord = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// 停止
		bt_voice_stop.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (myRecAudioFile != null) {
					// 停止录音
					mMediaRecorder01.stop();
					mMediaRecorder01.release();
					mMediaRecorder01 = null;

					bt_voice_start.setEnabled(true);
					bt_voice_stop.setEnabled(false);
					bt_voice_src.setEnabled(true);
					bt_voice_start.setBackgroundResource(R.drawable.whitevoice);
					bt_voice_stop.setBackgroundResource(R.drawable.greeystop);
					bt_voice_src.setBackgroundResource(R.drawable.whiteplay);
					isStopRecord = true;
				}

			}
		});

		bt_voice_src.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				String message8Voice = null;

				cursor = db.query(MyDatabaseHelper.TABLE_NAME1, null,
						"date_time = ?", new String[] { location_time }, null,
						null, null);
				if (cursor.moveToFirst()) {

					message8Voice = cursor.getString(cursor
							.getColumnIndex("voice"));
				}

				if (str_music == null) {
					if (message8Voice != null) {
						myPlayFile = new File(Environment
								.getExternalStorageDirectory().toString()
								+ "/temp_voice/" + message8Voice);
						openFile(myPlayFile);
					} else {
						Toast.makeText(getApplicationContext(), "无语音信息",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					myPlayFile = new File(Environment
							.getExternalStorageDirectory().toString()
							+ "/temp_voice/" + str_music);
					openFile(myPlayFile);
				}
			}
		});

	}

	@Override
	protected void onStop() {

		super.onStop();

		if (mMediaRecorder01 != null && !isStopRecord) {

			// 停止录音
			mMediaRecorder01.stop();
			mMediaRecorder01.release();
			mMediaRecorder01 = null;

		}

	}

	private void openFile(File f) {

		intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);

	}

	private String getMIMEType(File f) {

		String end = f
				.getName()
				.substring(f.getName().lastIndexOf(".") + 1,
						f.getName().length()).toLowerCase();
		String type = "";
		if (end.equals("3gp") || end.equals("aac") || end.equals("aac")
				|| end.equals("3gp") || end.equals("mpeg") || end.equals("mp4")) {
			type = "audio";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg")) {
			type = "image";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}

	/**
	 * 更新本地数据
	 */
	private void update() {
		// 得到输入框中的内容
		String message1 = company_name.getText().toString();
		String message2 = contact_person.getText().toString();
		String message3 = contact_phone.getText().toString();
		String message4 = pending_things.getText().toString();
		String message5 = work_results.getText().toString();
		String message6 = other_remark.getText().toString();
		String message7 = sqliteImagePath;
		String message8 = str_music;

		// 呵呵
		String onStartmessage7Image = null;
		String onStartmessage8Voice = null;

		cursor = db.query(MyDatabaseHelper.TABLE_NAME1, null, "date_time = ?",
				new String[] { location_time }, null, null, null);
		if (cursor.moveToFirst()) {

			onStartmessage7Image = cursor.getString(cursor
					.getColumnIndex("image"));
			onStartmessage8Voice = cursor.getString(cursor
					.getColumnIndex("voice"));
		}

		if (str_photograph == null) {
			message7 = onStartmessage7Image;
		}

		if (str_music == null) {
			message8 = onStartmessage8Voice;
		}

		// 进行更新的操作
		ContentValues values = new ContentValues();
		values.put(MyDatabaseHelper.COL_COMPANYNAME, message1);
		values.put(MyDatabaseHelper.COL_CONTACTPERSON, message2);
		values.put(MyDatabaseHelper.COL_CONTACTPHONE, message3);
		values.put(MyDatabaseHelper.COL_PENDINGTHINGS, message4);
		values.put(MyDatabaseHelper.COL_WORKRESULTS, message5);
		values.put(MyDatabaseHelper.COL_REMARK, message6);
		values.put(MyDatabaseHelper.COL_IMAGE, message7);
		values.put(MyDatabaseHelper.COL_VOICE, message8);
		values.put(MyDatabaseHelper.COL_COUNT, 1);

		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		time = sDateFormat.format(new Date());

		values.put(MyDatabaseHelper.COL_TIME, time);
		int rows = db.update(MyDatabaseHelper.TABLE_NAME1, values,
				"date_time=?", new String[] { location_time });
		if (rows <= 0) {
			Toast.makeText(getApplicationContext(), "对不起，信息上传失败，请重试",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			Toast.makeText(UpdateInformationActivity.this, "上传成功",
					Toast.LENGTH_SHORT).show();
		}
	}

	protected void onStart() {

		if (click_count == 1) {
			bt_camera.setClickable(false);
			bt_camera.setVisibility(View.GONE);

			bt_voice_start.setClickable(false);
			bt_voice_start.setVisibility(View.GONE);
			bt_voice_stop.setClickable(false);
			bt_voice_stop.setVisibility(View.GONE);

			updateBtn.setClickable(false);
			// 设置按钮隐藏不可见
			updateBtn.setVisibility(View.GONE);
			submitBtn.setClickable(false);
			submitBtn.setVisibility(View.GONE);

		}
		super.onStart();

		String onStartmessage8Voice = null;

		cursor = db.query(MyDatabaseHelper.TABLE_NAME1, null, "date_time = ?",
				new String[] { location_time }, null, null, null);
		if (cursor.moveToFirst()) {

			onStartmessage8Voice = cursor.getString(cursor
					.getColumnIndex("voice"));
		}

		if (str_music == null) {
			if (onStartmessage8Voice != null) {

				bt_voice_src.setEnabled(true);

			} else {

				bt_voice_src.setEnabled(false);
			}
		} else {

			bt_voice_src.setEnabled(true);
		}

	}

	public void showContent() {
		cursor = db.query(MyDatabaseHelper.TABLE_NAME1, null, "date_time = ?",
				new String[] { location_time }, null, null, null);
		if (cursor.moveToFirst()) {
			String message1 = cursor.getString(cursor
					.getColumnIndex("company_name"));
			String message2 = cursor.getString(cursor
					.getColumnIndex("contact_person"));
			String message3 = cursor.getString(cursor
					.getColumnIndex("contact_phone"));
			String message4 = cursor.getString(cursor
					.getColumnIndex("pending_things"));
			String message5 = cursor.getString(cursor
					.getColumnIndex("work_results"));
			String message6 = cursor.getString(cursor
					.getColumnIndex("other_remark"));
			String message7 = cursor.getString(cursor.getColumnIndex("image"));
			String message8 = cursor.getString(cursor.getColumnIndex("voice"));
			click_count = cursor.getInt(cursor.getColumnIndex("btn_count"));
			company_name.setText(message1);
			contact_person.setText(message2);
			contact_phone.setText(message3);
			pending_things.setText(message4);
			work_results.setText(message5);
			other_remark.setText(message6);

			String SDCarePath = Environment.getExternalStorageDirectory()
					.toString();
			String filePath;
			List<String> paths2 = new ArrayList<String>();
			if (message7 != null && !message7.equals("")) {

				galleryFlow.setVisibility(View.VISIBLE);

				String[] array = new String[10];
				array = message7.split("#");
				for (String a : array) {
					filePath = SDCarePath + "/temp_image/" + a;
					paths2.add(filePath);
				}
			} else {
				// galleryFlow.setVisibility(View.GONE);
				filePath = SDCarePath + "/temp_image/" + message7;
				paths2.add(filePath);
			}

			adapter = new ImageAdapter(UpdateInformationActivity.this, paths2);
			galleryFlow.setAdapter(adapter);
			galleryFlow.setSelection(adapter.getCount() / 2);
			galleryFlow
					.setOnItemClickListener(new GalleryOnItemClickListener());

			String SDCarePathVoice = Environment.getExternalStorageDirectory()
					.toString();
			String filePathVoice = SDCarePathVoice + "/temp_voice/" + message8;

		}
	}

	private void isInformationAvailable(final String time) {
		new Thread() {

			@Override
			public void run() {
				super.run();

				if (str_photograph == null) {
					if (messageSubmit7 != null) {
						str_photograph = messageSubmit7;
					} else {
						str_photograph = "";
					}
				} else {
					str_photograph = sqliteImagePath;
				}
				if (str_music == null) {
					if (messageSubmit8 == null) {
						str_music = "";
					} else {
						str_music = messageSubmit8;
					}
				}

				String url = null;
				try {
					Log.i("str6", str_otherRemark);
					url = informationUri + statue + "&company_name="
							+ URLEncoder.encode(str_companyName, "UTF-8")
							+ "&contact_person="
							+ URLEncoder.encode(str_contactName, "UTF-8")
							+ "&contact_phone="
							+ URLEncoder.encode(str_contactPhone, "UTF-8")
							+ "&work_content="
							+ URLEncoder.encode(str_pending, "UTF-8")
							+ "&work_results="
							+ URLEncoder.encode(str_work_Results, "UTF-8") + "&remark="
							+ URLEncoder.encode(str_otherRemark, "UTF-8") + "&duartime="
							+ time + "&work_img="
							+ URLEncoder.encode(str_photograph, "UTF-8") + "&work_music="
							+ URLEncoder.encode(str_music, "UTF-8");

					Log.v("url", url);

				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				String mUrl = url.replaceAll(" ", "%20");
				HttpPost httpRequest = new HttpPost(mUrl);
				HttpResponse httpResponse = null;
				try {
					httpResponse = new DefaultHttpClient().execute(httpRequest);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				int res = httpResponse.getStatusLine().getStatusCode();
				if (res == 200) {
					try {
						String isInformationAvailableResponse = EntityUtils
								.toString(httpResponse.getEntity());
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getApplicationContext(), "对不起，信息上传失败，请重试",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		}.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == RESULT_OK) {

			if (file != null && file.exists()) {
				galleryFlow.setVisibility(View.VISIBLE);
				paths.add(compressImage(file).getPath());

				adapter = new ImageAdapter(UpdateInformationActivity.this,
						paths);
				galleryFlow.setAdapter(adapter);
				galleryFlow.setSelection(paths.size() - 1);
				galleryFlow
						.setOnItemClickListener(new GalleryOnItemClickListener());
			} else {
				galleryFlow.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onDestroy() {
		destoryImage();
		super.onDestroy();
	}

	private void destoryImage() {
		if (photo != null) {
			photo = null;
		}
	}

	/**
	 * 加载本地图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String post(String pathToOurFile, String urlServer)
			throws ClientProtocolException, IOException, JSONException {
		HttpClient httpclient = new DefaultHttpClient();
		// 设置通信协议版本
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(urlServer);

		MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
		ContentBody cbFile;
		List<File> fileList = new ArrayList<File>();
		String[] uploadFiles = new String[10];
		uploadFiles = pathToOurFile.split("#");
		for (String uploadFile : uploadFiles) {
			File file = new File(uploadFile);
			fileList.add(file);
		}
		for (int i = 1; i <= fileList.size(); i++) {
			cbFile = new FileBody(fileList.get(i - 1));
			mpEntity.addPart("userfile" + i, cbFile);
		}
		mpEntity.addPart("phone", new StringBody(tmpph));
		mpEntity.addPart("type", new StringBody("1"));
		httppost.setEntity(mpEntity);
		System.out.println("executing request " + httppost.getRequestLine());

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();

		System.out.println(response.getStatusLine());// 通信Ok
		String json = "";
		if (resEntity != null) {
			json = EntityUtils.toString(resEntity, "utf-8");
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}

		httpclient.getConnectionManager().shutdown();
		// return path;
		return json;
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
				UpdateInformationActivity.this.finish();
				break;
			case R.id.t08:
				UpdateInformationActivity.this.finish();
				ArrayList<Activity> arrayList = ExitClass.getInstance().getAa();
				for (Activity activity : arrayList) {
					if (activity instanceof InformationAppActivity) {
						activity.finish();
					}
				}
				break;
			case R.id.t09:

				intent = new Intent();
				intent.setClass(UpdateInformationActivity.this,
						SetActivity.class);
				startActivity(intent);
				break;
			case R.id.t10:

				exitAlert("你确定退出本系统吗？\n（将无法收到新消息）");
				break;
			case R.id.t11:
				intent = new Intent();
				intent.setClass(UpdateInformationActivity.this,
						MoreActivity.class);
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

	/**
	 * 适配器
	 * 
	 * @author ChJan
	 * 
	 */

	private class ImageAdapter extends BaseAdapter {

		private Context mContext; // 上下文对象
		private List<String> paths;

		// 构造方法
		public ImageAdapter(Context context, List<String> paths) {
			this.paths = paths;
			this.mContext = context;
		}

		// 获取图片的个数
		public int getCount() {
			return paths.size();
		}

		// 获取图片在库中的位置
		public Object getItem(int position) {
			return paths.get(position);
		}

		// 获取图片在库中的位置
		public long getItemId(int position) {
			return position;
		}

		// 获取适配器中指定位置的视图对象
		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
			Bitmap bitmap;
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inSampleSize = 4;
			bitmap = BitmapFactory.decodeFile(paths.get(position), option);
			final ImageView imageView = new ImageView(mContext);
			// imageView.setImageResource(position);

			imageView.setImageBitmap(bitmap);
			imageView.setLayoutParams(new GalleryFlow.LayoutParams(150, 200));
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

			return imageView;
		}

	}

	/**
	 * 以下是网上压缩图片的例子
	 */
	// private Bitmap compressImage(Bitmap image) {
	// if (image != null) {
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
	// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	// int options = 100;
	// while (baos.toByteArray().length / 1024 > 20) { //
	// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
	// baos.reset();// 重置baos即清空baos
	// image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
	// 这里压缩options%，把压缩后的数据存放到baos中
	// options -= 10;// 每次都减少10
	// }
	// ByteArrayInputStream isBm = new ByteArrayInputStream(
	// baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
	// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
	// 把ByteArrayInputStream数据生成图片
	// return bitmap;
	// }
	// return null;
	// }

	/**
	 * 压缩图片
	 */
	private File compressImage(File file) {
		Bitmap bitmap = null;
		if (file != null) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			// 获取这个图片的宽和高
			bitmap = BitmapFactory.decodeFile(file.getPath(), option);
			// 此时返回bitmap为空
			option.inJustDecodeBounds = false;
			// 计算缩放比
			int be = (int) (option.outHeight / (float) 320);
			if (be <= 0)
				be = 1;
			option.inSampleSize = be;
			bitmap = BitmapFactory.decodeFile(file.getPath(), option);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			int options = 100;

			// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

			while (baos.toByteArray().length / 1024 > 100) {
				// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
				// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			byte[] b = baos.toByteArray();

			return getFileFromBytes(b, file.getPath());
		}
		return null;
	}

	/**
	 * 把字节数组保存为一个文件
	 * 
	 * @param b
	 * @param outputFile
	 * @return
	 */
	public static File getFileFromBytes(byte[] b, String outputFile) {
		File ret = null;
		BufferedOutputStream stream = null;
		try {
			ret = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(ret);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	/**
	 * Gallery 点击放大照片
	 * 
	 * @author ChJan
	 * 
	 */
	private class GalleryOnItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			AnimationSet animationSet = new AnimationSet(true);
			if (manimationSet != null && manimationSet != animationSet) {

				ScaleAnimation scaleAnimation = new ScaleAnimation(2, 1f, 2,
						1f, Animation.RELATIVE_TO_SELF, 0.5f, // 相对于自身缩放到自身的哪个点。
						Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation.setDuration(1000);
				manimationSet.addAnimation(scaleAnimation);
				manimationSet.setFillAfter(true); // 让其保持动画结束时的状态。

				view.startAnimation(manimationSet);
			}

			ScaleAnimation scaleAnimation = new ScaleAnimation(1, 2f, 1, 2f,
					Animation.RELATIVE_TO_SELF, 0.5f, // 相对于自身缩放到自身的哪个点。
					Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(1000);
			animationSet.addAnimation(scaleAnimation);
			animationSet.setFillAfter(true); // 让其保持动画结束时的状态。

			view.startAnimation(animationSet);

			manimationSet = animationSet;

		}

	}

}
