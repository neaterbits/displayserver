package com.neaterbits.displayserver.protocol.types;

public final class TIMESTAMP {

    private final long value;

    public TIMESTAMP(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
