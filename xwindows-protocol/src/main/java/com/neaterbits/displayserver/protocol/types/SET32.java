package com.neaterbits.displayserver.protocol.types;

public final class SET32 {

	private final int value;

	public SET32(int value) {
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
