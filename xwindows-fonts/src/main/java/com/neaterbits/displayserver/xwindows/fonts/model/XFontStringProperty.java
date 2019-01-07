package com.neaterbits.displayserver.xwindows.fonts.model;

import java.util.Objects;

public final class XFontStringProperty extends XFontProperty {

    private final String value;

    public XFontStringProperty(String name, String value) {
        super(name);
    
        Objects.requireNonNull(value);

        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
