package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;
import com.neaterbits.displayserver.xwindows.fonts.FontLoaderConfig;

public final class XConfig {

    private final DisplayAreaConfig displayAreaConfig;
    private final FontLoaderConfig fontConfig;
    private final String colorsFile;

    public XConfig(DisplayAreaConfig displayAreaConfig, FontLoaderConfig fontConfig, String colorsFile) {
        
        Objects.requireNonNull(displayAreaConfig);
        Objects.requireNonNull(fontConfig);
        Objects.requireNonNull(colorsFile);
        
        this.displayAreaConfig = displayAreaConfig;
        this.fontConfig = fontConfig;
        this.colorsFile = colorsFile;
    }

    DisplayAreaConfig getDisplayAreaConfig() {
        return displayAreaConfig;
    }

    FontLoaderConfig getFontConfig() {
        return fontConfig;
    }

    String getColorsFile() {
        return colorsFile;
    }
}
