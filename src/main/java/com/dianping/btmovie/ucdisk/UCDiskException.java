package com.dianping.btmovie.ucdisk;

import java.io.IOException;

public class UCDiskException extends IOException {

	private int statucCode;
	private String message;
	public UCDiskException(int statucCode, String message) {
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
