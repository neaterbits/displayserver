package com.neaterbits.displayserver.xwindows.fonts;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class FontLoaderConfig {
    
    private final List<File> fontPaths;
    private final String baseFontsAliasFile;

    public FontLoaderConfig(List<String> fontPaths) {
        this(fontPaths, null);
    }

    public FontLoaderConfig(List<String> fontPaths, String baseFontsAliasFile) {
        
        Objects.requireNonNull(fontPaths);

        this.fontPaths = Collections.unmodifiableList(fontPaths.stream()
                .map(string -> new File(string))
                .collect(Collectors.toList()));
        
        this.baseFontsAliasFile = baseFontsAliasFile;
    }
    
    List<File> getFontPaths() {
        return fontPaths;
    }

    String getBaseFontsAliasFile() {
        return baseFontsAliasFile;
    }
}
