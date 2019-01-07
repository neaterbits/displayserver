package com.neaterbits.displayserver.xwindows.fonts.model;

public final class XFontIntegerProperty extends XFontProperty {

    private final int value;

    public XFontIntegerProperty(String name, int value) {
        super(name);
    
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
