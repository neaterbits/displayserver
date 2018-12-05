package com.neaterbits.displayserver.protocol.exception;

public abstract class ProtocolException extends Exception {

	private static final long serialVersionUID = 1L;

	public ProtocolException(String message) {
		super(message);
	}
}
