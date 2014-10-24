package com.china.infoway;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppUtils {
	/**
	 * 检查网络是否连通
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNet(Context context) {
		ConnectivityManager connMaanager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMaanager != null) {
			NetworkInfo networkinfo = connMaanager.getActiveNetworkInfo();
			if (networkinfo != null && networkinfo.isConnected()) {
				if (networkinfo.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 网络设置对话框
	 * 
	 * @param context
	 */
	public static void netErrorAlert(final Context context) {
		Builder builder = new Builder(context);
		builder.setTitle("提示");
		builder.setMessage("是否打开网络");
		builder.setPositiveButton("设置", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Intent intent = new Intent(
						android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				context.startActivity(intent);

			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * 
	 * @param context
	 * @return true 表示开启
	 */
	public static final boolean isOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}

		return false;
	}

	/**
	 * 定位设置对话框
	 * 
	 * @param context
	 */
	public static void setGPSAlert(final Context context) {
		Builder builder = new Builder(context);
		builder.setTitle("提示");
		builder.setMessage("是否打开定位");
		builder.setPositiveButton("设置", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				// ACTION_SECURITY_SETTINGS
				Intent intent = new Intent(
						android.provider.Settings.ACTION_SETTINGS);
				context.startActivity(intent);
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

}
