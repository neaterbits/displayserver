package com.neaterbits.displayserver.protocol.types;

import java.util.Objects;

public final class BITMASK {

	private final int value;

	public static boolean isSameMask(BITMASK bitmask1, BITMASK bitmask2, int flags) {
	    return (bitmask1.value & flags) == (bitmask2.value & flags);
	}
	
	public BITMASK(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public boolean isSet(int flag) {
        return (value & flag) != 0;
    }

	public BITMASK bitwiseOr(BITMASK other) {
	    
	    Objects.requireNonNull(other);
	    
	    return new BITMASK(value | other.value);
	}
	
    @Override
    public String toString() {
        return String.format("%08x", value);
    }
}
