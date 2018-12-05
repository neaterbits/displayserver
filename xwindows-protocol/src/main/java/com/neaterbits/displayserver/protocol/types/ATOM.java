package com.neaterbits.displayserver.protocol.types;

public final class ATOM {

    public static final ATOM None = new ATOM(0);
    
    private final int value;

    public ATOM(int value) {
        this.value = value;
    }

    public int getValue() {
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
        ATOM other = (ATOM) obj;
        if (value != other.value)
            return false;
        return true;
    }
}
