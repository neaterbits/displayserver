package com.neaterbits.displayserver.server;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;

public final class XConfig {

    private final DisplayAreaConfig displayAreaConfig;
    private final List<String> fontPaths;
    private final String colorsFile;

    public XConfig(DisplayAreaConfig displayAreaConfig, List<String> fontPaths, String colorsFile) {
        
        Objects.requireNonNull(displayAreaConfig);
        Objects.requireNonNull(fontPaths);
        Objects.requireNonNull(colorsFile);
        
        this.displayAreaConfig = displayAreaConfig;
        this.fontPaths = Collections.unmodifiableList(fontPaths);
        this.colorsFile = colorsFile;
    }

    DisplayAreaConfig getDisplayAreaConfig() {
        return displayAreaConfig;
    }

    List<String> getFontPaths() {
        return fontPaths;
    }

    String getColorsFile() {
        return colorsFile;
    }
}
