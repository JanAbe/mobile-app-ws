package com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.request;

public class PasswordResetModel {
	String token;
	String password;
	
	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}