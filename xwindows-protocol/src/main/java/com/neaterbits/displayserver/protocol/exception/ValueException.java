package com.neaterbits.displayserver.protocol.exception;

public class ValueException extends ProtocolException {

	private static final long serialVersionUID = 1L;

	private final long value;
	
	public ValueException(String message, long value) {
		super(message);
		
		this.value = value;
	}

    public long getValue() {
        return value;
    }
}
