package com.dianping.btmovie.entity;

public class BaseReturn
{
	private int code;
	private String message;
	private Object body;
	
	public BaseReturn(int code, String message, Object body) {
		super();
		this.code = code;
		this.message = message;
		this.body = body;
	}
	public BaseReturn(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	public BaseReturn(){
		
	} 
	public int getCode()
	{
		return code;
	}
	public void setCode(int code)
	{
		this.code = code;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	public Object getBody()
	{
		return body;
	}
	public void setBody(Object body)
	{
		this.body = body;
	}
	
	
}
