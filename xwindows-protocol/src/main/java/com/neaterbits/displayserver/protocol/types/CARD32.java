package com.neaterbits.displayserver.protocol.types;

public final class CARD32 {

	private final long value;

	public CARD32(long value) {
		
		if (value < 0) {
			throw new IllegalArgumentException();
		}
		
		this.value = value;
	}

	public long getValue() {
		return value;
	}

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
