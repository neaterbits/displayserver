package com.neaterbits.displayserver.protocol.types;

public final class CARD8 {

	private final short value;

	public CARD8(short value) {
		
		if (value < 0) {
			throw new IllegalArgumentException();
		}
		
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
