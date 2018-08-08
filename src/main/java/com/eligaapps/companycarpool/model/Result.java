package com.eligaapps.companycarpool.model;

public class Result {
	private int code = 0;
	private String error;

	public Result() {

	}

	public Result(int code, String error) {
		this.code = code;
		this.error = error;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}