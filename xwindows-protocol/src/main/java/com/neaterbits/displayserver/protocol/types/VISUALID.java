package com.neaterbits.displayserver.protocol.types;

public final class VISUALID {

    public static final VISUALID None = new VISUALID(0);
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
        VISUALID other = (VISUALID) obj;
        if (value != other.value)
            return false;
        return true;
    }
}
