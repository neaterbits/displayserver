package com.neaterbits.displayserver.protocol.types;

public final class CURSOR extends RESOURCE {

    public static final CURSOR None = new CURSOR(0);

    public CURSOR(int value) {
        super(value);
    }
}
