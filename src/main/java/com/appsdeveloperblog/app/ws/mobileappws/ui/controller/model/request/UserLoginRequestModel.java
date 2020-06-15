package com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.request;

// UserLoginRequestModel contains information that is submitted when a user logs in.
// Used to transform incoming json into a POJO.
// The incoming json fields should match the property names of this class.
public class UserLoginRequestModel {
	private String email;
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}