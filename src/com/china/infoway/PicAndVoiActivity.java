package com.china.infoway;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.china.infoway.utils.Constants;
import com.china.infoway.utils.GalleryFlow;
import com.google.gson.Gson;

public class PicAndVoiActivity extends Activity {

	private StringBuffer sb;
	private StringBuffer imageUploadsb;
	private String imageUpload;
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

	private EditText other_remark;

	private Button submitBtn;

	private Cursor cursor = null;

	// 实时定位成功时返回的id
	private String tmpph;

	private String str_explain;
	private String str_photo;
	private String str_voice;

	private Intent intent;
	private int click_count = 0;

	private SharedPreferences spPreferences;

	// KIMI0311H
	private Button exit;
	private Button more;
	private Button set;
	private Button main;
	private GalleryFlow galleryFlow;
	private ImageAdapter adapter;
	private List<String> paths;
	private Handler handler;

	private List<String> picsPath = new ArrayList<String>();
	private String voiPath;

	private ProgressDialog mProgressDialog;
	
	private Spinner sp_bType;
	private int sp_position;
	private String types[];
	private ArrayAdapter<String> spAdapter;
	private EditText et_theme;

	protected void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picandvoi);

		intent = getIntent();
		
		types = new String[LoginActivity.mLoginResult.type.size()];
		for(int i = 0; i < LoginActivity.mLoginResult.type.size(); i++) {
			types[i] = LoginActivity.mLoginResult.type.get(i).title;
		}
		
		sp_bType = (Spinner) findViewById(R.id.sp_bType);
		spAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);	
		spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_bType.setAdapter(spAdapter);
		sp_bType.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				sp_position = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		other_remark = (EditText) findViewById(R.id.other_remark);
		galleryFlow = (GalleryFlow) findViewById(R.id.gallery);
		galleryFlow.setVisibility(View.GONE);

		paths = new ArrayList<String>();
		sb = new StringBuffer();
		imageUploadsb = new StringBuffer();

