package com.neaterbits.displayserver.xwindows.fonts.model;

import com.neaterbits.displayserver.protocol.types.ATOM;

public final class XFontIntegerProperty extends XFontProperty {

    private final int value;

    public XFontIntegerProperty(ATOM name, int value) {
        super(name);
    
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
