package com.appsdeveloperblog.app.ws.mobileappws.exceptions;

// create a different exception class for each service

public class UserServiceException extends RuntimeException {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public UserServiceException(String message) {
		super(message);
	}	
}