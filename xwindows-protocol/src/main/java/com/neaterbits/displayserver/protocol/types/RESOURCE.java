package com.neaterbits.displayserver.protocol.types;

public abstract class RESOURCE {

    public static final WINDOW None = new WINDOW(0);

	private final int value;

	public RESOURCE(int value) {
		this.value = value;
	}
	
	public final int getValue() {
		return value;
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
		RESOURCE other = (RESOURCE) obj;
		if (value != other.value)
			return false;
		return true;
	}

    @Override
    public String toString() {
        return value == 0 ? "None" : String.format("%08x", value);
    }
}
