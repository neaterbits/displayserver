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
}
