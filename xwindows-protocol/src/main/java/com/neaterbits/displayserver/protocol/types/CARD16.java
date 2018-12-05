package com.neaterbits.displayserver.protocol.types;

public final class CARD16 {

	private final int value;

	public CARD16(int value) {
		
		if (value < 0) {
			throw new IllegalArgumentException();
		}
		
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
