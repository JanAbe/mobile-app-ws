package com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.response;

import java.util.List;

// UserRest contains all data of the user that gets sent back after a user has been created.
// Used to transform POJO into JSON.
public class UserRest {
	private String userId; // is not the same id as the database user id. -> Why not just use a UUID as database id? Then you can also use it here, as it can't be guessed
	private String firstName;
	private String lastName;
	private String email;
	private List<AddressRest> addresses;

	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

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

	public List<AddressRest> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressRest> addresses) {
		this.addresses = addresses;
	}
}