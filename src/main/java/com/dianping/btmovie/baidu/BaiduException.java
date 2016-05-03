package com.dianping.btmovie.baidu;

import java.io.IOException;

public class BaiduException extends IOException {

	private int statucCode;
	private String message;
	public BaiduException(int statucCode, String message) {
		super();
		this.statucCode = statucCode;
		this.message = message;
	}
	public int getStatucCode() {
		return statucCode;
	}
	public String getMessage() {
		return message;
	}
	
}
