package com.china.infoway.utils;

import java.lang.reflect.Type;

import org.junit.Assert;
import org.junit.Test;

import com.china.infoway.entry.LoginResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JSonTest {
	private LoginResult mLoginResult;
	@Test
	public void test() {
		String str = "{'status':'2','companyName':'\u4e0a\u6d77\u6613\u4fe1\u79d1\u6280\u6709\u9650\u516c\u53f8'," +
				"'type':[{'id':'10','title':'\u901a\u77e5','remark':'\u901a\u77e5\u6a21\u677f\u7c7b\u578b','company_id':'3'}," +
				"{'id':'12','title':'\u5de5\u4f5c\u6c47\u62a5','remark':'\u5de5\u4f5c\u6c47\u62a5\u6a21\u677f'," +
				"'company_id':'3'},{'id':'13','title':'\u7b7e\u5230\u62a5\u544a','remark':'\u7b7e\u5230\u62a5\u544a\u6a21\u677f','company_id':'3'}]}";
		Gson gson = new Gson();
		Type type = new TypeToken<LoginResult>() {}.getType();
		mLoginResult = gson.fromJson(str, type);
		Assert.assertEquals(mLoginResult.status, "2");
	}

}
