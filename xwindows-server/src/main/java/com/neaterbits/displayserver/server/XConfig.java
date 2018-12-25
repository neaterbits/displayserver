package com.neaterbits.displayserver.server;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.windows.config.DisplayAreaConfig;

public final class XConfig {

    private final DisplayAreaConfig displayAreaConfig;
    private final List<String> fontPaths;

    public XConfig(DisplayAreaConfig displayAreaConfig, List<String> fontPaths) {
        
        Objects.requireNonNull(displayAreaConfig);
        Objects.requireNonNull(fontPaths);
        
        this.displayAreaConfig = displayAreaConfig;
        this.fontPaths = Collections.unmodifiableList(fontPaths);
    }

    DisplayAreaConfig getDisplayAreaConfig() {
        return displayAreaConfig;
    }

    List<String> getFontPaths() {
        return fontPaths;
    }
}
