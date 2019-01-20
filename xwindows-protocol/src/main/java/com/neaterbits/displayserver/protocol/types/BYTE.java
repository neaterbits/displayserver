package com.neaterbits.displayserver.protocol.types;

public final class BYTE {

	private final byte value;

	public BYTE(byte value) {
		this.value = value;
	}

	public byte getValue() {
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
        BYTE other = (BYTE) obj;
        if (value != other.value)
            return false;
        return true;
    }
}
