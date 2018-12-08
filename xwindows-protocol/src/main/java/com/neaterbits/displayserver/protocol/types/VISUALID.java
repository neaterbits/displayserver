package com.neaterbits.displayserver.protocol.types;

public final class VISUALID {

    public static final VISUALID CopyFromParent = new VISUALID(0);
    
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
