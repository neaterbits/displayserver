package com.neaterbits.displayserver.protocol.exception;

import com.neaterbits.displayserver.protocol.types.RESOURCE;

public final class IDChoiceException extends ProtocolException {

	private static final long serialVersionUID = 1L;

	private final RESOURCE resource;
	
	public IDChoiceException(String message, RESOURCE resource) {
		super(message);
		
		this.resource = resource;
	}

    public RESOURCE getResource() {
        return resource;
    }
}
