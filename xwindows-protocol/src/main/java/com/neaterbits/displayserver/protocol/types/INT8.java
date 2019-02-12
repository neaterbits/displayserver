package com.neaterbits.displayserver.protocol.types;

public final class INT8 {

	private final byte value;

	public INT8(byte value) {
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
