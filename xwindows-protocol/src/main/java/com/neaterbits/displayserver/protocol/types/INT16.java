package com.neaterbits.displayserver.protocol.types;

public final class INT16 {

	private final short value;

	public INT16(short value) {
		this.value = value;
	}

	public short getValue() {
		return value;
	}

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
