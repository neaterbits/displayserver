package com.neaterbits.displayserver.xwindows.fonts.model;

import java.util.Objects;

public final class XNamedFontModel {

    private final String name;
    private final XFontModel model;
    
    public XNamedFontModel(String fontName, XFontModel fontModel) {

        Objects.requireNonNull(fontName);
        Objects.requireNonNull(fontModel);
        
        this.name = fontName;
        this.model = fontModel;
    }

    public String getName() {
        return name;
    }

    public XFontModel getModel() {
        return model;
    }
}
