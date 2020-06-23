package com.appsdeveloperblog.app.ws.mobileappws.io.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name="password_reset_tokens") // because it's gonna be persisted into the database
public class PasswordResetTokenEntity implements Serializable {

	private static final long serialVersionUID = -1378865640264770818L;

	@Id
	@GeneratedValue
	private long id;

	private String token;

	@OneToOne
	@JoinColumn(name="users_id")
	private UserEntity userDetails;

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserEntity getUserDetails() {
		return this.userDetails;
	}

	public void setUserDetails(UserEntity userEntity) {
		this.userDetails = userEntity;
	}

}