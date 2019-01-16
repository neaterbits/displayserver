package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class FontLoaderConfig {
    
    private final List<File> fontPaths;

    public FontLoaderConfig(List<String> fontPaths) {
        Objects.requireNonNull(fontPaths);

        this.fontPaths = Collections.unmodifiableList(fontPaths.stream()
                .map(string -> new File(string))
                .collect(Collectors.toList()));
    }
    
    List<File> getFontPaths() {
        return fontPaths;
    }
}
