package com.neaterbits.displayserver.protocol.types;

public final class SETofDEVICEEVENT {

    public static final int UNUSED = 0xFFFFC0B0;
    
    private final int value;

    public SETofDEVICEEVENT(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%08x", value);
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
        SETofDEVICEEVENT other = (SETofDEVICEEVENT) obj;
        if (value != other.value)
            return false;
        return true;
    }
}
