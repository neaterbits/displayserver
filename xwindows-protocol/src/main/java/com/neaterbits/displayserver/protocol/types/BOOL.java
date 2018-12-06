package com.neaterbits.displayserver.protocol.types;

public final class BOOL {

	private final byte value;

	public BOOL(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public boolean isSet() {
	    return value != 0;
	}
	
    @Override
    public String toString() {
        return value != 0 ? "true" : "false";
    }
}
