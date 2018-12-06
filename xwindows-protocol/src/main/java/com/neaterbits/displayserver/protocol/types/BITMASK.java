package com.neaterbits.displayserver.protocol.types;

public final class BITMASK {

	private final int value;

	public BITMASK(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

    @Override
    public String toString() {
        return String.format("%08x", value);
    }
}
