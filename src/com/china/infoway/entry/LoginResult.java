package com.china.infoway.entry;

import java.io.Serializable;
import java.util.List;

import android.text.method.DateTimeKeyListener;

public class LoginResult implements Serializable {
	
	public String status;
	public String companyName;
	public List<Type> type;
	
	public static class Type {
		 public String id;
		 public String title;
		 public String remark;
		 public String company_id;
	}
}
