package com.neaterbits.displayserver.xwindows.fonts.model;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.ATOM;

public final class XFontStringProperty extends XFontProperty {

    private final String value;

    public XFontStringProperty(ATOM name, String value) {
        super(name);
    
        Objects.requireNonNull(value);

        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