//				mProgressDialog = new ProgressDialog(PicAndVoiActivity.this);
//				mProgressDialog.setMessage("Loading...");

		back = (LinearLayout) findViewById(R.id.bespeak_btn_back);
		et_theme = (EditText) findViewById(R.id.et_theme);
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

		submitBtn = (Button) findViewById(R.id.submitBtn);

		spPreferences = getSharedPreferences("PHONE", MODE_PRIVATE);

		tmpph = spPreferences.getString("phone", "");

		/**
		 * 点击提交按钮
		 */
		submitBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				str_explain = other_remark.getText().toString();

				if (str_explain.equals("")) {
					Toast.makeText(getApplicationContext(), "请填写至少一些信息!",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
									
//					mProgressDialog.show();

					if (str_photo == null && str_voice == null) {
						new Thread() {
							@Override
							public void run() {
								try {
									post(Constants.picAndVoiUrl);
								} catch (ClientProtocolException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}.start();
					}
					else {
						
						if(str_voice == null) {
							new Thread() {
								@Override
								public void run() {
									try {
										post(picsPath, Constants.picAndVoiUrl);
									} catch (ClientProtocolException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}.start();
						}else if(str_photo == null) {
							new Thread() {
								@Override
								public void run() {
									try {
										post(voiPath, Constants.picAndVoiUrl);
									} catch (ClientProtocolException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}.start();
						}else {
							new Thread() {
								@Override
								public void run() {
									try {
										post(picsPath, voiPath, Constants.picAndVoiUrl);
									} catch (ClientProtocolException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}.start();
						}
					}
				}
			}
		});

		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				switch (msg.what) {
				case 10:
					Toast.makeText(getApplicationContext(), "上传失败！",
							Toast.LENGTH_SHORT).show();
					break;
				case 11:
					Toast.makeText(getApplicationContext(), "上传成功！",
							Toast.LENGTH_SHORT).show();
					PicAndVoiActivity.this.finish();
					break;
				case 111:
					mProgressDialog.show();
					break;
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
					str_photo = str + ".jpg";

					picsPath.add(saveDir + str + ".jpg");

					imageUpload = saveDir + str_photo;
					imageUploadsb.append(imageUpload).append("#");
					sb.append(str_photo).append("#");

					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
							Toast.makeText(PicAndVoiActivity.this, "文件不存在!",
									Toast.LENGTH_LONG).show();
							return;
						}
					}
					Intent intent = new Intent(
							"android.media.action.IMAGE_CAPTURE");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
					startActivityForResult(intent, 1);

				} else {
					Toast.makeText(PicAndVoiActivity.this, "sdcard不存在",
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

					if (!myRecAudioFile.exists()) {
						myRecAudioFile.createNewFile();
					}
					str_voice = strVoice + ".3gp";

					voiPath = saveMusicDir + str_voice;

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

				MediaPlayer player = new MediaPlayer();
				try {
					player.setDataSource(voiPath);
					player.prepare();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				player.start();
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

	protected void onStart() {

		if (click_count == 1) {
			bt_camera.setClickable(false);
			bt_camera.setVisibility(View.GONE);

			bt_voice_start.setClickable(false);
			bt_voice_start.setVisibility(View.GONE);
			bt_voice_stop.setClickable(false);
			bt_voice_stop.setVisibility(View.GONE);

			submitBtn.setClickable(false);
			submitBtn.setVisibility(View.GONE);

		}
		super.onStart();

		String onStartmessage8Voice = null;

		if (str_voice == null) {
			if (onStartmessage8Voice != null) {

				bt_voice_src.setEnabled(true);

			} else {

				bt_voice_src.setEnabled(false);
			}
		} else {

			bt_voice_src.setEnabled(true);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == RESULT_OK) {

			if (file != null && file.exists()) {
				galleryFlow.setVisibility(View.VISIBLE);
				paths.add(compressImage(file).getPath());

				adapter = new ImageAdapter(PicAndVoiActivity.this, paths);
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

	/**
	 * 上传图片和语音到服务器
	 * @param picsPath-图片地址和名称
	 * @param voiPath-语音地址和名称
	 * @param urlServer-服务器方法地址
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void post(List<String> picsPath, String voiPath, String urlServer)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient httpclient = new DefaultHttpClient();
		// 设置通信协议版本
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(urlServer);

		MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
		ContentBody cbFile;
		List<File> fileList = new ArrayList<File>();
		for (int i = 0; i < picsPath.size(); i++) {
			File file = new File(picsPath.get(i));
			fileList.add(file);
		}

		File voiFile = new File(voiPath);

		for (int i = 1; i <= fileList.size(); i++) {
			cbFile = new FileBody(fileList.get(i - 1));
			mpEntity.addPart("picsfile" + i, cbFile);
		}

		cbFile = new FileBody(voiFile);
		mpEntity.addPart("voiFile", cbFile);

		mpEntity.addPart("title", new StringBody(URLEncoder.encode(et_theme.getText().toString())));
		mpEntity.addPart("typeId", new StringBody(LoginActivity.mLoginResult.type.get(sp_position).id));
		mpEntity.addPart("phone", new StringBody(tmpph));
		mpEntity.addPart("latitude", new StringBody(BMapApiDemoApp.latitude
				+ ""));
		mpEntity.addPart("longitude", new StringBody(BMapApiDemoApp.longitude
				+ ""));
		if (BMapApiDemoApp.currAddrStr == null)
			mpEntity.addPart("addrStr",
					new StringBody(URLEncoder.encode("位置不详")));
		else
			mpEntity.addPart(
					"addrStr",
					new StringBody(URLEncoder
							.encode(BMapApiDemoApp.currAddrStr)));
		mpEntity.addPart(
				"remark",
				new StringBody(URLEncoder.encode(other_remark.getText()
						.toString())));
		httppost.setEntity(mpEntity);

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();

		String json = "";
		if (resEntity != null) {
			json = EntityUtils.toString(resEntity, "utf-8");
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}

		httpclient.getConnectionManager().shutdown();

		if (json.contains("0"))
			handler.sendEmptyMessage(10);
		else if (json.contains("1")) {
			handler.sendEmptyMessage(11);
		}
	}

	/**
	 * 上传图片到服务器
	 * @param picsPath-图片地址和名称
	 * @param urlServer-服务器方法地址
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 */
	public void post(List<String> picsPath, String urlServer)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient httpclient = new DefaultHttpClient();
		// 设置通信协议版本
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(urlServer);

		MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
		ContentBody cbFile;
		List<File> fileList = new ArrayList<File>();
		for (int i = 0; i < picsPath.size(); i++) {
			File file = new File(picsPath.get(i));
			fileList.add(file);
		}

		for (int i = 1; i <= fileList.size(); i++) {
			cbFile = new FileBody(fileList.get(i - 1));
			mpEntity.addPart("picsfile" + i, cbFile);
		}

		mpEntity.addPart("title", new StringBody(URLEncoder.encode(et_theme.getText().toString())));
		mpEntity.addPart("typeId", new StringBody(LoginActivity.mLoginResult.type.get(sp_position).id));
		mpEntity.addPart("phone", new StringBody(tmpph));
		mpEntity.addPart("latitude", new StringBody(BMapApiDemoApp.latitude
				+ ""));
		mpEntity.addPart("longitude", new StringBody(BMapApiDemoApp.longitude
				+ ""));
		if (BMapApiDemoApp.currAddrStr == null)
			mpEntity.addPart("addrStr",
					new StringBody(URLEncoder.encode("位置不详")));
		else
			mpEntity.addPart(
					"addrStr",
					new StringBody(URLEncoder
							.encode(BMapApiDemoApp.currAddrStr)));
		mpEntity.addPart(
				"remark",
				new StringBody(URLEncoder.encode(other_remark.getText()
						.toString())));
		httppost.setEntity(mpEntity);

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();

		String json = "";
		if (resEntity != null) {
			json = EntityUtils.toString(resEntity, "utf-8");
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}

		httpclient.getConnectionManager().shutdown();

		if (json.contains("0"))
			handler.sendEmptyMessage(10);
		else if (json.contains("1")) {
			handler.sendEmptyMessage(11);
		}
	}

	public void post(String urlServer) throws ClientProtocolException,
			IOException, JSONException {

		HttpClient httpclient = new DefaultHttpClient();
		// 设置通信协议版本
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(urlServer);

		MultipartEntity mpEntity = new MultipartEntity(); // 文件传输

		mpEntity.addPart("title", new StringBody(URLEncoder.encode(et_theme.getText().toString())));
		mpEntity.addPart("typeId", new StringBody(LoginActivity.mLoginResult.type.get(sp_position).id));
		mpEntity.addPart("phone", new StringBody(tmpph));
		mpEntity.addPart("latitude", new StringBody(BMapApiDemoApp.latitude
				+ ""));
		mpEntity.addPart("longitude", new StringBody(BMapApiDemoApp.longitude
				+ ""));
		if (BMapApiDemoApp.currAddrStr == null)
			mpEntity.addPart("addrStr",
					new StringBody(URLEncoder.encode("位置不详")));
		else
			mpEntity.addPart(
					"addrStr",
					new StringBody(URLEncoder
							.encode(BMapApiDemoApp.currAddrStr)));
		mpEntity.addPart(
				"remark",
				new StringBody(URLEncoder.encode(other_remark.getText()
						.toString())));
		httppost.setEntity(mpEntity);

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();

		System.out.println(response.getStatusLine());// 通信Ok
		String json = "";
		if (resEntity != null) {
			json = EntityUtils.toString(resEntity, "UTF-8");
		}
		
		if (resEntity != null) {
			resEntity.consumeContent();
		}
		httpclient.getConnectionManager().shutdown();
		if (json.contains("0"))
			handler.sendEmptyMessage(10);
		else if (json.contains("1")) {
			handler.sendEmptyMessage(11);
		}
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
				PicAndVoiActivity.this.finish();
				break;
			case R.id.t08:
				PicAndVoiActivity.this.finish();
				ArrayList<Activity> arrayList = ExitClass.getInstance().getAa();
				for (Activity activity : arrayList) {
					if (activity instanceof InformationAppActivity) {
						activity.finish();
					}
				}
				break;
			case R.id.t09:

				intent = new Intent();
				intent.setClass(PicAndVoiActivity.this, SetActivity.class);
				startActivity(intent);
				break;
			case R.id.t10:

				exitAlert("你确定退出本系统吗？\n（将无法收到新消息）");
				break;
			case R.id.t11:
				intent = new Intent();
				intent.setClass(PicAndVoiActivity.this, MoreActivity.class);
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
	
	public void post(String voiPath, String urlServer)
			throws ClientProtocolException, IOException, JSONException {

		HttpClient httpclient = new DefaultHttpClient();
		// 设置通信协议版本
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(urlServer);

		MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
		ContentBody cbFile;
			File file = new File(voiPath);

			cbFile = new FileBody(file);
			mpEntity.addPart("voiFile", cbFile);

		mpEntity.addPart("title", new StringBody(URLEncoder.encode(et_theme.getText().toString())));
		mpEntity.addPart("typeId", new StringBody(LoginActivity.mLoginResult.type.get(sp_position).id));
		mpEntity.addPart("phone", new StringBody(tmpph));
		mpEntity.addPart("latitude", new StringBody(BMapApiDemoApp.latitude
				+ ""));
		mpEntity.addPart("longitude", new StringBody(BMapApiDemoApp.longitude
				+ ""));
		if (BMapApiDemoApp.currAddrStr == null)
			mpEntity.addPart("addrStr",
					new StringBody(URLEncoder.encode("位置不详")));
		else
			mpEntity.addPart(
					"addrStr",
					new StringBody(URLEncoder
							.encode(BMapApiDemoApp.currAddrStr)));
		mpEntity.addPart(
				"remark",
				new StringBody(URLEncoder.encode(other_remark.getText()
						.toString())));
		httppost.setEntity(mpEntity);

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

		if (json.contains("0"))
			handler.sendEmptyMessage(10);
		else if (json.contains("1")) {
			handler.sendEmptyMessage(11);
		}
	}
}
