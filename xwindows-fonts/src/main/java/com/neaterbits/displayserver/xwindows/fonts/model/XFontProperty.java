package com.neaterbits.displayserver.xwindows.fonts.model;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.ATOM;

public abstract class XFontProperty {

    private final ATOM name;

    public XFontProperty(ATOM name) {
        
        Objects.requireNonNull(name);
        
        this.name = name;
    }

    public final ATOM getName() {
        return name;
    }
}
