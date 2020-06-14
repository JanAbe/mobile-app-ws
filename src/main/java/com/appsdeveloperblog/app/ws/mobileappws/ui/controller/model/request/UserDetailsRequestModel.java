package com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.request;

// UserDetailsRequestModle contains information that is submitted when creating a new user account.
// Used to transform incoming json into a POJO.
// The incoming json fields should match the property names of this class.
public class UserDetailsRequestModel {
	private String firstName;
	private String lastName;
	private String email;
	private String password;	

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

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