package com.neaterbits.displayserver.protocol.types;

public final class BOOL {

	private final byte value;

	public BOOL(boolean value) {
	    this.value = value ? (byte)1 : (byte)0;
	}

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
