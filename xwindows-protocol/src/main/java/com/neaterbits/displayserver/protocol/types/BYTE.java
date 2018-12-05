package com.neaterbits.displayserver.protocol.types;

public final class BYTE {

	private final byte value;

	public BYTE(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
