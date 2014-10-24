package com.china.infoway;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.app.Service;

public class ExitClass extends Application {

	private ArrayList<Activity> aa = new ArrayList<Activity>();
	private ArrayList<Service> bb = new ArrayList<Service>();

	private static ExitClass exitClass;

	public ExitClass() {

	}

	public static ExitClass getInstance() {
		if (exitClass == null) {
			exitClass = new ExitClass();
		}
		return exitClass;
	}

	public void addActivity(Activity activity) {
		aa.add(activity);
	}

	public void addService(Service service) {
		bb.add(service);
	}

	public void exit() {
		for (Activity activity : aa) {
			activity.finish();
		}
		for (Service service : bb) {
			service.stopSelf();
		}
		System.exit(0);
	}

	public ArrayList<Activity> getAa() {
		return aa;
	}

}
