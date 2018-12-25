package com.neaterbits.displayserver.protocol.types;

public final class FONTABLE extends RESOURCE {

    public FONTABLE(int value) {
        super(value);
    }
    
    public FONT toFontResource() {
        return new FONT(getValue());
    }

    public GCONTEXT toGCResource() {
        return new GCONTEXT(getValue());
    }
}
