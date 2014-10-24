package com.china.infoway;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.china.infoway.utils.Constants;

@SuppressLint("HandlerLeak")
public class MoreActivity extends Activity {
	// 版本URL
	private String verUrl = Constants.verUrl;
	private TextView current_version;
	private RelativeLayout update_relativeLayout;
	private LinearLayout moreBackBtn;
	Handler mHandler;
	String currentVersion;
	private RelativeLayout help_relativeLayout;
	private RelativeLayout yewuinfo_relativeLayout;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		ExitClass.getInstance().addActivity(this);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.more);
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Loading...");

		current_version = (TextView) findViewById(R.id.current_version);
		update_relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout3);
		moreBackBtn = (LinearLayout) findViewById(R.id.more_btn_back);
		yewuinfo_relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout1);
		help_relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout2);

		moreBackBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				MoreActivity.this.finish();
			}
		});

		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
		currentVersion = packInfo.versionName;

		current_version.setText(currentVersion);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case Constants.Success:

					showDialog();
					break;
				case Constants.Failure:

					Toast.makeText(getApplicationContext(), "恭喜您，已是最新版本！",
							Toast.LENGTH_SHORT).show();
					break;
				case Constants.CheckVersion:
					checkVersion();
					break;

				default:
					break;
				}

			}
		};

		update_relativeLayout.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				mHandler.sendEmptyMessage(Constants.CheckVersion);

			}
		});

		yewuinfo_relativeLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(MoreActivity.this, "业务尚未开通", Toast.LENGTH_LONG)
						.show();

			}
		});
		help_relativeLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MoreActivity.this, HelpActivity.class);
				startActivity(intent);

			}
		});

	}

	private void showDialog() {
		Dialog dialog = new AlertDialog.Builder(MoreActivity.this)
				.setIcon(android.R.drawable.btn_star)
				.setTitle("软件版本更新")
				.setMessage("有最新的软件包哦，亲，快下载吧~~")
				.setPositiveButton("下载", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = Uri.parse(Constants.downloadUri);
						Intent it = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(it);
					}

				})
				.setNeutralButton("以后再说",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
							}

						}).create();
		dialog.show();
	}

	private void checkVersion() {
		
		mProgressDialog.show();

		new Thread(new Runnable() {
			public void run() {

				// 创建Http get 连接
				HttpGet httpRequest = new HttpGet(verUrl);

				try {
					// 发出HTTP request
					HttpResponse httpResponse = new DefaultHttpClient()
							.execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						String strResult = EntityUtils.toString(httpResponse
								.getEntity(),HTTP.UTF_8);
						strResult = strResult.substring(1, strResult.length());
						// Log.e("strResult========================91",
						// strResult);
//						System.out.println(Double.valueOf(strResult));
						if(strResult.compareTo(currentVersion) > 0){
//						if (Double.valueOf(currentVersion) < Double
//								.valueOf(strResult)) {
							mHandler.sendEmptyMessage(Constants.Success);
						} else {
							mHandler.sendEmptyMessage(Constants.Failure);
						}
					}
				} catch (ClientProtocolException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					if(mProgressDialog != null) {
						mProgressDialog.dismiss();
					}
				}

			}
		}).start();
	}

}
