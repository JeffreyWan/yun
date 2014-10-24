package com.china.infoway.entry;

public class Message {
	private String message_title;
	private String message_content;
	private String message_time;
	private String click_count;
	public String getClick_count() {
		return click_count;
	}
	public void setClick_count(String click_count) {
		this.click_count = click_count;
	}
	public String getMessage_title() {
		return message_title;
	}
	public void setMessage_title(String message_title) {
		this.message_title = message_title;
	}
	public String getMessage_content() {
		return message_content;
	}
	public void setMessage_content(String message_content) {
		this.message_content = message_content;
	}
	public String getMessage_time() {
		return message_time;
	}
	public void setMessage_time(String message_time) {
		this.message_time = message_time;
	}
}
