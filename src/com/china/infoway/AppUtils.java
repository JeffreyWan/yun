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
	 * ��������Ƿ���ͨ
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
	 * �������öԻ���
	 * 
	 * @param context
	 */
	public static void netErrorAlert(final Context context) {
		Builder builder = new Builder(context);
		builder.setTitle("��ʾ");
		builder.setMessage("�Ƿ������");
		builder.setPositiveButton("����", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				Intent intent = new Intent(
						android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				context.startActivity(intent);

			}
		});
		builder.setNegativeButton("ȡ��", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	/**
	 * �ж�GPS�Ƿ�����GPS����AGPS����һ������Ϊ�ǿ�����
	 * 
	 * @param context
	 * @return true ��ʾ����
	 */
	public static final boolean isOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// ͨ��GPS���Ƕ�λ����λ������Ծ�ȷ���֣�ͨ��24�����Ƕ�λ��������Ϳտ��ĵط���λ׼ȷ���ٶȿ죩
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// ͨ��WLAN���ƶ�����(3G/2G)ȷ����λ�ã�Ҳ����AGPS������GPS��λ����Ҫ���������ڻ��ڸ������Ⱥ��ï�ܵ����ֵȣ��ܼ��ĵط���λ��
		boolean network = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}

		return false;
	}

	/**
	 * ��λ���öԻ���
	 * 
	 * @param context
	 */
	public static void setGPSAlert(final Context context) {
		Builder builder = new Builder(context);
		builder.setTitle("��ʾ");
		builder.setMessage("�Ƿ�򿪶�λ");
		builder.setPositiveButton("����", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				// ACTION_SECURITY_SETTINGS
				Intent intent = new Intent(
						android.provider.Settings.ACTION_SETTINGS);
				context.startActivity(intent);
			}
		});
		builder.setNegativeButton("ȡ��", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

}
