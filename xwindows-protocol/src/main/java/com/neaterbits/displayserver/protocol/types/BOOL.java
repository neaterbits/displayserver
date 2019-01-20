package com.neaterbits.displayserver.protocol.types;

public final class BOOL {

    public static final BOOL True = new BOOL(true);
    public static final BOOL False = new BOOL(false);
    
	private final byte value;

	public static BOOL valueOf(boolean value) {
	    return value ? True : False;
	}
	
	public BOOL(boolean value) {
	    this.value = value ? (byte)1 : (byte)0;
	}

	public BOOL(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

	public boolean isSet() {
	    return value != 0;
	}
	
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BOOL other = (BOOL) obj;
        if (value != other.value)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return value != 0 ? "true" : "false";
    }
}
