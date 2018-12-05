package com.neaterbits.displayserver.protocol.types;

public final class VISUALID {

	private final int value;

	public VISUALID(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
