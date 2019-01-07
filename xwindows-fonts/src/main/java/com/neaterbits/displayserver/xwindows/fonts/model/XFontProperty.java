package com.neaterbits.displayserver.xwindows.fonts.model;

import java.util.Objects;

public abstract class XFontProperty {

    private final String name;

    public XFontProperty(String name) {
        
        Objects.requireNonNull(name);
        
        this.name = name;
    }

    public final String getName() {
        return name;
    }
}
