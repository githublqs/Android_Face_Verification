package com.tec.jca.faceverification.domain.exception;

public class UserInfoNotFoundException extends Exception {

	private static final long serialVersionUID = -7784865557223999342L;

	public UserInfoNotFoundException() {
		super();
	}

	public UserInfoNotFoundException(final String message) {
		super(message);
	}

	public UserInfoNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public UserInfoNotFoundException(final Throwable cause) {
		super(cause);
	}
}